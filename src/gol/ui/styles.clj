(ns gol.ui.styles
  (:require [garden.def :refer [defstyles]]
            [gol.ui.components.viewport.styles :as viewport]
            [gol.ui.components.control.styles :as control]))

(defstyles styles
  viewport/styles
  control/styles)
