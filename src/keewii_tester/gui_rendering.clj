(ns keewii-tester.gui_rendering
  (:use [seesaw core tree]
        [keewii-tester.vars]
        [keewii-tester.toolbox])
  (:import [java.io File FilenameFilter]
           [javax.swing.filechooser FileSystemView FileNameExtensionFilter]
           [javax.swing.filechooser.FileFilter])
  (:require [clojure.string :as string]) )
(def chooser (javax.swing.JFileChooser.)) 
;Part 1-1: tree
(def tree-model
  (simple-tree-model
    #(.isDirectory %)
     (fn [f]  (filter #(.isDirectory %) (.listFiles f)))
    (File. "C:\\Code\\keewii1\\data\\"))) 
;Part 1-2: list-item of tree
(defn render-file-item
  [renderer {:keys [value]}]
  (config! renderer :text (.getName value)
                   :icon (.getIcon chooser value)))
(defn render-file-item-filter
  [renderer {:keys [value]}]
  (when (.endsWith (.getName value) ".wav") (dissoc value)) 
  (if (.endsWith (.getName value) ".wav") 
  (config! renderer :text (.getName value)
                   :icon (.getIcon chooser value))
  (config! renderer :text (.getName value)
           ;:enabled? false
           :visible? false
           :icon (.getIcon chooser value))))
;Part 1: Tree&list-item rendering
(defn tree-explorer []
  (let [panel (border-panel 
                :border 5 :hgap 5 :vgap 5
                :north  (label :id :current-dir :text "Location")
                
                :center (left-right-split
                          (scrollable (tree    :id :tree :model tree-model :renderer render-file-item  :preferred-size [200 :by 50]))
                          (scrollable (listbox :id :list :renderer render-file-item-filter) :preferred-size [300 :by 50])
                          :divider-location 3/4
                          )
                :south  (horizontal-panel 
                          :items [(label :id :status :text "Ready" :h-text-position :center :preferred-size [200 :by 50])
                                  (button :text "Previous"
                                          :id :previous
                                          :preferred-size [270 :by 50]
                                          :mnemonic \N) 
                                  (button :text "Next"
                                          :id :next
                                          :preferred-size [270 :by 50]
                                          :mnemonic \N)])
                ; :divider-location 1/3)
                )]
    panel))

;Part 2: vowel selection rendering
(defn vowel-choice []
  "Radio buttons: vowel selections"
  (let [group (button-group)
        info-label (label :id :info-label :text "Select a vowel you heard" :border 10 :h-text-position :center)
        panel (border-panel
                :north (button :text "Play"
                               :id :play-wav
                               :mnemonic \N
                               )
                
                :center (border-panel
                          :north (vertical-panel
                                   :items [(radio :text "a" :selected? true :group group)
                                           (radio :text "e" :group group)
                                           (radio :text "i" :group group)
                                           (radio :text "o" :group group)
                                           (radio :text "u" :group group)])
                          :south info-label
                          )
                :south (left-right-split 
                         (button :text "Submit"
                                 :mnemonic \N
                                 :id :submit
                                 )
                         (button :text "Close"
                                 :mnemonic \N
                                 :id :close
                                 )
                         :divider-location 1/2
                         ) )]
    (listen group :action
      (fn [e]
        (text! info-label
          (str
            (text (selection group))))))
    panel))

;Part 3: merge and display
(defn content []
  (let [panel (left-right-split 
                (tree-explorer)
                (vowel-choice)
                :divider-location 3/4)]
  panel))

(def make-frame
  (let [f (frame :title "Myocontrol player" 
       :id "main-frame"
       :height 400
       :width 800
       :content (content)
       :visible? false)]
    (listen (select f [:#tree]) :selection
      (fn [e]
        (if-let [dir (last (selection e))]
          (let [files  (.listFiles dir)]
            (config! (select f [:#current-dir]) :text (.getAbsolutePath dir))
            (config! (select f [:#status]) :text (format "Ready (%d items)" (count files)))
            (config! (select f [:#list]) :model  files)
            
            ))))
    (listen (select f [:#list]) :selection
            (fn [e]
              (text!  (select f [:#current-dir])
                       (str (selection e)))
              ))
    (listen (select f [:#submit]) :action 
                     (fn [e] (doseq [](submit-save f)
                       (.setSelectedIndex (select f [:#list]) (+ (.getSelectedIndex (select f [:#list])) 1))
                       (change_path f);change path 
                       )))
    (listen (select f [:#close]) :action 
                     (fn [e] (save-close f)))
    (listen (select f [:#play-wav]) :action
            (fn [e] (wav-play f)))
    (listen (select f [:#previous]) :action
            (fn [e] (.setSelectedIndex (select f [:#list]) (- (.getSelectedIndex (select f [:#list])) 1))))
    (listen (select f [:#next]) :action
            (fn [e] (.setSelectedIndex (select f [:#list]) (+ (.getSelectedIndex (select f [:#list])) 1)) ))
    f))