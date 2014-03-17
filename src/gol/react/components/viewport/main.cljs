(ns gol.react.components.viewport.main
  (:require [cljs.core.async :refer [put!]]))

(defn- toggle-cell-handler [chan x y population]
  (fn [] (put! chan {:msg :toggle :loc [x y] :population population})))

(defn viewport-component
  [state {:keys [actions]}]
  (let [{{:keys [width height]} :viewport
         {:keys [population]}   :universe} @state]
    (into [:ul.cell-area]
          (for [x (range width)]
            (into [:li]
                  (for [y (range height)]
                    [:b.cell {:on-click (toggle-cell-handler actions x y population)}
                     (when (population [x y]) [:i])]))))))
