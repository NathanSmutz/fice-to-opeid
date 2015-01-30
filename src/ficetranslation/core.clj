(ns ficetranslation.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.set :as cset])
  (:gen-class))


(defn -main
  []
  (println "Hello, World!"))


(defn add-to-set [the-set element] (if the-set 
                                     (conj the-set element) 
                                     #{element}))


#_ (def the-file-content (slurp "./resources/ic9596_a.csv"))
#_ (def the-csv (csv/read-csv the-file-content))

(defn slurp-to-csv [filename]
  (csv/read-csv (slurp filename)))

(defn csv-header-index [csv] (zipmap (first csv) (range)))
(defn get-csv-column-by-header 
  "Returns the last column with the given name"
  [csv column-name]
  (let [index  ((csv-header-index csv) column-name)]
    (map #(nth % index) csv)))

(defn extend-fice-opeid-map 
  [the-map csv-vector fice-header ope-header]
  (let [csv-index  (csv-header-index csv-vector)
        fice-index (csv-index fice-header)
        ope-index  (csv-index ope-header)]    
    (reduce #(update-in %1  [(%2 fice-index)] add-to-set (%2 ope-index))
            the-map
            (filter #(and 
                       ((complement clojure.string/blank?) (% fice-index)) 
                       ((complement clojure.string/blank?) (% ope-index))) 
                    (rest csv-vector)))))

#_ (def all-fice-opeid 
  (reduce #(extend-fice-opeid-map %1 %2 "fice" "opeid")
          {}
          (map slurp-to-csv ["./resources/ic9596_a.csv" "./resources/ic9697_a.csv" "./resources/ic9798_hdr.csv"])))

;; All the FICE IDs we have, mapt to the set of OPEID's corresponding to each
(def all-fice-opeid
  (->
    {}
    (extend-fice-opeid-map (slurp-to-csv "./resources/ic9596_a.csv")   "fice" "opeid")
    (extend-fice-opeid-map (slurp-to-csv "./resources/ic9697_a.csv")   "fice" "opeid")
    (extend-fice-opeid-map (slurp-to-csv "./resources/ic9798_hdr.csv") "fice" "opeid")))

;; This turns out to be a one-to-many situation
;; Here are the ones mapping to multiple OPEIDS
(def multi-opeids (filter #(< 1 (count (second %)))all-fice-opeid))


;; The good news is that there is only one FICE ID for any given OPEID
;; And intersection on all the sets of multiple OPEIDs returns the empty set
(apply cset/intersection (map second all-fice-opeid))
; #{}
