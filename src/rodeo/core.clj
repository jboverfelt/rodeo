(ns rodeo.core
  (:require [cheshire.core :as che]
            [clj-http.client :as http]))

(def ^:const api-key-name "GEOCODIO_API_KEY")
(def ^:const geocode-base-url "http://api.geocod.io/v1/geocode?api_key=")
(def ^:const parse-base-url "http://api.geocod.io/v1/parse?api_key=")
(def ^:const env-exception-text (str "The " api-key-name " environment variable was not set.
                                     Set the variable or pass in the api key to this function."))

(defn- get-env-variable []
  (get (System/getenv) api-key-name))

(defn- geocodio-get [url address]
  (let [get-url (str url "&q=" address)]
    (-> get-url
        (http/get)
        (:body)
        (che/parse-string true))))

(defn- geocodio-post [url body]
  (che/parse-string
    (->> {:body body :content-type :json :accept :json}
         (http/post url)
         (:body)) true))

(defn batch
  "Takes a seq of addresses and
  and optional api key and
  makes a request to Geocodio.
  Returns a Clojure map of results."
  ([addresses]
    (if-let [env-api-key (get-env-variable)]
      (let [addr-json (che/generate-string addresses)
            url (str geocode-base-url env-api-key)]
        (geocodio-post url addr-json))
      (throw (Exception. env-exception-text))))

  ([addresses api-key]
    (let [addr-json (che/generate-string addresses)
          url (str geocode-base-url api-key)]
      (geocodio-post url addr-json))))

(defn single
  "Takes a single address string and an optional api key and
  makes a request to Geocodio. Returns
  a Clojure map of results"
  ([address]
    (if-let [env-api-key (get-env-variable)]
      (geocodio-get (str geocode-base-url env-api-key) address)
      (throw (Exception. env-exception-text))))

  ([address api-key]
    (let [url (str geocode-base-url api-key)]
      (geocodio-get url address))))

(defn components
  "Takes a single address string and an optional
  api key and returns a clojure map of the components
  of that address (street, city, etc as a
  Clojure map"
  ([address]
    (if-let [env-api-key (get-env-variable)]
      (geocodio-get (str parse-base-url env-api-key) address)
      (throw (Exception. env-exception-text))))

  ([address api-key]
   (let [url (str parse-base-url api-key)]
     (geocodio-get url address))))
