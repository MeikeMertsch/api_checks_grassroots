(ns test-automation.your-tv-test
  (:require [expectations :refer :all]
            [test-automation.user.actions :as actions]
            [test-automation.your-tv.parsing :as your-tv]
            [test-automation.your-tv.validation :as val]
            [test-automation.your-tv.meta.validators :as schema]
            [test-automation.constants.users :as users]
            [test-automation.constants.general :as const]
            [schema.core :as s]))

(defn- create-random-user []
  (actions/create (users/random)))

(defn- with-random-user [f]
  (let [user (create-random-user)]
    (try
      (f (:sessionId user))
      (finally
        (actions/delete user)))))


;;; Invariant Answer From API
(expect (s/validate [schema/Section]
                    (with-random-user #(your-tv/sections %))))

;;; API Returns Error With Faked Session Id
(let [random-fake-session-id "FAKE-FAKE-FAKE"]
  (expect val/returns-forbidden?
          #(your-tv/sections random-fake-session-id)))

(let [user (create-random-user)]
  (try
    (expect (complement val/returns-forbidden?)
            #(your-tv/sections (:sessionId user)))
    (finally (actions/delete user))))

;;; Fetch Sections on MyTV
(expect (and const/my-tv const/my-lineup)
        (in (with-random-user #(->> (your-tv/sections %)
                                    your-tv/section-ids))))

;;; I can see either a live section or a popular section
(expect val/contains-popular-or-live?
        (with-random-user #(->> (your-tv/sections %)
                                (your-tv/subsection-ids const/my-tv))))

;;; Max Three Airings Per Featured Subsection
(expect (partial every? (partial val/nil-or-max-n-airings? 3))
        (with-random-user #(->> (your-tv/sections %)
                                your-tv/featured-subsections)))

;;; Airings Are Ordered By Airing Date Ascending In A Featured Subsection
(expect (partial every? val/ordered-ascending?)
        (with-random-user #(->> (your-tv/sections %)
                                your-tv/featured-subsections
                                (map your-tv/airings))))

;;; Featured Subsections Don't Contain Past Airings
(expect (partial every? val/only-live-or-future?)
        (with-random-user #(->> (your-tv/sections %)
                                your-tv/featured-subsections
                                (map your-tv/airings))))

;;; Live Subsection Doesn't Contain Past Airings
(expect (partial val/only-live-or-future?)
        (with-random-user #(->> (your-tv/sections %)
                                your-tv/live-subsection
                                your-tv/airings)))

;;; Popular doesn't have less than 12 airings
(expect (partial val/nil-or-min-n-airings? 12)
        (with-random-user #(->> (your-tv/sections %)
                                your-tv/popular-subsection)))

;;; Popular has maximum 24 airings
(expect (partial val/nil-or-max-n-airings? 24)
        (with-random-user #(->> (your-tv/sections %)
                                your-tv/popular-subsection)))

;;; Airings That We Do Not Have Live Rights For Won't Show In Any Subsection
(expect (partial every? val/available-live?)
        (with-random-user #(->> (your-tv/sections %)
                                your-tv/subsections
                                (map your-tv/airings)
                                flatten)))
