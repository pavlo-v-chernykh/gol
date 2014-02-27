(defproject gol "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.1" :exclusions [[ring/ring-jetty-adapter]]]
                 [compojure "1.1.1"]
                 [hiccup "1.0.5"]

                 [org.clojure/clojurescript "0.0-2156"]
                 [org.clojure/google-closure-library-third-party "0.0-20130212-95c19e7f0f5f"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [reagent "0.4.1"]
                 [com.facebook/react "0.8.0.1"]

                 [garden "1.1.5"]

                 [ring-serve "0.1.2" :exclusions [[ring/ring-devel]]]
                 [ring-mock "0.1.5"]

                 [ns-tracker "0.2.2"]]
  :plugins [[lein-cljsbuild "1.0.2"]
            [lein-garden "0.1.5"]
            [lein-ring "0.8.10"]]
  :ring {:handler gol.server/app}
  :profiles {:dev {:source-paths ["dev"]
                   :cljsbuild    {:builds [{:id           "gol"
                                            :source-paths ["src"]
                                            :compiler     {:output-to     "resources/public/js/build/gol.js"
                                                           :output-dir    "resources/public/js/build"
                                                           :optimizations :none}}]}
                   :garden       {:builds [{:id         "gol"
                                            :stylesheet gol.styles/styles
                                            :compiler   {:output-to "resources/public/css/build/styles.css"
                                                         :vendors ["webkit" "moz" "o" "ms"]}}]}}}
  :aliases {"build" ["do" ["cljsbuild" "once"] ["garden" "once"]]})
