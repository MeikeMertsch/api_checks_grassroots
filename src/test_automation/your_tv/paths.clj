(ns test-automation.your-tv.paths
  (:require [com.rpl.specter :as specter]))

(defn- append [& paths]
  (vec (apply concat paths)))

(defn secs-->subs [section-id]
  [specter/ALL #(= section-id (:id %)) :sectionItems specter/ALL])

(defn- subs-->sub [id]
  [#(= id (:id (:subsection %)))])

(defn secs-->sub [section-id subsection-id]
  (append (secs-->subs section-id) (subs-->sub subsection-id)))

(defn- filter-out [subs-ids]
  [#(not (contains? subs-ids (:id (:subsection %))))])

(defn secs-->filtered-subs [section-id subsection-ids]
  (append (secs-->subs section-id) (filter-out subsection-ids)))

(defn secs-->sub-ids [section-id]
  (let [sub-->id [:subsection :id]]
    (append (secs-->subs section-id) sub-->id)))

(def -->ids
  [specter/ALL :id])

(def sub-->airs
  [:subsection :items specter/ALL])

(defn air-->time [which]
  [:airing which])

(def airing-->live-rights
  [:airing :rights :live])