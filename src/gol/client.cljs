(ns gol.client
  (:require [reagent.core :as r]
            [gol.client.bl :refer [create-state create-channels start]]
            [gol.client.ui :refer [main-component control-component]]))

(def state (create-state))
(def channels (create-channels))

(r/render-component [main-component state channels] (js/document.getElementById "app"))
(r/render-component [control-component state channels] (js/document.getElementById "control"))

(start state channels)
