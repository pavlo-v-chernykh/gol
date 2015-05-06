(ns gol.ui.components.control.styles
  (:require [garden.def :refer [defstyles]]
            [garden.def :refer [defrule defkeyframes]]
            [garden.units :refer [px percent s]]
            [garden.color :refer [hex->rgb rgba rgb]]))

(defstyles styles
  [:#control {:margin-top (px 5)
              :float :right}])
