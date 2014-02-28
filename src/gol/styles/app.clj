(ns gol.styles.app
  (:require [garden.def :refer [defstyles]]
            [garden.def :refer [defrule defkeyframes]]
            [garden.units :refer [px percent s]]
            [garden.color :refer [hex->rgb rgba rgb]]))

(def cell-size 20)

(defstyles styles
  [:#app {:display        "table-cell"
          :vertical-align "middle"
          :text-align     "center"}]

  [:.cell-area {:display     "inline-block"
                :margin      [[0 (px -1) (px -1) 0]]
                :padding     [[(px 1) (px 1) 0 0]]
                :list-style  "none"
                :font-size   0
                :line-height 0}

  [:&:hover
   [:b {:border-color (hex->rgb "#ccc")}]]

  [:b {:display        "inline-block"
       :width          (px cell-size)
       :height         (px cell-size)
       :padding        (px 1)
       :margin         [[(px -1) 0 0 (px -1)]]
       :border         [[(px 1) "dotted" "transparent"]]
       :vertical-align "top"
       :white-space    "nowrap"}

   [:i {:position         "relative"
        :display          "block"
        :width            (percent 100)
        :height           (percent 100)
        :border           [[(px 2) "dashed" (hex->rgb "#90E9FF")]]
        :background-color (hex->rgb "#B2F3FF")}

    [:& ^:prefix {:border-radius (percent 50)
                  :box-shadow    [[0 0 (px 10) (rgba 0 0 0 0.2)]]}]

    [:&:after {:position         "absolute"
               :top              (percent 50)
               :left             (percent 50)
               :width            (percent 15)
               :height           (percent 15)
               :background-color (hex->rgb "#00D7FF")
               :content          "\"\""}

     [:& ^:prefix {:border-radius (percent 50)
                   :box-shadow    [[0 0 (px 3) (rgba 0 0 0 0.5)]]}]]]]])
