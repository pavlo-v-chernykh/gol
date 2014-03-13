(ns gol.client.ui
  (:require [cljs.core.async :as async :refer [put!]]
            [gol.client.bl :refer [filter-on-viewport]]))

(defn main-component
  [state channels]
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
             (when (p [x y]) [:i])]))))))

(defn control-component
  [state channels]
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
    [:button {:on-click (fn [] (put!
                                 (:actions channels)
                                 {:msg    :populate
                                  :count  (get-in @state [:generator :count])
                                  :width  (get-in @state [:viewport :width])
                                  :height (get-in @state [:viewport :height])}))} "Random"]
    [:button {:on-click (fn [] (put!
                                 (:actions channels)
                                 {:msg    :populate
                                  :count  0
                                  :width  0
                                  :height 0}))} "Clean"]]
   [:div
    [:input {:on-change (fn [this] (put! (:actions channels) {:msg :period :period (aget this "target" "value")}))
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
              :on-change (fn [] (put! (:actions channels) {:msg :type :type :unlimited}))} "Unlimited"]]
    [:div
     [:input {:type      :radio
              :checked   (= (get-in @state [:universe :type]) :limited)
              :name      :universe
              :on-change (fn [] (put! (:actions channels) {:msg :type :type :limited}))} "Limited"]]]
   [:div
    [:div "Visible: " (count (let [p (get-in @state [:universe :population])
                                   w (get-in @state [:viewport :width])
                                   h (get-in @state [:viewport :height])]
                               (filter-on-viewport w h p)))]
    [:div "All: " (count (get-in @state [:universe :population]))]]])
