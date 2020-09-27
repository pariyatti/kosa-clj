(ns kosa-crux.entity.pali-word.spec
  (:require [clojure.string]
            [clojure.spec.alpha :as s]))

(s/def ::truthy-string (s/and string? #(or (= "true" %)
                                                 (= "false" %))))
(s/def ::truthy-value boolean?)

(s/def ::published-at inst?)
(s/def ::bookmarkable (s/or :entity/truthy-value :entity/truthy-string))
(s/def ::shareable (s/or :entity/truthy-value :entity/truthy-string))
(s/def ::card-type (s/and string? #(= "pali_word" %)))
(s/def ::pali (s/and string? #(-> % clojure.string/blank? not)))
(s/def ::id uuid?)
(s/def ::header
  (s/and string? #(-> % clojure.string/blank? not)))

(s/def ::url (s/and string? #(-> % clojure.string/blank? not)))
(s/def ::audio (s/keys :req-un [:pali-word.audio/url]))

(s/def ::language (s/and string? #(contains? #{"hi" "en"} %)))
(s/def ::translation (s/and string? #(-> % clojure.string/blank? not)))
(s/def ::id uuid?)
(s/def ::translations
  (s/coll-of (s/keys :req-un [:translations/id
                              :translations/translation
                              :translations/language])))

(s/def ::pali-word
  (s/keys :req-un [:pali-word/id
                   :pali-word/header
                   :pali-word/bookmarkable
                   :pali-word/shareable
                   :pali-word/card-type
                   :pali-word/pali
                   :pali-word/published-at
                   :pali-word/audio
                   :pali-word/translations]))

(s/def ::pali-word-request
  (s/keys :req-un [:pali-word/header :pali-word/pali]
          :opt [:pali-word/bookmarkable :pali-word/shareable]))
