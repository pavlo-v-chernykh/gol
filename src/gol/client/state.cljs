(ns gol.client.state
  (:require [reagent.core :as r :refer [atom]]
            [gol.client.bl :refer [rand-population]]))

(defn create-state
  [& {:keys [width height count period status type]
      :or   {width  30
             height 30
             count  250
             period 500
             status :progress
             type   :unlimited}}]
  (atom {:universe  {:population (set (take count (rand-population width height)))
                     :type       type}
         :evolution {:period period
                     :status status}
         :generator {:count count}
         :viewport  {:width  width
                     :height height}}))
