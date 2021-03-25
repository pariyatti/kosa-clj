(ns kutis.record-test
  (:require [clojure.test :refer :all]
            [crux.api]
            [kutis.support.digest :refer [->uuid]]
            [kutis.fixtures.record-fixtures :as fixtures]
            [kutis.record :as sut]))

(use-fixtures :once fixtures/load-states)

(def record {:crux.db/id (->uuid "3291d680-0d70-4940-914d-35413e261115")
             :record     "vinyl"
             :artist     "The Who"})

(def record-without-id {:city "Igatpuri"
                        :state "Maharashtra"
                        :country "India"
                        :population 1234})

(deftest db-insert-operations
  (testing "Can insert a raw datum"
    (let [tx (sut/put-async* record)]
      (crux.api/await-tx sut/crux-node tx)
      (is (= record (sut/get "3291d680-0d70-4940-914d-35413e261115")))))

  (testing "put returns the record inserted"
    (let [inserted (sut/put record [:record :artist])]
      (is (= (:record record) (:record inserted)))
      (is (= (:artist record) (:artist inserted)))))

  (testing "put generates a new id"
    (let [inserted (sut/put record-without-id [:country :state :city :population])]
      (is (= record-without-id (dissoc inserted :crux.db/id)))
      (is (not (nil? (:crux.db/id inserted))))))

  (testing "put barfs on badly-formed documents"
    (is (thrown-with-msg? java.lang.Exception #"Extra fields ':superfluous-field' found during put."
                          (sut/put {:city "Igatpuri" :superfluous-field "I should cause an error."}
                                   [:city])))))

(deftest db-update-operations
  (testing "Can update raw datums"
    (let [_               (sut/put-async* record)
          new-record      (-> record
                              (assoc :artist "the kinks" :song "Lola"))
          inserted-record (sut/put-async* new-record)]
      (crux.api/await-tx sut/crux-node inserted-record)
      (is (= (sut/get "3291d680-0d70-4940-914d-35413e261115")
             new-record))))

  (testing "put overwrites an existing record"
    (let [required-fields [:country :state :city :population]
          created (sut/put record-without-id required-fields)
          updated (sut/put (update created :population #(+ % 5555)) required-fields)]
      (is (= 1234 (:population created)))
      (is (= 6789 (:population updated)))
      (is (= (:crux.db/id created) (:crux.db/id updated))))))
