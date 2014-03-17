(ns user
  (:require [cljs.repl :as repl]
            [cljs.repl.browser :as browser]))

(defn cljs-repl
  []
  (repl/repl (browser/repl-env)))
