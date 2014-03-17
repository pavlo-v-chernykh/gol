(ns gol.core.bl)

(defn neighbours
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1]
        :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn stepper
  [neighbours birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

(def step (stepper neighbours #{3} #{2 3}))

(defn filter-on-viewport
  [bw bh coll]
  (filter (fn [[x y]] (and (< -1 x bw) (< -1 y bh))) coll))

(defn filtered-on-viewport-stepper
  [w h]
  (comp set (partial filter-on-viewport w h) step))

(defn rand-2d-seq
  [width height]
  (cons [(rand-int width) (rand-int height)] (lazy-seq (rand-2d-seq width height))))

(defn rand-population
  [width height]
  (distinct (rand-2d-seq width height)))
