(ns learnwebsql.core
  (:require [reagent.core :as reagent]
            [learnwebsql.db :as mydb]
            [learnwebsql.events :as events]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn userdatoms []
  (let [datoms (:datoms @mydb/app-state)]
    [:div
     [:input
      {:type "button"
       :value "Add datom"
       :on-click (fn [_]
                   (events/addDatom))}]
     [:div "Datoms: "]
     [:ul
      (for [datom datoms]
        ^{:key datom} [:li (str datom)])]]))

(defn page [ratom]
  [:div "Welcome to reagent-figwheel."
   [userdatoms]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))


(defn reload []
  (reagent/render [page mydb/app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload)
  (mydb/setupclientdata)
  (events/runtestwebsql))
