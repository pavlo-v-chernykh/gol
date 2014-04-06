(ns gol.react.client
  (:require [reagent.core :as r :refer [atom]]
            [gol.core.act :refer [listen-channels watch-changes run-periods]]
            [gol.core.chan :refer [create-channels]]
            [gol.core.state :refer [create-state]]
            [gol.react.components.viewport.main :refer [viewport-component]]
            [gol.react.components.control.main :refer [control-component]]))

(defn ^:export main
  []
  (let [state (atom @(create-state {:width 30
                                    :height 30
                                    :count 450
                                    :period 500
                                    :status :progress
                                    :type :unlimited}))
        channels (create-channels)]

    (r/render-component [viewport-component state channels] (js/document.getElementById "viewport"))
    (r/render-component [control-component state channels] (js/document.getElementById "control"))

    (listen-channels state channels)
    (watch-changes state channels)
    (run-periods state channels)))
