(ns gol.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r :refer [atom]]
            [cljs.core.async :as async :refer [chan timeout <! put!]]
            [gol.client.bl :refer [create-state rand-gen]]
            [gol.client.ui :refer [main-component control-component]]))

(def state (create-state))

(def c (chan))

(r/render-component [main-component state c] (js/document.getElementById "app"))
(r/render-component [control-component state c] (js/document.getElementById "control"))

(go (while true
      (let [{v :msg :as msg} (<! c)]
        (case v
          :pause (swap! state update-in [:pause] not)
          :random (swap! state update-in [:gen] (fn [_] (let [s @state] (rand-gen (:random-cell-count s) (get-in s [:board :width]) (get-in s [:board :height])))))
          :clean (swap! state update-in [:gen] (fn [_] #{}))
          :toggle (swap! state update-in [:gen] (fn [gen] (let [{loc :loc} msg] (if (gen loc) (disj gen loc) (conj gen loc)))))
          :timeout (swap! state update-in [:timeout :current] (fn [_] (:timeout msg)))
          :limit (swap! state update-in [:limit] (fn [_] (:limit msg)))
          :count (swap! state update-in [:random-cell-count] (fn [_] (let [s @state
                                                               w (get-in s [:board :width])
                                                               h (get-in s [:board :height])
                                                               c (:count msg)
                                                               m (* w h)] (if (<= c m) c m))))))))
