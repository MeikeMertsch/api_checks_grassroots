(ns test-automation.constants.urls
  (:require [environ.core :refer [env]]))

(def env-integration (env :magine-env))

(def login "/login/v1/auth/magine")
(def health "/health")
(def your-tv "/your-tv/v1/sections")
(def users "/user/v1/users/")
(def magine-secret "HIDDEN")
