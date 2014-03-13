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
  [bw bh coll]
  (filter (fn [[x y]] (and (< -1 x bw) (< -1 y bh))) coll))

(defn rand-gen
  [count width height]
  (set (take count (distinct (repeatedly (fn [] [(rand-int width) (rand-int height)]))))))

(defn cell
  [c x y cell]
  [:b.cell
   {:on-click (fn [] (put! c {:msg :toggle :loc [x y]}))}
   (when cell [:i])])

(defn row
  [c x row]
  (into [:li] (map-indexed (partial cell c x) row)))

(defn main-component
  [state c]
  (go (while true
        (<! (timeout (:timeout @state)))
        (let [s @state
              w (:width s)
              h (:height s)
              l (:limit s)]
          (when (not (:pause s))
            (let [ns (swap! state update-in [:gen] (if l (comp set (partial filter-on-board w h) step) step))]
              (when (empty? (:gen ns))
                (put! c {:msg :pause})))))))
  (fn [state c] (into [:ul.cell-area] (let [s @state
                              w (:width s)
                              h (:height s)
                              gen (:gen s)]
                          (map-indexed (partial row c) (populate (empty-board w h) (filter-on-board w h gen)))))))

(defn control-component
  [state c]
  (fn [state c]
    [:div
     [:div
      [:input {:value     (:count @state)
               :type      :number
               :min       1
               :max       (* (:width @state) (:height @state))
               :on-change (fn [this] (put! c {:msg :count :count (aget this "target" "value")}))}]]
     [:div
      [:button {:on-click (fn [] (put! c {:msg :pause}))} (if (:pause @state) "Play" "Pause")]
      [:button {:on-click (fn [] (put! c {:msg :random}))} "Random"]
      [:button {:on-click (fn [] (put! c {:msg :clean}))} "Clean"]]
     [:div
      [:input {:on-change (fn [this] (put! c {:msg :timeout :timeout (aget this "target" "value")}))
               :type      :range
               :min       (:timeout-min @state)
               :max       (:timeout-max @state)
               :step      (:timeout-step @state)
               :value     (:timeout @state)}]]
     [:div
      [:div
       [:input {:type      :radio
                :checked   (not (:limit @state))
                :name      :limit
                :on-change (fn [] (put! c {:msg :limit :limit false}))} "Unlimited"]]
      [:div
       [:input {:type      :radio
                :checked   (:limit @state)
                :name      :limit
                :on-change (fn [] (put! c {:msg :limit :limit true}))} "Limited"]]]
     [:div
      [:div "Visible: " (count (let [gen (:gen @state)
                                     w (:width @state)
                                     h (:height @state)]
                                 (filter-on-board w h gen)))]
      [:div "Universe: " (count (:gen @state))]]]))

(def state (atom (let [count 500 width 30 height 30]
                   {:gen          (rand-gen count width height)
                    :count        count
                    :timeout      500
                    :timeout-min  200
                    :timeout-max  2000
                    :timeout-step 100
                    :width        width
                    :height       height
                    :limit        false
                    :pause        false})))

(def c (chan))

(r/render-component [main-component state c] (js/document.getElementById "app"))
(r/render-component [control-component state c] (js/document.getElementById "control"))

(go (while true
      (let [{v :msg :as msg} (<! c)]
        (case v
          :pause (swap! state update-in [:pause] not)
          :random (swap! state update-in [:gen] (fn [_] (let [s @state] (rand-gen (:count s) (:width s) (:height s)))))
          :clean (swap! state update-in [:gen] (fn [_] #{}))
          :toggle (swap! state update-in [:gen] (fn [gen] (let [{loc :loc} msg] (if (gen loc) (disj gen loc) (conj gen loc)))))
          :timeout (swap! state update-in [:timeout] (fn [_] (:timeout msg)))
          :limit (swap! state update-in [:limit] (fn [_] (:limit msg)))
          :count (swap! state update-in [:count] (fn [_] (let [s @state
                                                               w (:width s)
                                                               h (:height s)
                                                               c (:count msg)
                                                               m (* w h)] (if (<= c m) c m))))))))
