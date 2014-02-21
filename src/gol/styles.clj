(ns gol.styles
  (:require [garden.def :refer [defstyles]]
            [garden.def :refer [defrule defkeyframes]]
            [garden.units :refer [px percent s]]
            [garden.color :refer [hex->rgb rgba rgb]]
  )
)


(def cell-size 10)


(defkeyframes cell-blinking
  [:from {:opacity 1}]
  [:50% {:opacity 0.6}]
  [:to {:opacity 1}]
)


(defkeyframes cell-animation
  [:form {
    :width (percent 100)
    :height (percent 100)
    :border-radius (percent 50) }]

  [:20% {
    :width (percent 110)
    :height (percent 110)
    :border-radius [[(percent 47) (percent 100) (percent 60) (percent 81)]] }]

  [:40% {
    :width (percent 100)
    :height (percent 100)
    :border-radius [[(percent 188) (percent 40) (percent 206) (percent 80)]] }]

  [:60% {
    :width (percent 110)
    :height (percent 110)
    :border-radius [[(percent 81) (percent 99) (percent 66) (percent 75)]] }]

  [:80% {
    :width (percent 100)
    :height (percent 100)
    :border-radius [[(percent 71) (percent 33) (percent 63) (percent 47)]] }]

  [:to {
    :width (percent 100)
    :height (percent 100)
    :border-radius (percent 50) }]
)


(defkeyframes cell-core
  [:form {
    :left (percent 50)
    :top (percent 50) }]

  [:33% {
    :left (percent 30)
    :top (percent 60) }]

  [:66% {
    :left (percent 35)
    :top (percent 40) }]

  [:to {
    :left (percent 50)
    :top (percent 50) }]
)


(defstyles styles
  cell-animation
  cell-blinking
  cell-core

  [:html {
    :height (percent 100) }]

  [:body {
    :display "table"
    :width (percent 100)
    :height (percent 100)
    :margin 0
    :background-color (hex->rgb "#fdfdfd") }]

  [:#app {
    :display "table-cell"
    :vertical-align "middle"
    :text-align "center" }]

  [:.cell-area {
    :display "inline-block"
    :margin 0
    :padding [[(px 1) (px 1) 0 0]]
    :list-style "none"
    :font-size 0
    :line-height 0 }

    [:&:hover
      [:b {:border-color (hex->rgb "#ccc")}] ]

    [:.cell ^:prefix {
      :animation [[cell-blinking (s 2) "infinite" "linear"]] }]

    [:b {
      :display "inline-block"
      :width (px cell-size)
      :height (px cell-size)
      :padding (px 1)
      :margin [[(px -1) 0 0 (px -1)]]
      :vertical-align "top"
      :border [[(px 1) "dotted" "transparent"]] }

      [:i {
        :position "relative"
        :display "block"
        :width (percent 100)
        :height (percent 100)
        :border [[(px 2) "dashed" (hex->rgb "#90E9FF")]]
        :background-color (hex->rgb "#B2F3FF") }

        [:& ^:prefix {
          :border-radius (percent 50)
          :box-shadow [[0 0 (px 10) (rgba 0 0 0 0.2)]] }]

        [:& ^:prefix {
          :animation [[cell-animation (s 5) "infinite" "linear"]] }]

        [:&:after {
          :position "absolute"
          :top (percent 50)
          :left (percent 50)
          :width (percent 15)
          :height (percent 15)
          :background-color (hex->rgb "#00D7FF")
          :content "\"\""}
          [:& ^:prefix {
            :border-radius (percent 50)
            :box-shadow [[0 0 (px 3) (rgba 0 0 0 0.5)]] }]

          [:& ^:prefix {
            :animation [[cell-core (s 3) "infinite" "linear"]] }] ]]]]
)
