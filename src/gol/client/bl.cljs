(ns gol.client.bl
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r :refer [atom]]
            [cljs.core.async :as async :refer [chan timeout <! put!]]))

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

(defn empty-board [w h] (vec (repeat w (vec (repeat h nil)))))

(defn filter-on-board
  [bw bh coll]
  (filter (fn [[x y]] (and (< -1 x bw) (< -1 y bh))) coll))

(defn populate
  [board living-cells]
  (reduce
    (fn [board coordinates] (assoc-in board coordinates :on))
    board
    living-cells))

(defn rand-2d
  [width height]
  (cons [(rand-int width) (rand-int height)] (lazy-seq (rand-2d width height))))

(defn rand-gen
  [count width height]
  (set (take count (distinct (rand-2d width height)))))

(defn create-state
  []
  (atom (let [count 500 width 30 height 30]
          {:gen               (rand-gen count width height)
           :random-cell-count count
           :timeout           {:current 500
                               :min     200
                               :max     2000
                               :step    100}
           :board             {:width  width
                               :height height}
           :limit             false
           :pause             false})))
