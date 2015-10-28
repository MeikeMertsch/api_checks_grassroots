(ns test-automation.your-tv.meta.time
  (:require [schema.core :as sc :include-macros true]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check :as tc]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [test-automation.your-tv.parsing :as ytv]))


;;; Helper Functions For Times
(defn to-unix [time]
  (quot (c/to-long time) 1000))

(defn future-time [offset]
  (to-unix (t/plus (t/now) (t/minutes offset))))

(defn past-time [offset]
  (to-unix (t/minus (t/now) (t/minutes offset))))


;;; Timing
(defn put-into-future [airing]
  (let [offset (inc (rand-int 10))]
    (-> airing
        (update-in [:airing :startUnixtime] (constantly (future-time offset)))
        (update-in [:airing :availableFromUnixtime] (constantly (future-time offset)))
        (update-in [:airing :stopUnixtime] (constantly (future-time (+ 5 offset)))))))

(defn put-into-past [airing]
  (let [offset (inc (rand-int 10))]
    (-> airing
        (update-in [:airing :startUnixtime] (constantly (past-time (+ 5 offset))))
        (update-in [:airing :availableFromUnixtime] (constantly (past-time (+ 5 offset))))
        (update-in [:airing :stopUnixtime] (constantly (past-time offset))))))

(defn put-live [airing]
  (let [offset (inc (rand-int 10))]
    (-> airing
        (update-in [:airing :startUnixtime] (constantly (past-time (+ 5 offset))))
        (update-in [:airing :availableFromUnixtime] (constantly (past-time (+ 5 offset))))
        (update-in [:airing :stopUnixtime] (constantly (future-time (+ 5 offset)))))))