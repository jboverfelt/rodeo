# rodeo [![Build Status](https://travis-ci.org/jboverfelt/rodeo.png?branch=master)](https://travis-ci.org/jboverfelt/rodeo) [![Dependencies Status](http://jarkeeper.com/jboverfelt/rodeo/status.png)](http://jarkeeper.com/jboverfelt/rodeo) [![Coverage Status](https://coveralls.io/repos/jboverfelt/rodeo/badge.png?branch=master)](https://coveralls.io/r/jboverfelt/rodeo?branch=master)

A Clojure library designed to be a thin wrapper over the Geocodio API.

[API Documentation](http://jboverfelt.github.io/rodeo/)
## Installation

#### Leiningen
```clojure
[rodeo "1.0.0"]
```
#### Maven
```xml
<dependency>
  <groupId>rodeo</groupId>
  <artifactId>rodeo</artifactId>
  <version>1.0.0</version>
</dependency>
```
#### Gradle
```groovy
compile "rodeo:rodeo:1.0.0"
```

## Usage

You must acquire an API key from the [Geocodio site](http://geocod.io)

Then, either set the GEOCODIO_API_KEY 
envrionment variable with the API key or 
pass it into the Rodeo functions as described below.

Acceptable address formats are detailed [here](http://geocod.io/docs/)

All functions return Clojure map. The map will contain an :error key
with a description if there was an error. Otherwise, it will
return a map containing the response.

### Geocoding

Returns a Clojure map with a parsed address and geolocation information

```clojure

(ns my.ns
  (:require [rodeo.core :refer :all]))

;;; batch addresses

;; with environment variable

(batch ["42370 Bob Hope Dr, Rancho Mirage CA" "54 West Colorado Boulevard, Pasadena, CA 91105"])

;; without environment variable

(batch ["42370 Bob Hope Dr, Rancho Mirage CA" "54 West Colorado Boulevard, Pasadena, CA 91105"] 
  "api-key-here")

;;; single addresses

(single "42370 Bob Hope Dr, Rancho Mirage CA")

(single "42370 Bob Hope Dr, Rancho Mirage CA" "api-key-here")

```

### Reverse Geocoding

Given a seq of lat long pairs, returns a Clojure map with address information

```clojure

(single-reverse "42.584149,-71.005885")
(single-reverse "42.584149,-71.005885" "api-key-here")

(batch-reverse ["42.584149,-71.005885" "34.1455496,-118.151631"])
(batch-reverse ["42.584149,-71.005885" "34.1455496,-118.151631"] "api-key-here")
```

### Parsing Components

Returns a Clojure map containing just the parsed address (street, city, etc)

```clojure

(components "42370 Bob Hope Dr, Rancho Mirage CA")
(components "42370 Bob Hope Dr, Rancho Mirage CA" "api-key-here")
```

## License

Copyright © 2014 Justin Overfelt

Distributed under the Eclipse Public License (same as Clojure) either version 1.0 or (at
your option) any later version.
