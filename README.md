# rodeo [![Build Status](https://travis-ci.org/jboverfelt/rodeo.png?branch=master)](https://travis-ci.org/jboverfelt/rodeo)

A Clojure library designed to be a thin wrapper over the Geocodio API.

[API Documentation](http://jboverfelt.github.io/rodeo/)
## Installation

#### Leiningen
```clojure
[rodeo "0.1.0-SNAPSHOT"]
```
#### Maven
```xml
<dependency>
  <groupId>rodeo</groupId>
  <artifactId>rodeo</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```
#### Gradle
```groovy
compile "rodeo:rodeo:0.1.0-SNAPSHOT"
```

## Usage

You must acquire an API key from the [Geocodio site](http://geocod.io)

Then, either set the GEOCODIO_API_KEY 
envrionment variable with the API key or 
pass it into the Rodeo functions as described below.

Acceptable address formats are detailed [here](http://geocod.io/docs/)

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
