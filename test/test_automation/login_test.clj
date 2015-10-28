(ns test-automation.login-test
  (:require [expectations :refer :all]
            [test-automation.login :as login]
            [test-automation.constants.status-codes :refer :all]
            [test-automation.constants.users :refer :all]))

;;; Login
(expect {:status OK} (in (login/login default)))

(expect (more string? some?) (login/session-id default))