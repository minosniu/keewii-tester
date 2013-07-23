(ns keewii-tester.core
  (:use [seesaw.core :only (show!)]
        [keewii-tester.toolbox]
        [keewii-tester.vars]
        [keewii-tester.gui_rendering])
  (:gen-class))

(defn -main [& args]
  (show! make-frame))
;kangwoo + minos 
(-main)


