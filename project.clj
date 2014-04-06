(defproject gol "0.1.0-SNAPSHOT"
  :description "Conway's Game of Life"
  :url "http://pavel-v-chernykh.github.io/gol/"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/google-closure-library-third-party "0.0-20130212-95c19e7f0f5f"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [reagent "0.4.2"]
                 [hiccups "0.3.0"]
                 [garden "1.1.5"]]
  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-garden "0.1.8"]]
  :hooks [leiningen.cljsbuild]
  :profiles {:dev  {:source-paths ["dev"]
                    :cljsbuild    {:builds [{:id           "react"
                                             :source-paths ["src/gol/core"
                                                            "src/gol/react"
                                                            "dev/gol/react"]
                                             :compiler     {:output-to     "resources/public/js/gol/react/react.js"
                                                            :output-dir    "resources/public/js/gol/react"
                                                            :target        :nodejs
                                                            :pretty-print  true
                                                            :optimizations :none
                                                            :source-map    "resources/public/js/gol/react/react.js.map"}}]}
                    :garden       {:builds [{:id         "react"
                                             :stylesheet gol.react.styles/styles
                                             :compiler   {:output-to     "resources/public/css/react.css"
                                                          :pretty-print? true
                                                          :vendors       ["webkit" "moz" "o" "ms"]}}]}}}
  :aliases {"dev"  ["do" ["clean"] ["cljsbuild" "once"] ["garden" "once"]]
            "prod" ["do" ["clean"] ["with-profile" "prod" "cljsbuild" "once"] ["with-profile" "prod" "garden" "once"]]})
