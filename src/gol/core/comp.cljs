(ns gol.core.comp
  (:require [cljs.core.async :refer [chan]]
            [gol.core.bl :refer [rand-population]]))

(defn create-state
  [{:keys [width height count period status type]}]
  (atom {:universe {:population (set (take count (rand-population width height)))
                    :type type}
         :evolution {:period period
                     :status status}
         :generator {:count count}
         :viewport {:width width
                    :height height}}))

(defn create-channels
  []
  {:actions (chan)
   :periods (chan)})
