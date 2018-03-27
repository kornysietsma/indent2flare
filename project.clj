(defproject indent2flare "1.0.0"
  :description "Simple tool to add file indentation statistics to a d3 json file"
  :url "https://github.com/kornysietsma/indent2flare"
  :license {:name "Apache 2.0"
            :url  "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [cheshire "5.8.0"]
                 [com.stuartsierra/frequencies "0.1.0"]]
  :main indent2flare.cli
  :uberjar-name "indent2flare.jar"
  :profiles {
             :uberjar {:aot :all}
             :dev     {:dependencies [[midje "1.9.1"]]}
             }
  :plugins [[lein-midje "3.2"]]
  )
