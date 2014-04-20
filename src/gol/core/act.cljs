(ns gol.core.act
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :refer [timeout <! put!]]
            [gol.core.hand :refer [change-status repopulate toggle-cell change-period
                                   change-type change-count evolve empty-population-watcher]]))

(def ^:private msg-handler-map
  {:status change-status
   :repopulate repopulate
   :toggle toggle-cell
   :period change-period
   :type change-type
   :count change-count
   :evolve evolve})

(defn- process-msg
  [state {action-key :msg :as msg}]
  (let [{handler action-key} msg-handler-map]
    (handler state msg)))

(defn listen-channels
  [state {:keys [actions periods]}]
  (go (while true
        (alt!
          actions ([msg] (process-msg state msg))
          periods ([msg] (process-msg state msg))))))

(defn run-periods
  [state {:keys [periods]}]
  (go (while true
        (let [{{:keys [period status]} :evolution} @state]
          (<! (timeout period))
          (when (= status :progress)
            (put! periods {:msg :evolve}))))))

(defn watch-changes
  [state]
  (add-watch state :empty-population (empty-population-watcher state)))
