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
  [state {:keys [loc population]}]
  (swap! state assoc-in [:universe :population] (if (population loc) (disj population loc) (conj population loc))))

(defn- change-period
  [state {:keys [period]}]
  (swap! state assoc-in [:evolution :period] period))

(defn- change-type
  [state {:keys [type]}]
  (swap! state assoc-in [:universe :type] type))

(defn- change-count
  [state {:keys [count]}]
  (swap! state assoc-in [:generator :count] count))

(defn- evolve
  [state {:keys [type width height population]}]
  (let [s (case type
            :limited (filtered-on-viewport-stepper width height)
            :unlimited step)]
    (swap! state assoc-in [:universe :population] (s population))))

(def ^:private actions-map
  {:status   change-status
   :populate populate
   :toggle   toggle-cell
   :period   change-period
   :type     change-type
   :count    change-count
   :evolve   evolve})

(defn process-actions
  [state {:keys [actions]}]
  (go (while true
        (let [{action :msg :as msg} (<! actions)]
          ((action actions-map) state msg)))))

(defn process-period
  [state {:keys [actions]}]
  (go (while true
        (<! (timeout (get-in @state [:evolution :period])))
        (let [{{:keys [width height]}    :viewport
               {:keys [type population]} :universe
               {:keys [status]}          :evolution} @state]
          (when (= status :progress)
            (put! actions {:msg :evolve :population population :width width :height height :type type}))))))

(defn- empty-population-watcher
  [{:keys [actions]}]
  (fn [key ref old new]
    (let [{{np :population} :universe
           {s :status}      :evolution} new]
      (if (and (empty? np) (= s :progress))
        (put! actions {:msg :status :status :stasis})))))

(defn process-changes
  [state channels]
  (add-watch state :empty-population (empty-population-watcher channels)))
