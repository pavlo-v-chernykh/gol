(ns gol.client.ui
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r :refer [atom]]
            [cljs.core.async :as async :refer [chan timeout <! put!]]
            [gol.client.bl :refer [filter-on-viewport step]]))

(defn main-component
  [state channels]
  (go (while true
        (<! (timeout (get-in @state [:evolution :period])))
        (let [s @state
              w (get-in s [:viewport :width])
              h (get-in s [:viewport :height])
              t (get-in s [:universe :type])]
          (when (= (get-in s [:evolution :status]) :progress)
            (let [ns (swap! state update-in [:universe :population] (if (= t :limited) (comp set (partial filter-on-viewport w h) step) step))]
              (when (empty? (get-in ns [:universe :population]))
                (put! (:actions channels) {:msg :status :status :stasis})))))))
  (fn [state channels]
    (let [s @state
          w (get-in s [:viewport :width])
          h (get-in s [:viewport :height])
          p (get-in s [:universe :population])
          c (:actions channels)]
      (into
        [:ul.cell-area]
        (for [x (range w)]
          (into
            [:li]
            (for [y (range h)]
              [:b.cell
               {:on-click (fn [] (put! c {:msg :toggle :loc [x y]}))}
               (when (p [x y]) [:i])])))))))

(defn control-component
  [state channels]
  (fn [state channels]
    [:div
     [:div
      [:input {:value     (get-in @state [:generator :count])
               :type      :number
               :min       1
               :max       (* (get-in @state [:viewport :width]) (get-in @state [:viewport :height]))
               :on-change (fn [this] (put! (:actions channels) {:msg :count :count (aget this "target" "value")}))}]]
     [:div
      [:button {:on-click (fn []
                            (case
                                (get-in @state [:evolution :status])
                              :stasis (put! (:actions channels) {:msg :status :status :progress})
                              :progress (put! (:actions channels) {:msg :status :status :stasis})))}
       (if (= (get-in @state [:evolution :status]) :stasis) "Play" "Pause")]
      [:button {:on-click (fn [] (put! (:actions channels) {:msg :random}))} "Random"]
      [:button {:on-click (fn [] (put! (:actions channels) {:msg :clean}))} "Clean"]]
     [:div
      [:input {:on-change (fn [this] (put! (:actions channels) {:msg :evolution :period (aget this "target" "value")}))
               :type      :range
               :min       200
               :max       2000
               :step      100
               :value     (get-in @state [:evolution :period])}]]
     [:div
      [:div
       [:input {:type      :radio
                :checked   (= (get-in @state [:universe :type]) :unlimited)
                :name      :universe
                :on-change (fn [] (put! (:actions channels) {:msg :universe :type :unlimited}))} "Unlimited"]]
      [:div
       [:input {:type      :radio
                :checked   (= (get-in @state [:universe :type]) :limited)
                :name      :universe
                :on-change (fn [] (put! (:actions channels) {:msg :universe :type :limited}))} "Limited"]]]
     [:div
      [:div "Visible: " (count (let [p (get-in @state [:universe :population])
                                     w (get-in @state [:viewport :width])
                                     h (get-in @state [:viewport :height])]
                                 (filter-on-viewport w h p)))]
      [:div "All: " (count (get-in @state [:universe :population]))]]]))
