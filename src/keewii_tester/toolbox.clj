(ns keewii-tester.toolbox
  (:use [seesaw.core]
        [clj-audio.core]
        [keewii-tester.vars])
  (:require [clojure.string :as string]))

;(defn parse-int [s]
;  "str2int" 
;   (Integer. (re-find  #"\d+" s )))

(defn change_path [f]
  "It change the path in the memory to read dat file by the path of wav file"
    (let [cur_file (text (select f [:#current-dir]))
        cur_wav (last (string/split (str cur_file) #"\\"))
        cur_path (string/replace cur_file (str "wav\\" cur_wav) "")]
      (reset! PATH cur_path)
      (reset! filename (first (string/split cur_file #"\."))))); exclude extension to get sequence name

(defn wav-play [f]
  "Play button: play wav file"
  (let [cur_file (text (select f [:#current-dir]))] 
    (play (->stream cur_file))
    (change_path f)))

(defn submit-save [f]
"Submit button: file root/user response/real answer are saved in test-log.txt" 
  (let [datapath (string/replace @filename "\\wav\\" "\\dat\\")]  
    (reset! alphabet  (second (string/split (slurp (str datapath ".dat")) #"\n")))
    (spit (str @PATH "test-log.txt") (str (text (select f [:#current-dir])) " " @alphabet " " (text (select f [:#info-label])) "\n")  :append true)
  (if (= @alphabet (str (text (select f [:#info-label])))) (reset! SR (inc @SR))))
  (reset! TOTAL (inc @TOTAL)))
(defn save-close [f]
  "Close button: test-log.txt willbe saved"
  (spit (str @PATH  "test-log.txt") (str  (format "%.2f" (* 100.0 (/ @SR @TOTAL))) "%\n\n")  :append true)
  (dispose! f)) 