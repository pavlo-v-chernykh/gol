(defproject gol "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/google-closure-library-third-party "0.0-20130212-95c19e7f0f5f"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [reagent "0.4.1"]
                 [hiccups "0.3.0"]
                 [garden "1.1.5"]]
  :plugins [[lein-cljsbuild "1.0.2"]
            [lein-garden "0.1.8" :exclusions [rhino/js]]
            [lein-rhino "0.1.0-SNAPSHOT"]]
  :hooks [leiningen.cljsbuild]
  :profiles {:dev {:source-paths ["dev"]
                   :cljsbuild {:builds [{:id "react"
                                         :source-paths ["src/gol/core"
                                                        "src/gol/react"]
                                         :compiler {:output-to "resources/public/js/gol/react/react.js"
                                                    :output-dir "resources/public/js/gol/react"
                                                    :pretty-print true
                                                    :optimizations :none
                                                    :source-map "resources/public/js/gol/react/react.js.map"}}]}
                   :rhino {:builds [{:id "react"
                                     :src "resources/public/js/gol/react/react.js"
                                     :script "gol.react.page.main(true);"
                                     :output-to "resources/public/react.html"}]}
                   :garden {:builds [{:id "react"
                                      :stylesheet gol.react.styles/styles
                                      :compiler {:output-to "resources/public/css/react/styles.css"
                                                 :pretty-print? true
                                                 :vendors ["webkit" "moz" "o" "ms"]}}]}}
             :prod {:cljsbuild {:builds [{:id "react"
                                          :source-paths ["src/gol/core"
                                                         "src/gol/react"]
                                          :compiler {:output-to "resources/public/js/gol/react/react.js"
                                                     :output-dir "resources/public/js/gol/react"
                                                     :preamble ["reagent/react.min.js"]
                                                     :pretty-print false
                                                     :optimizations :advanced
                                                     :source-map "resources/public/js/gol/react/react.js.map"}}]}
                    :rhino {:builds [{:id "react"
                                      :src "resources/public/js/gol/react/react.js"
                                      :script "gol.react.page.main();"
                                      :output-to "resources/public/react.html"}]}
                    :garden {:builds [{:id "react"
                                       :stylesheet gol.react.styles/styles
                                       :compiler {:output-to "resources/public/css/react/styles.css"
                                                  :pretty-print? false
                                                  :vendors ["webkit" "moz" "o" "ms"]}}]}}}
  :aliases {"dev" ["do"
                   ["clean"]
                   ["with-profile" "prod" "cljsbuild" "once"]
                   ["rhino"]
                   ["clean"]
                   ["cljsbuild" "once"]
                   ["garden" "once"]]
            "prod" ["do"
                    ["clean"]
                    ["with-profile" "prod" "cljsbuild" "once"]
                    ["with-profile" "prod" "rhino"]
                    ["with-profile" "prod" "garden" "once"]]})
