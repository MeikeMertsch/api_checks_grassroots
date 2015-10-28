(ns test-automation.your-tv.validation
  (:require [test-automation.your-tv.parsing :as your-tv]
            [test-automation.constants.general :as const]
            [clj-time.core :as t]
            [slingshot.slingshot :as sling]
            [test-automation.constants.status-codes :as status]))

(defn contains-popular-or-live? [coll]
  (->> coll
       (filter #(contains? #{const/popular const/live} %))
       count
       (= 1)))

(defn compare-airing-count [fn number subsection]
  (if subsection
    (->> subsection
         your-tv/airings
         count
         (fn number))
    true))

(defn nil-or-max-n-airings? [number subsection]
  (compare-airing-count >= number subsection))

(defn nil-or-min-n-airings? [number subsection]
  (compare-airing-count <= number subsection))

(defn ordered-ascending? [airings]
  (let [starting-times (map your-tv/starting-time airings)]
    (= starting-times (sort starting-times))))

(defn ended? [airing]
  (t/after? (t/now) (your-tv/stop-time airing)))

(defn only-live-or-future? [airings]
  (every? (complement ended?) airings))

(defn available-live? [airing]
  (->> airing
       your-tv/live-rights
       :available))

(defn returns-error? [f error]
  (let [returns-error true
        returns-something-else false]
    (sling/try+
      (f)
      returns-something-else
      (catch [:status error] {:keys [_ _ _]}
        returns-error))))

(defn returns-forbidden? [f]
  (returns-error? f status/FORBIDDEN))

(defn max-n-days-into-the-future? [number airings]
  (->> (map your-tv/starting-time airings)
       (map (partial t/after? (t/plus (t/today-at-midnight) (t/days (inc number)))))
       (every? true?)))
