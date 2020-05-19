(ns rodeo.core
  (:require [cheshire.core :as json]
            [clojure.string :as string]
            [clj-http.lite.client :as http]))

(def ^:const ^:private api-key-name "GEOCODIO_API_KEY")
(def ^:const ^:private geocode-base-url "https://api.geocod.io/v1.5/geocode")
(def ^:const ^:private parse-base-url "https://api.geocod.io/v1.5/parse")
(def ^:const ^:private reverse-base-url "https://api.geocod.io/v1.5/reverse")
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

(defn- geocodio-get [url api-key address fields]
  (->> {:accept :json :query-params {"q" address "api_key" api-key "fields" fields} :throw-exceptions false}
       (http/get url) 
       (handle-response)))

(defn- geocodio-post [url api-key body fields]
  (->> {:body body :content-type :json :accept :json :query-params {"api_key" api-key "fields" fields} :throw-exceptions false}
       (http/post url)
       (handle-response)))

(defn- fields->str [fields]
  (string/join "," fields))

(defn- handle-single [base-url location api-key fields]
  (if api-key
    (geocodio-get base-url api-key location (fields->str fields))
    {:error env-exception-text}))

(defn- handle-batch [base-url locations api-key fields]
  (if api-key
    (let [addr-json (json/generate-string locations)
          fields-str (fields->str fields)]
      (geocodio-post base-url api-key addr-json fields-str))
    {:error env-exception-text}))

(defn batch
  "Takes a seq of address strings 
  and an option hash containing
  zero or more of the following keys:

  :api-key A string with your Geocodio api key. If this 
  option is not set, the key will be read from the
  GEOCODIO_API_KEY environment variable
  
  :fields A seq of strings specifying which (if any) extra
  fields are desired. The possible fields and their corresponding
  parameter names are specified in the Geocodio documentation

  Returns a Clojure map of results. The map
  will have an :error key with a description if there
  was an error"
  [addresses & {:keys [api-key fields] :or {api-key (get-env-variable) fields []}}]
    (handle-batch geocode-base-url addresses api-key fields))

(defn single
  "Takes a single address string
  and an option hash containing
  zero or more of the following keys:

  :api-key A string with your Geocodio api key. If this 
  option is not set, the key will be read from the
  GEOCODIO_API_KEY environment variable
  
  :fields A seq of strings specifying which (if any) extra
  fields are desired. The possible fields and their corresponding
  parameter names are specified in the Geocodio documentation

  Returns a Clojure map of results. The map
  will have an :error key with a description if there
  was an error"
  [address & {:keys [api-key fields] :or {api-key (get-env-variable) fields []}}]
    (handle-single geocode-base-url address api-key fields))

(defn components
  "Takes a single address string
  and an option hash containing
  zero or more of the following keys:

  :api-key A string with your Geocodio api key. If this 
  option is not set, the key will be read from the
  GEOCODIO_API_KEY environment variable
  
  Returns a Clojure map of results. The map
  will have an :error key with a description if there
  was an error"
  [address & {:keys [api-key] :or {api-key (get-env-variable)}}]
    (handle-single parse-base-url address api-key []))

(defn single-reverse
  "Takes a single comma-separated 
  latitude and longitude pair and an option hash containing
  zero or more of the following keys:

  :api-key A string with your Geocodio api key. If this 
  option is not set, the key will be read from the
  GEOCODIO_API_KEY environment variable
  
  :fields A seq of strings specifying which (if any) extra
  fields are desired. The possible fields and their corresponding
  parameter names are specified in the Geocodio documentation

  Returns a Clojure map of address results. The map
  will have an :error key with a description if there
  was an error"
  [lat-long-pair & {:keys [api-key fields] :or {api-key (get-env-variable) fields []}}]
    (handle-single reverse-base-url lat-long-pair api-key fields))

(defn batch-reverse
  "Takes a seq of strings containing comma separated
  latitude and longitude pairs and an option hash containing
  zero or more of the following keys:

  :api-key A string with your Geocodio api key. If this 
  option is not set, the key will be read from the
  GEOCODIO_API_KEY environment variable
  
  :fields A seq of strings specifying which (if any) extra
  fields are desired. The possible fields and their corresponding
  parameter names are specified in the Geocodio documentation

  Returns a Clojure map of address results. The map
  will have an :error key with a description if there
  was an error"
  [pairs & {:keys [api-key fields] :or {api-key (get-env-variable) fields []}}] 
    (handle-batch reverse-base-url pairs api-key fields))
