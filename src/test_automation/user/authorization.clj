(ns test-automation.user.authorization
  (:require [clojure.string :refer [split]]
            [environ.core :refer [env]]
            [cheshire.core :as json]
            [test-automation.constants.urls :as urls]
            [test-automation.constants.general :as const]))

(def zookeeper-url (env :zookeeper-url))
(def magine-secret-path (env :magine-secret-path))
(def magine-env (env :magine-env))
(def token "HIDDEN")

(def legacy-api-header
  {:user-agent (str "test_automation/1.0 (Token " token ") ")})

(def magine-secret
  (let [secrets-file (slurp urls/magine-secret)
        fragment (map keyword (split magine-secret-path #"\."))]
    (-> (json/parse-string secrets-file const/transform-to-keywords)
        (get-in fragment))))

(defn- auth-header [session-id is-internal]
  (if is-internal
    {:authorization (str "Bearer internal:" magine-secret)}
    (if session-id
      {:authorization (str "Bearer " session-id)}
      {})))

(defn make-headers [session_id is_internal]
  (let [header (auth-header session_id is_internal)
        legacy-header legacy-api-header]
    (-> (merge header legacy-header)
        (assoc :content-type "application/json"
               :x-test "test_automation"))))
