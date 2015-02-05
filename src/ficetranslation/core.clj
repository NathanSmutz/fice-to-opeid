(ns ficetranslation.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.set :as cset])
  (:gen-class))


(defn -main
  []
  (println "Hello, World!"))


(defn add-to-set
  "Ads an element to a set and handles the housekeeping of creating a new set if one doesn't exist."
  [the-set element] 
  (if the-set 
    (conj the-set element)
    #{element}))


#_ (def the-file-content (slurp "./resources/ic9596_a.csv"))
#_ (def the-csv (csv/read-csv the-file-content))

(defn slurp-to-csv 
  "'Slurps' a file and loads it as a LaxySeq of vectors corresponding to each row
   of the incoming file"
  [filename]
  (csv/read-csv (slurp filename)))

(defn csv-header-index 
  "Receives a CSV file and returns a map with the items in the header row mapped to their indices in the row"
  [csv] (zipmap (first csv) (range)))

(defn get-csv-column-by-header 
  "Returns the last column with the given name"
  [csv column-name]
  (let [index  ((csv-header-index csv) column-name)]
    (map #(nth % index) csv)))

(defn extend-corelation-map
  "Extends a map corelating values coresponding to two columns in a CSV table.
   If values corresponnding to both headers are present in the row,
   then the second value is added to set refernced by the first value.
   Receives a map, a LazySeq of Vectors representing a CSV table and 
   two hashable values represented in the header of the CSV"
  [the-map csv-vector domain-header range-header]
  (let [csv-index  (csv-header-index csv-vector)
        fice-index (csv-index domain-header)
        ope-index  (csv-index range-header)]    
    (reduce #(update-in %1  [(%2 fice-index)] add-to-set (%2 ope-index))
            the-map
            (filter #(and ;; TODO: Think about using remove here
                       ((complement clojure.string/blank?) (% fice-index)) 
                       ((complement clojure.string/blank?) (% ope-index))) 
                    (rest csv-vector)))))

;; TODO: Create slurp-csv-into-corelation map
(defn slurp-csv-into-corelation-map
  [the-map the-file-name first-header second-header]
  (extend-corelation-map the-map (slurp-to-csv the-file-name) first-header second-header))

(defn stream-csv-into-corelation-map
  [the-map the-file-name first-header second-header]
  (with-open [the-file-stream (clojure.java.io/reader the-file-name)]
    (extend-corelation-map the-map (csv/read-csv the-file-stream) first-header second-header)))


#_ (def all-fice-opeid 
  (reduce #(extend-corelation-map %1 %2 "fice" "opeid")
          {}
          (map slurp-to-csv ["./resources/ic9596_a.csv" "./resources/ic9697_a.csv" "./resources/ic9798_hdr.csv"])))

;; All the FICE IDs we have, mapt to the set of OPEID's corresponding to each
(def all-fice-opeid
  (->
    {}
    (extend-corelation-map (slurp-to-csv "./resources/ic9596_a.csv")   "fice" "opeid")
    (extend-corelation-map (slurp-to-csv "./resources/ic9697_a.csv")   "fice" "opeid")
    (extend-corelation-map (slurp-to-csv "./resources/ic9798_hdr.csv") "fice" "opeid")))

;; This turns out to be a one-to-many situation
;; Here are the ones mapping to multiple OPEIDS
(def multi-opeids (filter #(< 1 (count (second %)))all-fice-opeid))


;; The good news is that there is only one FICE ID for any given OPEID
;; And intersection on all the sets of multiple OPEIDs returns the empty set
(apply cset/intersection (map second all-fice-opeid))
; #{}

