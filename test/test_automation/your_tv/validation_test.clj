(ns test-automation.your-tv.validation-test
  (:require [expectations :refer :all]
            [test-automation.constants.general :as con]
            [test-automation.your-tv.validation :as val]
            [test-automation.your-tv.parsing :as ytv]
            [test-automation.your-tv.paths :as p]
            [test-automation.your-tv.meta.generators :as gen]
            [test-automation.your-tv.meta.time :as time]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as tcg]
            [clojure.test.check.properties :as pro]
            [clj-time.core :as t]))

(defn check [pred generator]
  (tc/quick-check 100 (pro/for-all [value generator] (pred value))))


;;; Validate Subsections
(let [coll-with-either-popular-or-live (gen/combine-and-shuffle (tcg/elements [con/live con/popular])
                                                                (tcg/vector tcg/string 0 10))]
  (expect {:result true} (in (check val/contains-popular-or-live?
                                    coll-with-either-popular-or-live))))

(let [coll-without-popular-or-live (tcg/vector tcg/string)]
  (expect {:result true} (in (check (complement val/contains-popular-or-live?)
                                    coll-without-popular-or-live))))

(let [coll-with-both-popular-and-live (gen/combine-and-shuffle (tcg/shuffle [con/live con/popular])
                                                               (tcg/vector tcg/string 0 10))]
  (expect {:result true} (in (check (complement val/contains-popular-or-live?)
                                    coll-with-both-popular-and-live))))

;;; Maximum Of Airings In Subsections
(let [maximum (rand-int 12)
      small-subsection (->> (tcg/vector gen/random-airing 0 maximum)
                            gen/a-subsection-with)]
  (expect {:result true} (in (check (partial val/nil-or-max-n-airings? maximum)
                                    small-subsection))))

(let [maximum 7
      no-subsection (tcg/return nil)]
  (expect {:result true} (in (check (partial val/nil-or-max-n-airings? maximum)
                                    no-subsection))))

(let [maximum (rand-int 12)
      big-subsection (gen/a-subsection-with (tcg/vector gen/random-airing (inc maximum) 50))]
  (expect {:result true} (in (check (complement (partial val/nil-or-max-n-airings? maximum))
                                    big-subsection))))

;;; Airings Are Ordered Ascending
(let [ordered-airings (->> (tcg/vector gen/random-airing)
                           (tcg/fmap #(sort-by ytv/starting-time t/before? %)))]
  (expect {:result true} (in (check val/ordered-ascending?
                                    ordered-airings))))

(let [unordered-airings (->> (tcg/vector gen/random-airing 2 10)
                             (tcg/such-that #(not= (sort-by ytv/starting-time t/before? %) %)))]
  (expect {:result true} (in (check (complement val/ordered-ascending?)
                                    unordered-airings))))

;;; Airing Has Ended
(expect {:result true} (in (check val/ended? gen/past-airing)))
(expect {:result true} (in (check (complement val/ended?) gen/live-airing)))
(expect {:result true} (in (check (complement val/ended?) gen/future-airing)))

;;; Airings Are All Live Or In The Future
(let [live-and-future-airings (-> (tcg/one-of [gen/future-airing gen/live-airing])
                                  (tcg/vector 0 25))]
  (expect {:result true} (in (check val/only-live-or-future?
                                    live-and-future-airings))))

(let [airings-with-at-least-one-past-airing (->> (tcg/vector gen/random-airing 0 25)
                                                 (gen/combine-and-shuffle gen/past-airing))]
  (expect {:result true} (in (check (complement val/only-live-or-future?)
                                    airings-with-at-least-one-past-airing))))

;;; Section Never Has Less Than n Airings
(let [minimum 6
      min-airings (-> (tcg/vector gen/random-airing minimum 10)
                      gen/a-subsection-with)]
  (expect {:result true} (in (check (partial val/nil-or-min-n-airings? minimum)
                                    min-airings))))

(let [minimum 3
      no-subsection (tcg/return nil)]
  (expect {:result true} (in (check (partial val/nil-or-min-n-airings? minimum)
                                    no-subsection))))

(let [minimum 4
      max-minimum-airings (-> (tcg/vector gen/random-airing 0 (dec minimum))
                              gen/a-subsection-with)]
  (expect {:result true} (in (check (complement (partial val/nil-or-min-n-airings? minimum))
                                    max-minimum-airings))))

;;; Airing Is Available Live
(let [right-available-live (gen/update-generator [:available] (tcg/return true) gen/random-right)
      airing-available-live (gen/update-generator p/airing-->live-rights right-available-live gen/random-airing)]
  (expect {:result true} (in (check val/available-live?
                                    airing-available-live))))

(let [right-not-available-live (gen/update-generator [:available] (tcg/return false) gen/random-right)
      airing-not-available-live (gen/update-generator p/airing-->live-rights right-not-available-live gen/random-airing)]
  (expect {:result true} (in (check (complement val/available-live?)
                                    airing-not-available-live))))

;;; Featured Airings Are Max 3 Days Into The Future
(let [live-airings (tcg/vector gen/live-airing 1 5)]
  (expect {:result true} (in (check (partial val/max-n-days-into-the-future? 3)
                                    live-airings))))

(let [airings-with-one-min-4d-future (->> (tcg/fmap t/days (tcg/choose 5 7))
                                          (tcg/fmap (partial t/plus (t/today-at-midnight)))
                                          (tcg/fmap time/to-unix)
                                          (#(gen/update-generator (p/air-->time :startUnixtime) % gen/future-airing))
                                          (gen/combine-and-shuffle (tcg/vector gen/random-airing 1 5)))]
  (expect {:result true} (in (check (complement (partial val/max-n-days-into-the-future? 3))
                                    airings-with-one-min-4d-future))))