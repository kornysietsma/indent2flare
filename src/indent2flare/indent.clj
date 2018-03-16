(ns indent2flare.indent
  (:require [indent2flare.flare :as flare]
            [clojure.java.io :as io]
            [clojure.walk :refer [keywordize-keys]]
            [cheshire.core :as cheshire]
            [clojure.set :as set]
            [com.stuartsierra.frequencies :as freq])
  (:import (java.io Reader Writer File)))

(defn errmsg [msg]
  (binding [*out* *err*]
    (println msg)))

(def ws-pattern #"^([ \t]*)(.*)$")

(defn scan-for-indents "indentation of line - nil for blank lines"
  [line]
  (let [[_ ws rem] (re-matches ws-pattern line)]
    (cond
      (empty? rem) nil
      (empty? ws) 0
      :else (let [counts (frequencies ws)
                  spaces (or (counts \space) 0)
                  tabs (or (counts \tab) 0)]
              (+ spaces (* 4 tabs))))))

(defn scan-file-indents [^File file]
  (with-open [rdr (io/reader file)]
    (->> (line-seq rdr)
         (map scan-for-indents)
         (filter identity)
         frequencies
         freq/stats)))

(defn merge-indents-with-dir
  [node dir]
  (if (:children node)
    (assoc node :children
                (map (fn [child]
                       (let [child-name (:name child)
                             f (File. dir child-name)]
                         (cond
                           (not (.exists f)) (do
                                               (errmsg (str "no matching file for " child-name " in " dir))
                                               child)
                           (.isFile f) (assoc-in child [:data :indents] (scan-file-indents f))
                           (.isDirectory f) (merge-indents-with-dir child f)
                           :else (do (errmsg (str "not a file or directory: " dir))
                                     child))))
                     (:children node)))
    (do
      (errmsg (str "associating dir " dir " with empty node."))
      node)))

(defn merge-indents-with-json
  [in-file out-file root-dir]
  (let [data (cheshire/parse-stream in-file true)]
    (-> data
        (merge-indents-with-dir root-dir)
        (cheshire/generate-stream out-file {:pretty true}))))

