(ns test-automation.your-tv.meta.generators
  (:require [clojure.test.check.generators :as gen]
            [test-automation.your-tv.meta.time :as time]
            [test-automation.your-tv.meta.structures :as struc]))

(defn- cache-expensive-data [generator]
  (let [cache (gen/sample generator 100)]
    (gen/elements cache)))

(def random-time
  (gen/one-of [(gen/fmap time/future-time gen/nat)
               (gen/fmap time/past-time gen/nat)]))

(def random-right
  (->> (gen/fmap struc/generate-right (gen/vector gen/boolean 6))
       cache-expensive-data))

(def random-link
  (gen/fmap struc/generate-link gen/nat))

(def random-airing
  (->> (gen/fmap (partial struc/generate-airing "airing") (gen/tuple (gen/vector gen/string-alphanumeric 6)
                                                                     (gen/vector random-time 4)
                                                                     (gen/vector gen/nat 1)
                                                                     (gen/vector random-link 3)
                                                                     (gen/vector random-right 2)
                                                                     (gen/vector gen/boolean 1)))
       cache-expensive-data))

(def future-airing
  (gen/fmap time/put-into-future random-airing))

(def past-airing
  (gen/fmap time/put-into-past random-airing))

(def live-airing
  (gen/fmap time/put-live random-airing))

(def random-subsection
  (->> (gen/fmap (partial struc/generate-subsection "subsection")
                 (gen/tuple (gen/vector gen/string-alphanumeric 2)
                            (gen/vector random-airing 0 25)))
       cache-expensive-data))

(defn- update-item [path [value item]]
  (update-in item path (constantly value)))

(defn update-generator [path value-gen item-gen]
  (gen/fmap (partial update-item path) (gen/tuple value-gen item-gen)))

(defn a-subsection-with [airings]
  (update-generator [:subsection :items] airings random-subsection))

(defn combine-and-shuffle [& generators]
  (->> (apply gen/tuple generators)
       (gen/fmap flatten)
       (gen/fmap shuffle)))

(defn string [length]
  (->> (gen/vector gen/char-alpha-numeric length)
       (gen/fmap #(apply str %))))