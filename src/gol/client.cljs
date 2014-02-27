(ns gol.client
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :as async :refer [chan timeout <! >! put! dropping-buffer]]
            [reagent.core :as reagent :refer [atom]]
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

(defn cell
  [cell]
  [:b.cell (when cell [:i])])

(defn row
  [row]
  [:li (map cell row)])

(def main-component
  (let [app (atom {:gen (set (take 500 (distinct (partition 2 (repeatedly #(rand-int 50))))))})]
    (with-meta
      (fn [] [:ul.cell-area (map row (populate (empty-board 50 50) (:gen @app)))])
      {:component-will-mount (fn [_] (go (while true
                                           (<! (timeout 0))
                                           (swap! app update-in [:gen] (comp set (partial filter (fn [[x y]] (and (< -1 x 50) (< -1 y 50)))) step)))))})))

(reagent/render-component
  [main-component]
  (. js/document (getElementById "app")))
