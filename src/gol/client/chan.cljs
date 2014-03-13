(ns gol.client.chan
  (:require [cljs.core.async :refer [chan]]))

(defn create-channels
  []
  {:actions (chan)})
