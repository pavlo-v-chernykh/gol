(ns gol.react.page
  (:require-macros [hiccups.core :refer [defhtml]])
  (:require [hiccups.runtime]
            [reagent.core :as r]
            [gol.core.comp :refer [create-state create-channels]]
            [gol.react.components.viewport.main :refer [viewport-component]]
            [gol.react.components.control.main :refer [control-component]]))

(defhtml ^:export main
  []
  (let [state (atom @(create-state {:width 30
                                    :height 30
                                    :count 450
                                    :period 500
                                    :status :progress
                                    :type :unlimited}))
        channels (create-channels)]
    "<!DOCTYPE html>"
    [:html [:head
            [:link {:rel "stylesheet"
                    :type "text/css"
                    :href "css/react.css"}]]
     [:body [:div#viewport (r/render-component-to-string [viewport-component state channels])]
      [:div#control (r/render-component-to-string [control-component state channels])]
      (seq [[:script {:type "text/javascript"
                      :src  "js/react/react.js"}]
            [:script {:type "text/javascript"
                      :src  "js/gol/react/goog/base.js"}]
            [:script {:type "text/javascript"
                      :src  "js/gol/react/react.js"}]
            [:script {:type "text/javascript"} "goog.require('gol.react.client')"]
            [:script {:type "text/javascript"} "gol.react.client.main()"]])]]))
