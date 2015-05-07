(defproject gol "0.1.0-SNAPSHOT"
            :description "Conway's Game of Life"
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-3211"]
                           [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                           [reagent "0.5.0"]
                           [hiccups "0.3.0"]
                           [garden "1.2.5"]]
            :plugins [[lein-cljsbuild "1.0.5"]
                      [lein-figwheel "0.3.1"]
                      [lein-garden "0.2.5"]]
            :hooks [leiningen.cljsbuild]
            :profiles {:dev {:cljsbuild {:builds [{:id           :gol
                                                   :source-paths ["src"]
                                                   :figwheel     {:on-jsload "gol.ui.client/on-js-reload"}
                                                   :compiler     {:output-to     "out/js/gol/gol.js"
                                                                  :output-dir    "out/js/gol"
                                                                  :asset-path    "out/js/gol"
                                                                  :pretty-print  false
                                                                  :main          gol.ui.client
                                                                  :optimizations :none}}]}
                             :garden    {:builds [{:id           :gol
                                                   :stylesheet   gol.ui.styles/styles
                                                   :source-paths ["src/ui/"]
                                                   :compiler     {:output-to     "out/css/gol.css"
                                                                  :pretty-print? false
                                                                  :vendors       ["webkit" "moz" "o" "ms"]}}]}}}
            :aliases {"dev" ["do" ["clean"] ["cljsbuild" "once"] ["garden" "once"]]})
