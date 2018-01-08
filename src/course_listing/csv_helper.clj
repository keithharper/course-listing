(ns course-listing.csv-helper
  (:require [clojure.data.csv :as csv]))

(defn replace-unsafe-chars [file]
  (clojure.string/replace file #"[()\"]" ""))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data)  ;; First row is the header
            (map keyword)     ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn create-map-from-file [file]
  (-> file
      (replace-unsafe-chars)
      (csv/read-csv :separator \;)
      (csv-data->maps)))
