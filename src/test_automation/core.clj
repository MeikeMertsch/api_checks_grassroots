(ns test-automation.core
  (:require [clj-http.client :as client]
            [test-automation.constants.urls :as urls]))


(defn html-get
  ([url] (html-get url nil))
  ([url params] (client/get (str urls/env-integration url) params)))

(defn html-post [url params]
  (client/post (str urls/env-integration url) params))
