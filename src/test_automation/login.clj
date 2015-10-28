(ns test-automation.login
  (:require [cheshire.core :as cc]
            [test-automation.core :as core]
            [test-automation.constants.urls :as urls]))

(defn login [who]
  (core/html-post urls/login {:form-params  {:identity  (:oid who)
                                            :accessKey (:pw who)}
                             :content-type :json}))

(defn session-id [user]
  ((cc/parse-string (:body (login user))) "sessionId"))
