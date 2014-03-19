(ns gol.core.act
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :refer [timeout <! put!]]
            [gol.core.bl :refer [rand-population step filtered-on-viewport-stepper]]))

(defn- change-status
  [state {:keys [status]}]
  (swap! state assoc-in [:evolution :status] status))

(defn- repopulate
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
  (let [step (case type
               :limited (filtered-on-viewport-stepper width height)
               :unlimited step)]
    (swap! state assoc-in [:universe :population] (step population))))

(def ^:private actions-map
  {:status     change-status
   :repopulate repopulate
   :toggle     toggle-cell
   :period     change-period
   :type       change-type
   :count      change-count
   :evolve     evolve})

(defn- run-action
  [state {action-key :msg :as msg}]
  (let [{action action-key} actions-map]
    (action state msg)))

(defn listen-channels
  [state {:keys [actions periods changes]}]
  (go (while true
        (alt!
          actions ([msg] (run-action state msg))
          changes ([msg] (run-action state msg))
          periods ([msg] (run-action state msg))))))

(defn run-periods
  [state {:keys [periods]}]
  (go (while true
        (<! (timeout (get-in @state [:evolution :period])))
        (let [{{:keys [width height]}    :viewport
               {:keys [type population]} :universe
               {:keys [status]}          :evolution} @state]
          (when (= status :progress)
            (put! periods {:msg :evolve :population population :width width :height height :type type}))))))

(defn- empty-population-watcher
  [{:keys [changes]}]
  (fn [key ref old new]
    (let [{{np :population} :universe
           {s :status}      :evolution} new]
      (if (and (empty? np) (= s :progress))
        (put! changes {:msg :status :status :stasis})))))

(defn watch-changes
  [state channels]
  (add-watch state :empty-population (empty-population-watcher channels)))
