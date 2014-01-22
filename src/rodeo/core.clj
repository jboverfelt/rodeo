(ns rodeo.core
  (:require [cheshire.core :as che]
            [clj-http.client :as http]))

(def ^:const api-key (get (System/getenv) "GEOCODIO_API_KEY"))
(def ^:const geocode-url (str "http://api.geocod.io/v1/geocode?api_key=" api-key))
(def ^:const parse-url (str "http://api.geocod.io/v1/parse?api_key=" api-key))

(defn- geocodio-get [url address]
  (let [get-url (str parse-url "&q=" address)]
    (-> get-url
        (http/get)
        (:body)
        (che/parse-string true)))) 

(defn batch
  "Takes a vector of addresses and
  makes a request to Geocodio.
  Returns a Clojure map of results."
  [addresses]
  (let [addr-json (che/generate-string addresses)]
    (che/parse-string 
      (->> {:body addr-json :content-type :json :accept :json}
           (http/post geocode-url)
           (:body)) true)))

(defn single
  "Takes a single address string and
  makes a request to Geocodio. Returns
  a Clojure map of results"
  [address]
  (geocodio-get geocode-url address))

(defn components
  "Takes a single address string and
  returns a clojure map of the components
  of that address (street, city, etc as a 
  Clojure map"
  [address]
  (geocodio-get parse-url address)) 
