(ns gol.client.state
  (:require [reagent.core :as r :refer [atom]]
            [gol.client.bl :refer [rand-population]]))

(defn create-state
  [{:keys [width height count period status type]}]
  (atom {:universe  {:population (set (take count (rand-population width height)))
                     :type       type}
         :evolution {:period period
                     :status status}
         :generator {:count count}
         :viewport  {:width  width
                     :height height}}))
