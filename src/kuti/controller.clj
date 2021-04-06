(ns kuti.controller
  (:require
    [kuti.support.time :as time]))

(defn map-with-name [doc mapping params]
  (assoc doc mapping (get params mapping)))

(defn map-with-alias [doc mapping params]
  (assoc doc
         (second mapping)
         (get params (first mapping))))

(defn map-with-lambda [doc mapping params]
  (assoc doc
         (first mapping)
         ((second mapping) params)))

(defn map-with-vector [doc mapping params]
  (if (keyword? (second mapping))
    (map-with-alias doc mapping params)
    (map-with-lambda doc mapping params)))

(defn apply-mapping
  "Maps a value from `params` to `doc` either with a supplied `[field fn]` (vector)
   form or simply by copying the field (keyword) directly."
  [doc mapping params]
  (cond
    (vector? mapping) (map-with-vector doc mapping params)
    (keyword? mapping) (map-with-name doc mapping params)
    :else (throw (java.lang.Exception. (format "Parameter mapping '%s' is not a keyword or fn." mapping)))))

(defn tag-date [params]
  (if (:published-at params)
    (:published-at params)
    (time/now)))

(defn namespaced-pair [n [k v]]
  (assert (keyword? k))
  [(keyword (name n) (name k))
   v])

;; public API

(defn params->doc [params mappings]
  (let [mappings (conj mappings [:published-at tag-date])]
    (reduce #(apply-mapping %1 %2 params) {} mappings)))

(defn namespaced [n params]
  (assert (keyword? n))
  (into {}
        (map (partial namespaced-pair n) params)))
