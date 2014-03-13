(ns gol.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r :refer [atom]]
            [cljs.core.async :as async :refer [chan timeout <! put!]]
            [gol.client.bl :refer [create-state create-population-state create-channels]]
            [gol.client.ui :refer [main-component control-component]]))

(def state (create-state))
(def channels (create-channels))

(r/render-component [main-component state channels] (js/document.getElementById "app"))
(r/render-component [control-component state channels] (js/document.getElementById "control"))

(go (while true
      (let [{v :msg :as msg} (<! (:actions channels))]
        (case v
          :pause (swap! state update-in [:pause] not)
          :random (swap! state update-in [:universe :population] (fn [_] (let [s @state]
                                                                 (create-population-state
                                                                   (get-in s [:generator :count])
                                                                   (get-in s [:viewport :width])
                                                                   (get-in s [:viewport :height])))))
          :clean (swap! state update-in [:universe :population] (fn [_] #{}))
          :toggle (swap! state update-in [:universe :population] (fn [p] (let [{loc :loc} msg] (if (p loc) (disj p loc) (conj p loc)))))
          :evolution (swap! state update-in [:evolution :period] (fn [_] (:period msg)))
          :universe (swap! state update-in [:universe :type] (fn [_] (:type msg)))
          :count (swap! state update-in [:generator :count] (fn [_] (let [s @state
                                                                          w (get-in s [:viewport :width])
                                                                          h (get-in s [:viewport :height])
                                                                          c (:count msg)
                                                                          m (* w h)] (if (<= c m) c m))))))))
