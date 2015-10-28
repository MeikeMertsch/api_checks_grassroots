(ns test-automation.your-tv.meta.validators
  (:require [schema.core :as s]
            [test-automation.your-tv.meta.structures :as struc]))


(def Right (struc/generate-right (repeat 6 s/Bool)))

(def Airing
  (-> (struc/generate-airing (s/eq "airing") [(repeat 6 s/Str)
                                              (repeat 4 s/Num)
                                              (repeat 1 s/Int)
                                              (repeat 3 s/Str)
                                              (repeat 2 Right)
                                              (repeat 1 s/Bool)])
      (update-in [:airing] #(update-in % [(s/optional-key :pgAge)] (constantly s/Int)))))

(def Subsection (struc/generate-subsection (s/eq "subsection") [(repeat 2 s/Str)
                                                                [Airing]]))

(def Fruitbox (struc/generate-fruitbox (s/eq "fruitbox-whole-screen")
                                       (repeat 2 s/Str)))

(def Section-Item (s/either Subsection Fruitbox))

(def Section (struc/generate-section [(repeat 2 s/Str)
                                      [Section-Item]]))