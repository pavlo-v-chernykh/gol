(ns gol.client.ui
  (:require [cljs.core.async :refer [put!]]
            [gol.client.bl :refer [filter-on-viewport]]))

(defn- toggle-cell-handler [chan x y population]
  (fn [] (put! chan {:msg :toggle :loc [x y] :population population})))

(defn app-component
  [state {:keys [actions]}]
  (let [{{:keys [width height]} :viewport
         {:keys [population]}   :universe} @state]
    (into [:ul.cell-area]
          (for [x (range width)]
            (into [:li]
                  (for [y (range height)]
                    [:b.cell {:on-click (toggle-cell-handler actions x y population)}
                     (when (population [x y]) [:i])]))))))



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
                                 {:msg    :repopulate
                                  :count  (get-in @state [:generator :count])
                                  :width  (get-in @state [:viewport :width])
                                  :height (get-in @state [:viewport :height])}))} "Random"]
    [:button {:on-click (fn [] (put!
                                 (:actions channels)
                                 {:msg    :repopulate
                                  :count  0
                                  :width  0
                                  :height 0}))} "Clean"]]
   [:div
    [:input {:on-change (fn [this] (put! (:actions channels) {:msg :period :period (aget this "target" "value")}))
             :type      :range
             :min       0
             :max       1000
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
