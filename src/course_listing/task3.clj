(ns course-listing.task3
  (:gen-class)
  (:require [course-listing.csv-helper :refer :all]
            [clojure.core.reducers :as r]))

(defn print-result [result]
  (mapv (fn [[[first-instructor second-instructor]
              meeting-count]]
          (printf "%-30s %-30s %-30d\r\n" first-instructor second-instructor meeting-count)
          (flush))
        result))

(defn valid-course? [{:keys [instructor room]}]
  (let [instructor-trent-university (re-find #"Trent University" instructor)
        instructor-tba              (re-find #"TBA" instructor)
        invalid-room                (re-find #"N/A" room)]
    (and
      (nil? instructor-trent-university)
      (nil? instructor-tba)
      (nil? invalid-room))))

(defn get-top-instructor-pairs [instructors]
  (->> instructors
       frequencies
       (sort-by second)
       reverse
       (take 10)))

(defn same-weekday? [[{first-weekday :weekday} {second-weekday :weekday} first-course second-course]]
  (let [first-weekday  (:weekday first-course)
        second-weekday (:weekday second-course)]
    (= first-weekday second-weekday)))

(defn start-end-hour-relationship?
  [[{first-starthour :starthour first-endhour :endhour}
    {second-starthour :starthour second-endhour :endhour}]]
  (and (or (= first-starthour second-endhour) (= first-endhour second-starthour))
       (not= first-starthour second-starthour)))

(defn same-semester?
  [[{first-semester :semester} {second-semester :semester}]]
  (= first-semester second-semester))

(defn same-room?
  [[{first-room :room} {second-room :room}]]
  (= first-room second-room))

(defn different-instructors? [[first-course second-course]]
  (let [first-instructor  (:instructor first-course)
        second-instructor (:instructor second-course)]
    (not= first-instructor second-instructor)))

(defn compare-courses [[first-instructor second-instructor :as courses]]
  ((every-pred same-weekday? start-end-hour-relationship? same-semester? same-room? different-instructors?)
    courses))

(defn check-for-matches [matched-instructors current-course course-to-match]
  (if-let [match? (compare-courses [course-to-match current-course])]
    (conj matched-instructors [(:instructor course-to-match) (:instructor current-course)])
    matched-instructors))


(defn find-instructor-meetings [courses]
  (loop [course    (first courses)
         remaining (rest courses)
         meetings  []]
    (if-not (empty? remaining)
      (let [matched (r/reduce #(check-for-matches %1 %2 course) [] remaining)]
        (recur
          (first remaining)
          (rest remaining)
          (if-not (empty? matched)
            (into meetings matched)
            meetings)))
      meetings)))

(defn find-instructor-pairs [courses]
  (->> courses
       (sort-by :room)
       (partition-by :room)
       (sort-by :weekday)
       (pmap find-instructor-meetings)))

(defn get-courses [file]
  (->> file
       slurp
       create-map-from-file
       (filter valid-course?)))

(defn main
  "Takes a CSV file that is delimited by semicolons and
  reads the file into a map, splits the map by semester,
  and finds all instances where one instructor physically
  meets another."
  [course-listing-file]
  (println "Finding pairs of instructors...")
  (->> course-listing-file
       get-courses
       (partition-by :semester)
       (pmap find-instructor-pairs)
       (mapcat #(mapcat conj %))
       get-top-instructor-pairs
       print-result))
