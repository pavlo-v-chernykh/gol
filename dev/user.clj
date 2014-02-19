(ns user
  (:require [cljs.repl :as repl]
            [cljs.repl.browser :as browser]
            [gol.server :refer [app]]
            [ns-tracker.core :refer [ns-tracker]]
            [ring.util.serve :refer [serve]]
            [ring.mock.request :refer [request]]))

(defn cljs-repl
  []
  (repl/repl (browser/repl-env)))

(defn- ns-reload [track]
  (try
    (doseq [ns-sym (track)]
      (require ns-sym :reload))
    (catch Throwable e (.printStackTrace e))))

(defn watch
  ([] (watch ["src" "dev"]))
  ([src-paths]
   (let [track (ns-tracker src-paths)
         done (atom false)]
     (doto
         (Thread.
           (fn []
             (while (not @done)
               (ns-reload track)
               (Thread/sleep 500))))
       (.setDaemon true)
       (.start))
     (fn [] (swap! done not)))))

(defn restart
  []
  (serve app 3000)
  (watch))
