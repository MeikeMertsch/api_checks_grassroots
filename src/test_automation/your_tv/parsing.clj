(ns test-automation.your-tv.parsing
  (:require [cheshire.core :as json]
            [test-automation.core :as core]
            [test-automation.your-tv.paths :as p]
            [test-automation.constants.urls :as urls]
            [test-automation.constants.general :as const]
            [clj-time.coerce :as ctime]
            [com.rpl.specter :as specter]))


(defn sections [session-id]
  (->> session-id
       (str "Bearer ")
       (#(core/html-get urls/your-tv {:headers {"Authorization" %}}))
       :body
       (#(json/parse-string % const/transform-to-keywords))
       :sections))

(defn subsection-ids [section-id sections]
  (specter/select (p/secs-->sub-ids section-id) sections))

(defn section-ids [sections]
  (specter/select p/-->ids sections))

(defn subsections [sections]
  (specter/select (p/secs-->subs const/my-tv) sections))

(defn popular-subsection [sections]
  (specter/select-one (p/secs-->sub const/my-tv const/popular) sections))

(defn live-subsection [sections]
  (specter/select-one (p/secs-->sub const/my-tv const/live) sections))

(defn featured-subsections [sections]
  (specter/select (p/secs-->filtered-subs const/my-tv #{const/popular const/live}) sections))

(defn airings [subsection]
  (specter/select p/sub-->airs subsection))

(defn- airing-time [which airing]
  (->> (specter/select-one (p/air-->time which) airing)
       (* const/millis)
       ctime/from-long))

(defn starting-time [airing]
  (airing-time :startUnixtime airing))

(defn stop-time [airing]
  (airing-time :stopUnixtime airing))

(defn live-rights [airing]
  (specter/select-one p/airing-->live-rights airing))