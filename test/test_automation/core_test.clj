(ns test-automation.core-test
  (:require [expectations :refer :all]
            [test-automation.core :as tac]
            [test-automation.constants.status-codes :as sc]
            [test-automation.constants.urls :as urls]))


;;; General health check
(expect {:status sc/OK} (in (tac/html-get urls/health)))
