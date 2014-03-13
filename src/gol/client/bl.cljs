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

(defn filter-on-viewport
  [bw bh coll]
  (filter (fn [[x y]] (and (< -1 x bw) (< -1 y bh))) coll))

(defn rand-2d
  [width height]
  (cons [(rand-int width) (rand-int height)] (lazy-seq (rand-2d width height))))

(defn rand-population
  [count width height]
  (set (take count (distinct (rand-2d width height)))))

(defn create-channels
  []
  {:actions (chan)})

(defn create-state
  [& {:keys [width height count period status type]
      :or   {width  30
             height 30
             count  250
             period 500
             status :progress
             type   :unlimited}}]
  (atom {:universe  {:population (rand-population count width height)
                     :type       type}
         :evolution {:period period
                     :status status}
         :generator {:count count}
         :viewport  {:width  width
                     :height height}}))

(defn start
  [state channels]
  (go (while true
        (let [{v :msg :as msg} (<! (:actions channels))]
          (case v
            :status (swap! state update-in [:evolution :status] #(:status msg))
            :random (swap! state update-in [:universe :population] (fn [_] (let [s @state]
                                                                             (rand-population
                                                                               (get-in s [:generator :count])
                                                                               (get-in s [:viewport :width])
                                                                               (get-in s [:viewport :height])))))
            :clean (swap! state update-in [:universe :population] empty)
            :toggle (swap! state update-in [:universe :population] (fn [p] (let [{loc :loc} msg] (if (p loc) (disj p loc) (conj p loc)))))
            :evolution (swap! state update-in [:evolution :period] #(:period msg))
            :universe (swap! state update-in [:universe :type] #(:type msg))
            :count (swap! state update-in [:generator :count] #(let [s @state
                                                                     w (get-in s [:viewport :width])
                                                                     h (get-in s [:viewport :height])
                                                                     c (:count msg)
                                                                     m (* w h)] (if (<= c m) c m))))))))
