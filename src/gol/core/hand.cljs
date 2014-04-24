(ns gol.core.hand
  (:require [gol.core.bl :refer [rand-population step filtered-on-viewport-stepper]]))


(defn change-status
  [state {:keys [status]}]
  (swap! state assoc-in [:evolution :status] status))

(defn repopulate
  [state {:keys [count]}]
  (let [{{:keys [width height]} :viewport} @state]
    (swap! state assoc-in [:universe :population] (set (take count (rand-population width height))))))

(defn toggle-cell
  [state {:keys [loc]}]
  (let [{{population :population} :universe} @state]
    (swap! state assoc-in [:universe :population] (if (population loc) (disj population loc) (conj population loc)))))

(defn change-period
  [state {:keys [period]}]
  (swap! state assoc-in [:evolution :period] period))

(defn change-type
  [state {:keys [type]}]
  (swap! state assoc-in [:universe :type] type))

(defn change-count
  [state {:keys [count]}]
  (swap! state assoc-in [:generator :count] count))

(defn evolve
  [state]
  (let [{{:keys [type population]} :universe
         {:keys [width height]}    :viewport
         {:keys [status]}          :evolution} @state
        step (case type
               :limited (filtered-on-viewport-stepper width height)
               :unlimited step)
        new-population (step population)]
    (when (and (empty? new-population) (= status :progress))
      (change-status state {:status :stasis}))
    (swap! state assoc-in [:universe :population] new-population)))
