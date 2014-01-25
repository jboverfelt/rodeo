(ns rodeo.core-test
  (:require [clojure.test :refer :all]
            [rodeo.core :as rodeo]))

(def api-key (get (System/getenv) "GEOCODIO_API_KEY"))
(def batch-data ["1 Infinite Loop Cupertino CA 95014" 
                 "54 West Colorado Boulevard Pasadena CA 91105"
                 "826 Howard Street San Francisco CA 94103"])  
(def single-data (first batch-data))

(defn batch-results? [resp]
  (-> resp
      (:results)
      (first)
      (:response)
      (:results)
      (vector?)))

(defn single-result? [resp]
  (-> resp
      (:results)
      (vector?)))

(defn component-result? [resp]
  (-> resp
      (:address_components)
      (:street)
      (string?)))

(deftest batch
  (testing "batch geocode calls"
    (is (batch-results? (rodeo/batch batch-data)))
    (is (batch-results? (rodeo/batch batch-data api-key))))
  (testing "bad api key"
    (is (thrown-with-msg? Exception #"status 403" (rodeo/batch batch-data "bogus key")))))

(deftest single
  (testing "single geocode call"
    (is (single-result? (rodeo/single single-data)))
    (is (single-result? (rodeo/single single-data api-key))))
  (testing "bad api key"
    (is (thrown-with-msg? Exception #"status 403" (rodeo/single single-data "bogus key")))))

(deftest components
  (testing "address component parsing"
    (is (component-result? (rodeo/components single-data)))
    (is (component-result? (rodeo/components single-data api-key))))
  (testing "bad api key"
    (is (thrown-with-msg? Exception #"status 403" (rodeo/components single-data "bogus key")))))
  
