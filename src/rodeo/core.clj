(ns rodeo.core
  (:require [cheshire.core :as che]
            [clj-http.client :as http]))

(def ^:const api-key (get (System/getenv) "GEOCODIO_API_KEY"))
(def ^:const url (str "http://api.geocod.io/v1/geocode?api_key=" api-key))

(defn batch
  "Takes a vector of addresses and
  makes a request to Geocodio.
  Returns a Clojure map of results."
  [addresses]
  (let [addr-json (che/generate-string addresses)]
    (che/parse-string 
      (->> {:body addr-json :content-type :json :accept :json}
           (http/post url)
           (:body)) true)))

(defn single
  "Takes a single address string and
  makes a request to Geocodio. Returns
  a Clojure map of results"
  [address]
  (let [get-url (str url "&q=" address)]
    (-> get-url
        (http/get)
        (:body)
        (che/parse-string true))))
