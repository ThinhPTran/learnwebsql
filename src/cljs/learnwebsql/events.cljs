(ns learnwebsql.events
  (:require [clojure.string :as str]
            [learnwebsql.db :as mydb :refer [send-message! db app-store app-state DBconn]]
            [learnwebsql.utils :as utils :refer [create-msg]]
            [datascript.core :as d]))

(defn runtestwebsql []
  (.transaction db
                (fn [tx]
                  (.executeSql tx "CREATE TABLE IF NOT EXISTS datoms (id , att, val, inst)"))
                (fn [err]
                  (.log js/console
                        (str "There was an error " (.core err))))
                (fn []
                  (.log js/console "It worked!"))))

;; Events
(defn addCount []
  (swap! app-state update-in [:count] inc)
  (send-message! app-store (create-msg -1 :inc 1)))

(defn addDatom []
  (let [datom [{:db/id -1 :name (str "Name " (rand-int 1000)) :age (rand-int 100)}]]
    (d/transact! DBconn datom)))

;; Handler for App-store
(defn app-store-handle-changes []
  (let [allactions (vals @app-store)]
    (.log js/console "handle-app-store-handle-changes: " (str allactions))
    (swap! app-state assoc :datoms (vec allactions))
    (doseq [action allactions]
      (.transaction db
                    (fn [tx]
                      (.executeSql tx "INSERT INTO datoms VALUES (?,?,?,?);" (clj->js [(:id action) (str (:att action)) (str (:val action)) (str (:inst action))])))))))

(add-watch app-store :key #(app-store-handle-changes))

;; Handler for DBconn
(defn DBconn-handle-changes []
  (let [datoms (utils/get-datoms DBconn)]
    (.log js/console "datoms: " (str datoms))
    (doseq [datom datoms]
      (send-message! app-store (create-msg (:e datom) (:a datom) (:v datom))))))

(add-watch DBconn :key #(DBconn-handle-changes))