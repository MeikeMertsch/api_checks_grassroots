(defproject test-automation "0.1.0-SNAPSHOT"
  :description "automation of checks against our API"
  :url "HIDDEN"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [expectations "2.1.2"]
                 [cheshire "5.5.0"]
                 [clj-http "2.0.0"]
                 [clj-time "0.11.0"]
                 [prismatic/schema "1.0.1"]
                 [org.clojure/test.check "0.8.2"]
                 [com.rpl/specter "0.7.1"]
                 [slingshot "0.12.2"]
                 [environ "1.0.1"]
                 [zookeeper-clj "0.9.1"]]
  :plugins [[lein-expectations "0.0.8"]
            [lein-environ "1.0.1"]]
  :main ^:skip-aot test-automation.core
  :target-path "target/%s"
  :profiles {:uberjar     {:aot :all}
             :integration {:env {:magine-env         "HIDDEN"
                                 :zookeeper-url      "HIDDEN"
                                 :memcache-url       "HIDDEN"
                                 :magine-secret-path "HIDDEN"}}
             :production  {:env {:magine-env         "HIDDEN"
                                 :zookeeper-url      "HIDDEN"
                                 :memcache-url       "HIDDEN"
                                 :magine-secret-path "HIDDEN"}}
             :dev-int {:env {:magine-env         "HIDDEN"
                             :zookeeper-url      "HIDDEN"
                             :memcache-url       "HIDDEN"
                             :magine-secret-path "HIDDEN"}}
             :dev-prod  {:env {:magine-env         "HIDDEN"
                               :zookeeper-url      "HIDDEN"
                               :memcache-url       "HIDDEN"
                               :magine-secret-path "HIDDEN"}}})
