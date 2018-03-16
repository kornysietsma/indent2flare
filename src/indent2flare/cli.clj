(ns indent2flare.cli
  (:require
    [indent2flare.indent :as indent]
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.java.io :as io]
    [clojure.string :as string])
  (:import (java.io File))
  (:gen-class))

  ; TODO:
  ; All I've done so far is copy the csv stuff - this isn't really started!
  ;  - this needs to actually iterate through the flare file looking for files to scan for indents
  ;  - so a lot of the csv stuff isn't useful!
  ; simplest thing that could work:
  ; - you should pass a flare file as input
  ; - you should run from a directory containing the root of the flare file
  ; - then it's probably simple to just recurse through updating the flare stuff
  ; Hmm - we might also want to insert part-way through the flare?  In a future version.

  (def cli-options
    [["-i" "--input filename" "select an input flare-format json file for merging - if you don't specify a -b option, the program will try to read flare data from standard input for piping"]
     ["-o" "--output filename" "select an output file name (default is STDOUT)"]
     ["-r" "--root path" "select a root directory matching the flare root (default is current dir)"]
     ["-h" "--help"]])

  (defn usage [options-summary]
    (->> ["Utilities for calculating indentation metrics and merging into a flare format for visualization"
          ""
          "Usage: java -jar indent2flare.jar [options]"
          ""
          "Options:"
          options-summary
          ""
          "Output is a flare JSON file for D3 visualization"
          ""
          "Note you must provide a base JSON flare file, and only files in that file will be updated, no new files are added."
          ""]
         (string/join \newline)))


(defn exit [status msg]
  (binding [*out* *err*]
    (println msg))
  (System/exit status))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

  (defn -main [& args]
    (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
      (cond
        (:help options) (exit 0 (usage summary))
        (not= (count arguments) 0) (exit 1 (usage summary))
        errors (exit 1 (error-msg errors)))
      (let [in-file (if (:input options)
                        (io/reader (:input options))
                        *in*)
            out-file (if (:output options)
                       (io/writer (:output options))
                       *out*)
            root-dir (or (:root options)
                         (.getCanonicalFile (File. ".")))]
        (try
          (indent/merge-indents-with-json in-file out-file root-dir)
          (finally
            (if (:input options)
              (.close in-file))
            (if (:output options)
              (.close out-file)))))))
