(ns gol.client
  (:require [reagent.core :as r :refer [atom]]))

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

(def main-component
  (let [c 300
        t 250
        w 25
        h 25
        s (atom {:gen (set (take c (distinct (repeatedly (fn [] [(rand-int w) (rand-int h)])))))})]
    (fn []
      (let [c (into [:ul.cell-area] (map row (populate (empty-board w h) (:gen @s))))]
        (js/setTimeout #(swap! s update-in [:gen] (comp set (filter-on-board w h) step)) t)
        c))))

(r/render-component [main-component] (js/document.getElementById "app"))
