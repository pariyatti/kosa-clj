(ns kosa.mobile.today.looped-words-of-buddha.db
  (:refer-clojure :exclude [list get])
  (:require [kuti.record :as record]
            [kuti.record.nested :as nested]
            [kuti.storage :as storage]
            [kuti.support :refer [assoc-unless]]
            [kuti.support.debugging :refer :all]))

(defn rehydrate [card]
  (as-> (nested/expand-all card :looped-words-of-buddha/audio-attachment) c
    ;; TODO: this behaviour really belongs in kuti.storage
    (assoc-in c
              [:looped-words-of-buddha/audio-attachment :attm/url]
              (storage/url (:looped-words-of-buddha/audio-attachment c)))))

(defn list []
  (map rehydrate (record/list :looped-words-of-buddha)))

(defn q [attr param]
  (let [find-query {:find     '[e updated-at]
                    :where    [['e attr 'v]
                               '[e :looped-words-of-buddha/updated-at updated-at]]
                    :order-by '[[updated-at :desc]]
                    :in       '[v]}]
    (map rehydrate (record/query find-query param))))

(defn next-index []
  (let [find-query '{:find     [(max ?idx)]
                     :where    [[e :looped-words-of-buddha/index ?idx]]}
        result (-> (record/q find-query) first first)]
    (if result
      (+ 1 result)
      0)))

(defn save! [e]
  (-> e
      (assoc :kuti/type :looped-words-of-buddha)
      (assoc-unless :looped-words-of-buddha/index (next-index))
      (nested/collapse-one :looped-words-of-buddha/audio-attachment)
      record/timestamp
      record/publish
      (record/save!)))
