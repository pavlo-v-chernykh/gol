(ns gol.core.state
  (:require [gol.core.bl :refer [rand-population]]))

(defn create-state
  [{:keys [width height count period status type]}]
  {:universe  {:population (set (take count (rand-population width height)))
               :type       type}
   :evolution {:period period
               :status status}
   :generator {:count count}
   :viewport  {:width  width
               :height height}})
