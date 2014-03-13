(ns gol.client.ui
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r :refer [atom]]
            [cljs.core.async :as async :refer [chan timeout <! put!]]
            [gol.client.bl :refer [filter-on-board populate empty-board step]]))

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
        (<! (timeout (get-in @state [:timeout :current])))
        (let [s @state
              w (get-in s [:board :width])
              h (get-in s [:board :height])
              l (:limit s)]
          (when (not (:pause s))
            (let [ns (swap! state update-in [:gen] (if l (comp set (partial filter-on-board w h) step) step))]
              (when (empty? (:gen ns))
                (put! c {:msg :pause})))))))
  (fn [state c] (into [:ul.cell-area] (let [s @state
                                            w (get-in s [:board :width])
                                            h (get-in s [:board :height])
                                            gen (:gen s)]
                                        (map-indexed (partial row c) (populate (empty-board w h) (filter-on-board w h gen)))))))

(defn control-component
  [state c]
  (fn [state c]
    [:div
     [:div
      [:input {:value     (:random-cell-count @state)
               :type      :number
               :min       1
               :max       (* (get-in @state [:board :width]) (get-in @state [:board :height]))
               :on-change (fn [this] (put! c {:msg :count :count (aget this "target" "value")}))}]]
     [:div
      [:button {:on-click (fn [] (put! c {:msg :pause}))} (if (:pause @state) "Play" "Pause")]
      [:button {:on-click (fn [] (put! c {:msg :random}))} "Random"]
      [:button {:on-click (fn [] (put! c {:msg :clean}))} "Clean"]]
     [:div
      [:input {:on-change (fn [this] (put! c {:msg :timeout :timeout (aget this "target" "value")}))
               :type      :range
               :min       (get-in @state [:timeout :min])
               :max       (get-in @state [:timeout :max])
               :step      (get-in @state [:timeout :step])
               :value     (get-in @state [:timeout :current])}]]
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
                                     w (get-in @state [:board :width])
                                     h (get-in @state [:board :height])]
                                 (filter-on-board w h gen)))]
      [:div "All: " (count (:gen @state))]]]))
