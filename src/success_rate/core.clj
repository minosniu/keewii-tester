(ns success_rate.core
  (:require [clojure.string :as string]
            [clojure.java.io :as io]) 
  (:import java.io.File)
  )
;memories
(def TEMP (atom ""))
(def PATH_subject "C:\\Code\\keewii1\\data\\")
(def PATH "C:\\Code\\keewii1\\data\\cmn20130620114055\\CT")
(def PATH1 "C:\\Code\\keewii1\\data\\lkw20130620172049\\CT")
;(def PATH2 "C:\\Code\\keewii1\\data\\jp\\Polar") ;nobody checked yet

;functions
(defn subject_list [d]
  "This function gets a subject list from keewii1//data"
  (doseq [f (.listFiles d)]
    (when (.isDirectory f)
      (println (.getName f))
      )))
(defn SR [d]
"This function loads test-log.txt and get success rate only"
  (doseq [f (.listFiles d)]
    (when (and (not (.isDirectory f)) (= "test-log.txt" (.getName f)))
      (reset! TEMP 
              (Float/parseFloat (first (string/split (last (string/split (slurp (str d "\\" (.getName f))) #"\n")) #"\%"))) )
      ))@TEMP)
;Usage!!!!
(subject_list (File. PATH_subject)) ; list of subjects' directories. Save them somewhere further
(SR (File. PATH1)); TEMP=xx.xx


;(def directory (clojure.java.io/file "C:\\Code\\keewii1\\data\\"))
;(def files (file-seq directory))
;(take 10 files)