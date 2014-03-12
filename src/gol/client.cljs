(ns gol.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r :refer [atom]]
            [cljs.core.async :as async :refer [chan timeout <! put!]]))

(defn empty-board [w h] (vec (repeat w (vec (repeat h nil)))))

(defn populate
  [board living-cells]
  (reduce (fn [board coordinates] (assoc-in board coordinates :on))
          board
          living-cells))

(defn neighbours
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1]
        :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn stepper
  [neighbours birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

(def step (stepper neighbours #{3} #{2 3}))

(defn filter-on-board
  [bw bh]
  (partial
    filter
    (fn [[x y]] (and (< -1 x bw) (< -1 y bh)))))

(defn cell
  [cell]
  [:b.cell (when cell [:i])])

(defn row
  [row]
  (into [:li] (map cell row)))

(defn rand-gen
  [count width height]
  (set (take count (distinct (repeatedly (fn [] [(rand-int width) (rand-int height)]))))))

(def state (atom (let [count 300 width 30 height 30]
                   {:gen     (rand-gen count width height)
                    :count   count
                    :timeout 1100
                    :timeout-min 200
                    :timeout-max 2000
                    :width   width
                    :height  height
                    :pause   false})))

(def main-component
  (with-meta
    (fn [] (into [:ul.cell-area] (let [s @state] (map row (populate (empty-board (:width s) (:height s)) (:gen s))))))
    {:component-will-mount (fn [this]
                             (go (while true
                                   (let [s @state]
                                     (<! (timeout (:timeout s)))
                                     (when (not (:pause s))
                                       (swap! state update-in [:gen] (comp set (filter-on-board (:width s) (:height s)) step)))))))}))

(r/render-component [main-component] (js/document.getElementById "app"))

(def c (chan))

(def control-component
  (fn []
    [:div
     [:button {:on-click (fn [] (put! c {:msg :toggle}))} "Toggle"]
     [:button {:on-click (fn [] (put! c {:msg :reset}))} "Reset"]
     [:input {:on-change (fn [this] (put! c {:msg :timeout :timeout (aget this "target" "value")}))
              :type      :range
              :min       (:timeout-min @state)
              :max       (:timeout-max @state)
              :value     (:timeout     @state)}]]))

(go (while true
      (let [{v :msg :as msg} (<! c)]
        (case v
          :toggle (swap! state update-in [:pause] not)
          :reset (swap! state update-in [:gen] (fn [_] (let [s @state] (rand-gen (:count s) (:width s) (:height s)))))
          :timeout (swap! state update-in [:timeout] (fn [_] (:timeout msg)))))))


(r/render-component [control-component] (js/document.getElementById "control"))
