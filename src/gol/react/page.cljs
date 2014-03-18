(ns gol.react.page
  (:require-macros [hiccups.core :refer [defhtml]])
  (:require [hiccups.runtime]
            [reagent.core :as r]
            [gol.core.state :refer [create-state]]
            [gol.core.chan :refer [create-channels]]
            [gol.react.components.viewport.main :refer [viewport-component]]
            [gol.react.components.control.main :refer [control-component]]))

(defhtml ^:export main
  [dev?]
  (let [state (atom @(create-state {:width  30
                                    :height 30
                                    :count  450
                                    :period 500
                                    :status :progress
                                    :type   :unlimited}))
        channels (create-channels)
        client? (exists? js/window)]
    "<!DOCTYPE html>"
    [:html
     [:head
      [:link {:rel  "stylesheet"
              :type "text/css"
              :href "//cdnjs.cloudflare.com/ajax/libs/normalize/3.0.0/normalize.min.css"}]
      [:link {:rel  "stylesheet"
              :type "text/css"
              :href "css/react/styles.css"}]]
     [:body
      [:div (seq [[:div] [:div]])]
      [:div#viewport (when-not client? (r/render-component-to-string [viewport-component state channels]))]
      [:div#control (when-not client? (r/render-component-to-string [control-component state channels]))]
      (when dev?
        '([:script {:type "text/javascript"
                    :src  "//fb.me/react-0.9.0.js"}]
          [:script {:type "text/javascript"
                    :src  "js/gol/react/goog/base.js"}]))
      [:script {:type "text/javascript"
                :src  "js/gol/react/react.js"}]
      (when (and dev? client?)
        [:script {:type "text/javascript"} "goog.require('gol.react.client')"])
      (when (or (and dev? client?) (not dev?))
        [:script {:type "text/javascript"} "gol.react.client.main()"])
      (when (and dev? (not client?))
        '([:script {:type "text/javascript"} "goog.require('gol.react.page')"]
          [:script {:type "text/javascript"} "window.onload=function(){document.write(gol.react.page.main(true))};"]))]]))
