(ns test-automation.constants.users
  (:require [clojure.test.check.generators :as gen]
            [test-automation.your-tv.meta.generators :as gens]
            [test-automation.constants.general :as const]))

(defn sample [generator]
  (first (gen/sample generator)))

(def default-oid "HIDDEN")
(def default-pw "HIDDEN")
(def default {:oid default-oid
              :pw  default-pw})


(defn random []
  {:open-id   (str "test+" (sample (gens/string 6)) "@magine.com")
   :password  (sample (gens/string 10))
   :arguments {:country (sample (gen/elements [const/sweden
                                               const/germany
                                               const/uk]))}})
