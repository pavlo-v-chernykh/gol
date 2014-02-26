(ns gol.client
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :as async :refer [chan timeout <! >! put! dropping-buffer]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
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

(def app-state
  (atom {:gen (set (take 250 (distinct (partition 2 (repeatedly #(rand-int 50))))))}))

(defn cell
  [cell owner]
  (dom/b
    (if (om/value cell) #js {:className "cell"} nil)
    (if (om/value cell) (dom/i nil) nil)))

(defn row
  [row owner]
  (apply dom/li nil (om/build-all cell row)))

(om/root
  app-state
  (fn [app owner]
    (reify
      om/IInitState
      (init-state [_]
        {:timeout {:current 250}
         :board   {:width  50
                   :height 50}})
      om/IWillMount
      (will-mount [_]
        (go (while (not (om/get-state owner :stop))
              (<! (timeout (om/get-state owner [:timeout :current])))
              (let [w (om/get-state owner [:board :width])
                    h (om/get-state owner [:board :height])]
                (om/transact! app :gen (comp set (partial filter (fn [[x y]] (and (< -1 x w) (< -1 y h)))) step))))))
      om/IRender
      (render [_]
        (apply
          dom/ul #js {:className "cell-area"}
          (om/build-all
            row
            (let [w (om/get-state owner [:board :width])
                  h (om/get-state owner [:board :height])]
              (populate (empty-board w h) (om/value (:gen app)))))))))
  (. js/document (getElementById "app")))
