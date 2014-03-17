(ns gol.react.client
  (:require [reagent.core :as r :refer [atom]]
            [gol.core.act :refer [process-changes process-actions process-periods]]
            [gol.core.chan :refer [create-channels]]
            [gol.core.state :refer [create-state]]
            [gol.react.components.viewport.main :refer [viewport-component]]
            [gol.react.components.control.main :refer [control-component]]))

(defn ^:export main
  []
  (let [state (atom (create-state {:width  30
                                   :height 30
                                   :count  450
                                   :period 500
                                   :status :progress
                                   :type   :unlimited}))
        channels (create-channels)]

    (r/render-component [viewport-component state channels] (js/document.getElementById "viewport"))
    (r/render-component [control-component state channels] (js/document.getElementById "control"))

    (process-changes state channels)
    (process-actions state channels)
    (process-periods state channels)))
