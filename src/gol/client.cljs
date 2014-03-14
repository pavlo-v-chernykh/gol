(ns gol.client
  (:require [reagent.core :as r]
            [gol.client.act :refer [process-changes process-actions process-period]]
            [gol.client.chan :refer [create-channels]]
            [gol.client.state :refer [create-state]]
            [gol.client.ui :refer [main-component control-component]]))

(def state (create-state))
(def channels (create-channels))

(r/render-component [main-component state channels] (js/document.getElementById "app"))
(r/render-component [control-component state channels] (js/document.getElementById "control"))

(process-changes state channels)
(process-actions state channels)
(process-period state channels)
