(ns rodeo.core
  (:require [cheshire.core :as json]
            [clj-http.lite.client :as http]))

(def ^:const ^:private api-key-name "GEOCODIO_API_KEY")
(def ^:const ^:private geocode-base-url "http://api.geocod.io/v1/geocode")
(def ^:const ^:private parse-base-url "http://api.geocod.io/v1/parse")
(def ^:const ^:private reverse-base-url "http://api.geocod.io/v1/reverse")
(def ^:const ^:private env-exception-text (str "The " api-key-name " environment variable was not set.
                                     Set the variable or pass in the api key to this function."))

(defn- get-env-variable []
  (get (System/getenv) api-key-name))

(defn- handle-response [resp]
  (let [status (:status resp)]
    (cond
      (= status 200) (json/parse-string (:body resp) true)
      (= status 403) (assoc (json/parse-string (:body resp) true) :status status)
      (= status 422) (assoc (json/parse-string (:body resp) true) :status status)
      :else          {:error (:body resp) :status status})))   

(defn- geocodio-get [url api-key address]
  (-> url
      (http/get {:accept :json :query-params {"q" address "api_key" api-key} :throw-exceptions false})
      (handle-response)))

(defn- geocodio-post [url api-key body]
  (->> {:body body :content-type :json :accept :json :query-params {"api_key" api-key} :throw-exceptions false}
       (http/post url)
       (handle-response)))

(defn- handle-single-with-env [base-url location]
    (if-let [env-api-key (get-env-variable)]
      (geocodio-get base-url env-api-key location)
      {:error env-exception-text}))

(defn- handle-single-with-key [base-url location api-key]
  (geocodio-get base-url api-key location))

(defn- handle-batch-with-env [base-url locations]
  (if-let [env-api-key (get-env-variable)]
    (let [addr-json (json/generate-string locations)]
      (geocodio-post base-url env-api-key addr-json))
    {:error env-exception-text}))
  
(defn- handle-batch-with-key [base-url locations api-key]
  (let [addr-json (json/generate-string locations)]
    (geocodio-post base-url api-key addr-json)))

(defn batch
  "Takes a seq of addresses and
  and optional api key and
  makes a request to Geocodio.
  Returns a Clojure map of results. The map will have
  an :error key with a description if there was an error"
  ([addresses]
    (handle-batch-with-env geocode-base-url addresses))

  ([addresses api-key]
    (handle-batch-with-key geocode-base-url addresses api-key)))

(defn single
  "Takes a single address string and an optional api key and
  makes a request to Geocodio. Returns
  a Clojure map of results. The map will have an :error key
  with a description if there was an error"
  ([address]
    (handle-single-with-env geocode-base-url address))

  ([address api-key]
    (handle-single-with-key geocode-base-url address api-key)))

(defn components
  "Takes a single address string and an optional
  api key and returns a clojure map of the components
  of that address (street, city, etc as a
  Clojure map. The map will have an :error key with a 
  description if there was an error"
  ([address]
    (handle-single-with-env parse-base-url address))

  ([address api-key]
    (handle-single-with-key parse-base-url address api-key)))

(defn single-reverse
  "Takes a string containing a comma separated latitude
  longitude pair and an optional api key and returns a
  Clojure map of address results. The map will have an
  :error key with a description if there was an error"
  ([lat-long-pair]
    (handle-single-with-env reverse-base-url lat-long-pair))

  ([lat-long-pair api-key]
    (handle-single-with-key reverse-base-url lat-long-pair api-key))) 

(defn batch-reverse
  "Takes a seq of strings containing comma separated
  latitude and longitude pairs and an optional api key
  and returns a Clojure map of address results. The map
  will have an :error key with a description if there
  was an error"
  ([pairs]
    (handle-batch-with-env reverse-base-url pairs))

  ([pairs api-key]
    (handle-batch-with-key reverse-base-url pairs api-key))) 
