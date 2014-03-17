(ns gol.react.components.control.main
  (:require [cljs.core.async :refer [put!]]
            [gol.core.bl :refer [filter-on-viewport]]))

(defn- change-evolution-status-handler
  [chan status]
  (fn []
    (put! chan {:msg :status :status (status {:stasis   :progress
                                              :progress :stasis})})))

(defn- random-population-handler
  [chan count width height]
  (fn [] (put! chan {:msg :repopulate :count count :width width :height height})))

(defn- evolve-population-handler
  [chan type width height population]
  (fn [] (put! chan {:msg :evolve :type type :width width :height height :population population})))

(defn- change-evolution-period-handler
  [chan]
  (fn [this] (put! chan {:msg :period :period (aget this "target" "value")})))

(defn- change-evolution-type-handler
  [chan type]
  (fn [] (put! chan {:msg :type :type type})))

(defn control-component
  [state {:keys [actions]}]
  (let [{{gc :count}               :generator
         {:keys [width height]}    :viewport
         {:keys [status period]}   :evolution
         {:keys [type population]} :universe} @state]
    [:div
     [:div
      [:button {:on-click (change-evolution-status-handler actions status)}
       (if (= status :stasis) "Play" "Pause")]
      [:button {:on-click (evolve-population-handler actions type width height population)}
       "Step"]
      [:button {:on-click (random-population-handler actions gc width height)}
       "Random"]
      [:button {:on-click (random-population-handler actions 0 0 0)}
       "Clean"]]
     [:div
      [:input {:type      :range :min 0 :max 1000 :step 100 :value period
               :on-change (change-evolution-period-handler actions)}]]
     [:div
      [:div
       [:input {:type      :radio :name :universe :checked (= type :unlimited)
                :on-change (change-evolution-type-handler actions :unlimited)}
        "Unlimited"]]
      [:div
       [:input {:type      :radio :name :universe :checked (= type :limited)
                :on-change (change-evolution-type-handler actions :limited)}
        "Limited"]]]
     [:div
      [:div "Visible: " (count (filter-on-viewport width height population))]
      [:div "All: " (count population)]]]))
