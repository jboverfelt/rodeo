(ns rodeo.core-test
  (:require [clojure.test :refer :all]
            [rodeo.core :as rodeo]))

(def api-key (get (System/getenv) "GEOCODIO_API_KEY"))
(def batch-data ["1 Infinite Loop Cupertino CA 95014" 
                 "54 West Colorado Boulevard Pasadena CA 91105"
                 "826 Howard Street San Francisco CA 94103"])  
(def single-data (first batch-data))
(def reverse-batch-data ["42.584149,-71.005885"
                         "34.1455496,-118.151631"
                         "39.0264385,-96.8377689"])
(def reverse-single-data (first reverse-batch-data))

(defn batch-results? [resp]
  (-> resp
      (:results)
      (first)
      (:response)
      (:results)
      (not-empty)))

(defn single-result? [resp]
  (-> resp
      (:results)
      (not-empty)))

(defn component-result? [resp]
  (-> resp
      (:address_components)
      (:street)
      (string?)))

(defn single-reverse-result? [resp]
  (-> resp
      (:results)
      (not-empty)))

(defn batch-reverse-results? [resp]
  (-> resp
      (:results)
      (count)
      (= (count reverse-batch-data))))

(deftest batch
  (testing "batch geocode calls"
    (is (batch-results? (rodeo/batch batch-data)))
    (is (batch-results? (rodeo/batch batch-data api-key))))
  (testing "errors"
    (is (= 422 (:status (rodeo/batch "1234"))))
    (is (= 403 (:status (rodeo/batch batch-data "bogus key"))))))

(deftest single
  (testing "single geocode call"
    (is (single-result? (rodeo/single single-data)))
    (is (single-result? (rodeo/single single-data api-key))))
  (testing "errors"
    (is (= 422 (:status (rodeo/single "1234"))))
    (is (= 403 (:status (rodeo/single single-data "bogus key"))))))

(deftest components
  (testing "address component parsing"
    (is (component-result? (rodeo/components single-data)))
    (is (component-result? (rodeo/components single-data api-key))))
  (testing "errors"
    (is (= 422 (:status (rodeo/components "1234"))))
    (is (= 403 (:status (rodeo/components single-data "bogus key"))))))

(deftest single-reverse
  (testing "reverse geocode lookup of single pair"
    (is (single-reverse-result? (rodeo/single-reverse reverse-single-data))) 
    (is (single-reverse-result? (rodeo/single-reverse reverse-single-data api-key))))
  (testing "errors"
    (is (= 422 (:status (rodeo/single-reverse "1234"))))
    (is (= 403 (:status (rodeo/single-reverse reverse-single-data "bogus key"))))))

(deftest batch-reverse
  (testing "reverse geocode lookup of seq of pairs"
    (is (batch-reverse-results? (rodeo/batch-reverse reverse-batch-data)))
    (is (batch-reverse-results? (rodeo/batch-reverse reverse-batch-data api-key))))
  (testing "errors"
    (is (= 422 (:status (rodeo/batch-reverse "1234"))))
    (is (= 403 (:status (rodeo/batch-reverse reverse-batch-data "bogus key"))))))
