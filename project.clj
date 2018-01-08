(defproject course-listing "0.1.0-SNAPSHOT"
  :description "Takes a CSV file that is delimited by semicolons and performs various analytics. This was a freelance job that had a very narrow scope."
  :url "https://github.com/keithharper/course-listing"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [datawalk "0.1.12"]]
  :main ^:skip-aot course-listing.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :user {:plugins [[lein-kibit "0.1.5"]] }})
