(ns test-automation.user.actions
  (:require [test-automation.constants.general :as const]
            [test-automation.constants.urls :as urls]
            [test-automation.user.authorization :as auth]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [zookeeper :as zk]))

;;; delete users
(defn- contact-zookeeper [path client]
  (->> path
       (zk/children client)
       first
       (#(zk/data client (str path "/" %)))
       :data
       (String.)
       (#(json/parse-string % const/transform-to-keywords))
       :serviceEndpoint
       (#(str "http://" (:host %) ":" (:port %)))))

(defn- zookeeper-address [path]
  (let [client (zk/connect auth/zookeeper-url)]
    (try
      (contact-zookeeper path client)
      (finally (zk/close client)))))

(defn- http-delete [path _ headers host]
  (println (str "delete " host path " with " nil " " headers))
  (client/delete (str host path) {:headers headers}))

(defn- internal-delete [service path json-data]
  (let [headers (auth/make-headers nil true)
        host (zookeeper-address (str "HIDDEN" service))]
    (http-delete path json-data headers host)))

(defn delete [user]
  (let [user-id (:userId user)
        path (str urls/users user-id)]
    (if (.endsWith user-id "XYZ")                           ; security clause to not accidentially delete random users from prod!
      (internal-delete "user" path nil))))


;;; create users
(defn- update-user-map [old new]
  (loop [old (update-in old [:tags] concat (:tags new))
         new (dissoc new :tags)]
    (let [traverser (first (keys new))]
      (if (empty? new)
        old
        (recur (assoc old traverser (traverser new))
               (dissoc new traverser))))))

(defn- default-user [mail-address password]
  {:identity  mail-address
   :accessKey password
   :email     mail-address
   :country   const/sweden
   :locale    const/swedish
   :name      const/default-name
   :tags      [const/test-tag]})                            ; security clause to avoid users being created that we have to pay for

(defn- create-request [mail-address password argument-map]
  {:headers        (auth/make-headers nil nil)
   :body           (-> (default-user mail-address password)
                       (update-user-map argument-map)
                       json/generate-string)
   :socket-timeout 1000
   :conn-timeout   1000})

(defn create [user]
  (let [mail-address (:open-id user)
        password (:password user)
        argument-map (:arguments user)]
    (-> (client/put (str auth/magine-env urls/login)
                    (create-request mail-address password argument-map))
        :body
        (json/parse-string true))))