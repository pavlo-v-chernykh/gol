(ns gol.client
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :as async :refer [chan timeout <! >! put! dropping-buffer]]
            [cljs.core :refer [clj->js]]
            [reagent.core :as r :refer [atom]]
            [clojure.browser.repl :as repl]))

(enable-console-print!)
;(repl/connect "http://localhost:9000/repl")

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
        w 50
        h 50
        s (atom {:gen (set (take c (distinct (repeatedly (fn [] [(rand-int w) (rand-int h)])))))})]
    (fn []
      (let [c (into [:ul.cell-area] (map row (populate (empty-board w h) (:gen @s))))]
        (js/setTimeout #(swap! s update-in [:gen] (comp set (filter-on-board w h) step)) 250)
        c))))

(r/render-component [main-component] (. js/document (getElementById "app")))
