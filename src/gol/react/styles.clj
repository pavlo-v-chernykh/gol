(ns gol.react.styles
  (:require [garden.def :refer [defstyles]]
            [gol.react.components.viewport.styles :as viewport]
            [gol.react.components.control.styles :as control]))

(defstyles styles
  viewport/styles
  control/styles)
