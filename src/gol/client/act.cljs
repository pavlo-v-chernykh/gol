(ns gol.client.act
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [timeout <! put!]]
            [gol.client.bl :refer [rand-population step filtered-on-viewport-stepper]]))

(defn- change-status
  [state {:keys [status]}]
  (swap! state assoc-in [:evolution :status] status))

(defn- populate
  [state {:keys [count width height]}]
  (swap! state assoc-in [:universe :population] (set (take count (rand-population width height)))))

(defn- toggle-cell
  [state {:keys [loc]}]
  (swap! state update-in [:universe :population] (fn [p] (if (p loc) (disj p loc) (conj p loc)))))

(defn- change-period
  [state {:keys [period]}]
  (swap! state assoc-in [:evolution :period] period))

(defn- change-type
  [state {:keys [type]}]
  (swap! state assoc-in [:universe :type] type))

(defn- change-count
  [state {:keys [count]}]
  (swap! state assoc-in [:generator :count] count))

(def ^:private actions-map
  {:status   change-status
   :populate populate
   :toggle   toggle-cell
   :period   change-period
   :type     change-type
   :count    change-count})

(defn process-actions
  [state {:keys [actions]}]
  (go (while true
        (let [{action :msg :as msg} (<! actions)]
          ((action actions-map) state msg)))))

(defn process-period
  [state channels]
  (go (while true
        (<! (timeout (get-in @state [:evolution :period])))
        (let [s @state
              w (get-in s [:viewport :width])
              h (get-in s [:viewport :height])
              t (get-in s [:universe :type])]
          (when (= (get-in s [:evolution :status]) :progress)
            (let [ns (swap! state update-in [:universe :population] (if (= t :limited) (filtered-on-viewport-stepper w h) step))]
              (when (empty? (get-in ns [:universe :population]))
                (put! (:actions channels) {:msg :status :status :stasis}))))))))
