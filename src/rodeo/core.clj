(ns rodeo.core
  (:require [cheshire.core :as che]
            [clj-http.lite.client :as http]))

(def ^:const ^:private api-key-name "GEOCODIO_API_KEY")
(def ^:const ^:private geocode-base-url "http://api.geocod.io/v1/geocode?api_key=")
(def ^:const ^:private parse-base-url "http://api.geocod.io/v1/parse?api_key=")
(def ^:const ^:private reverse-base-url "http://api.geocod.io/v1/reverse?api_key=")
(def ^:const ^:private env-exception-text (str "The " api-key-name " environment variable was not set.
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

(defn- handle-single-with-env [base-url location]
    (if-let [env-api-key (get-env-variable)]
      (geocodio-get (str base-url env-api-key) location)
      (throw (Exception. env-exception-text))))

(defn- handle-single-with-key [base-url location api-key]
  (let [url (str base-url api-key)]
    (geocodio-get url location)))

(defn- handle-batch-with-env [base-url locations]
  (if-let [env-api-key (get-env-variable)]
    (let [addr-json (che/generate-string locations)
          url (str base-url env-api-key)]
      (geocodio-post url addr-json))
    (throw (Exception. env-exception-text))))
  
(defn- handle-batch-with-key [base-url locations api-key]
  (let [addr-json (che/generate-string locations)
        url (str base-url api-key)]
    (geocodio-post url addr-json)))

(defn batch
  "Takes a seq of addresses and
  and optional api key and
  makes a request to Geocodio.
  Returns a Clojure map of results."
  ([addresses]
    (handle-batch-with-env geocode-base-url addresses))

  ([addresses api-key]
    (handle-batch-with-key geocode-base-url addresses api-key)))

(defn single
  "Takes a single address string and an optional api key and
  makes a request to Geocodio. Returns
  a Clojure map of results"
  ([address]
    (handle-single-with-env geocode-base-url address))

  ([address api-key]
    (handle-single-with-key geocode-base-url address api-key)))

(defn components
  "Takes a single address string and an optional
  api key and returns a clojure map of the components
  of that address (street, city, etc as a
  Clojure map"
  ([address]
    (handle-single-with-env parse-base-url address))

  ([address api-key]
    (handle-single-with-key parse-base-url address api-key)))

(defn single-reverse
  "Takes a string containing a comma separated latitude
  longitude pair and an optional api key and returns a
  Clojure map of address results"
  ([lat-long-pair]
    (handle-single-with-env reverse-base-url lat-long-pair))

  ([lat-long-pair api-key]
    (handle-single-with-key reverse-base-url lat-long-pair api-key))) 

(defn batch-reverse
  "Takes a seq of strings containing comma separated
  latitude and longitude pairs and an optional api key
  and returns a Clojure map of address results"
  ([pairs]
    (handle-batch-with-env reverse-base-url pairs))

  ([pairs api-key]
    (handle-batch-with-key reverse-base-url pairs api-key))) 
