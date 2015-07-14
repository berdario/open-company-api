# [OpenCompany.io](https://opencompany.io) Platform API

[![MPL License](http://img.shields.io/badge/license-MPL-blue.svg?style=flat)](https://www.mozilla.org/MPL/2.0/)
[![Roadmap on Trello](http://img.shields.io/badge/roadmap-trello-blue.svg?style=flat)](https://trello.com/b/3naVWHgZ/open-company-development)

## Overview

> A lack of transparency results in distrust and a deep sense of insecurity.

> -- Dalai Lama

Build your company in the open with transparency for your co-founders, your team, and your investors. Or share your company's journey with everyone, your customers and the rest of the startup community too.

[OpenCompany.io](https://opencompany.io) is GitHub for the rest of your company:

* **Dashboard** - An easy tool for founders to provide transparency to their teams and beyond.
* **Founders' Guide** - Tools, best practices and insights from open company founders and their companies.
* **Open Company Directory** - Founders sharing with their teams and beyond.
* **Community** - Spread the word and knowledge and inspire more founders to open up.

Like the open companies we promote and support, the [OpenCompany.io](https://opencompany.io) platform is completely transparent. The company supporting this effort, Transparency, LLC, is an open company. The [platform](https://github.com/open-company/open-company-web) is open source software, and open company data is [open data](https://en.wikipedia.org/wiki/Open_data) accessible through this platform API.

To get started, head to [OpenCompany.io](https://opencompany.io).

## Local Setup

Users of the [OpenCompany.io](https://opencompany.io) platform should get started by going to [OpenCompany.io](https://opencompany.io). The following local setup is for developers wanting to work on the platform's API software.

Most of the dependencies are internal, meaning [Leiningen](https://github.com/technomancy/leiningen) will handle getting them for you. There are a few exceptions:

* [Java 7/8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) - a Java 7 or 8 JRE is needed to run Clojure
* [Leiningen](https://github.com/technomancy/leiningen) 2.5.1+ - Clojure's build and dependency management tool
* [RethinkDB](http://rethinkdb.com/) v2.0.4+ - a multi-modal (document, key/value, relational) open source NoSQL database

#### Java

Chances are your system already has Java 7 or 8 installed. You can verify this with:

```console
java -version
```

If you do not have Java 7 or 8 [download it]((http://www.oracle.com/technetwork/java/javase/downloads/index.html)) and follow the installation instructions.

#### Leiningen

Leiningen is easy to install:

1. Download the latest [lein script from the stable branch](https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein).
1. Place it somewhere that's on your $PATH (`env | grep PATH`). `/usr/local/bin` is a good choice if it is on your PATH.
1. Set it to be executable. `chmod 755 /usr/local/bin/lein`
1. Run it: `lein` This will finish the installation.

Then let Leiningen install the rest of the dependencies:

```console
git clone https://github.com/open-company/open-company-api.git
cd open-company-api
lein deps
```

#### RethinkDB

RethinkDB is easy to install with official and community supported packages for most operating systems.

##### RethinkDB for Mac OS X via Brew

Assuming you are running Mac OS X and are a [Homebrew](http://mxcl.github.com/homebrew/) user, use brew to install RethinkDB:

```console
brew update && brew install rethinkdb
```

Follow the instructions provided by brew to run RethinkDB every time at login:

```console
ln -sfv /usr/local/opt/rethinkdb/*.plist ~/Library/LaunchAgents
```

And to run RethinkDB now:

```console
launchctl load ~/Library/LaunchAgents/homebrew.mxcl.rethinkdb.plist
```

Verify you can access the RethinkDB admin console:

```console
open http://localhost:8080/
```

After installing with brew:

* Your RethinkDB binary will be at `/usr/local/bin/rethinkdb`
* Your RethinkDB data directory will be at `/usr/local/var/rethinkdb`
* Your RethinkDB log will be at `/usr/local/var/log/rethinkdb/rethinkdb.log`
* Your RethinkDB launchd file will be at `~/Library/LaunchAgents/homebrew.mxcl.rethinkdb.plist`

##### RethinkDB for Mac OS X (Binary Package)

If you don't use brew, there is a binary package available for Mac OS X from the [Mac download page](http://rethinkdb.com/docs/install/osx/).

After downloading the disk image, mounting it (double click) and running the rethinkdb.pkg installer, you need to manually create the data directory:

```console
sudo mkdir -p /Library/RethinkDB
sudo chown <your-own-user-id> /Library/RethinkDB
mkdir /Library/RethinkDB/data
```

And you will need to manually create the launchd config file to run RethinkDB every time at login. From within this repo run:

```console
cp ./opt/com.rethinkdb.server.plist ~/Library/LaunchAgents/com.rethinkdb.server.plist
```

And to run RethinkDB now:

```console
launchctl load ~/Library/LaunchAgents/com.rethinkdb.server.plist
```

Verify you can access the RethinkDB admin console:

```console
open http://localhost:8080/
```

After installing with the binary package:

* Your RethinkDB binary will be at `/usr/local/bin/rethinkdb`
* Your RethinkDB data directory will be at `/Library/RethinkDB/data`
* Your RethinkDB log will be at `/var/log/rethinkdb.log`
* Your RethinkDB launchd file will be at `~/Library/LaunchAgents/com.rethinkdb.server.plist`


##### RethinkDB for Linux

If you run Linux on your development environment (good for you, hardcore!) you can get a package for you distribution or compile from source. Details are on the [installation page](http://rethinkdb.com/docs/install/).

##### RethinkDB for Windows

RethinkDB [isn't supported on Windows](https://github.com/rethinkdb/rethinkdb/issues/1100) directly. If you are stuck on Windows, you can run Linux in a virtualized environment to host RethinkDB.

## Introduction

You can verify all is well with your RethinkDB instance and get familiar with RethinkDB [ReQL query language](http://rethinkdb.com/docs/introduction-to-reql/) by using the Data Explorer:

```console
open http://localhost:8080/
```

Click the Data Explorer tab and enter these commands one-by-one, noting the output:

```javascript

// Create
r.dbCreate('open_company_dev');
r.db('open_company_dev').tableCreate('companies');

// Insert
r.db('open_company_dev').table('companies').insert([
  {symbol: 'OPEN', name: 'Transparency, LLC', url: 'https://opencompany.io/'},
  {symbol: 'BUFFR', name: 'Buffer', url: 'https://open.bufferapp.com/'}
]);

// Queries
r.db('open_company_dev').table('companies').count();
r.db('open_company_dev').table('companies').filter(r.row('symbol').eq('OPEN'));

// Cleanup
r.dbDrop('open_company_dev');
```

You can move that familiarity over into Clojure by running the REPL from within this project:

```console
lein repl
```

Then enter these commands one-by-one, noting the output:

```clojure
(require '[rethinkdb.query :as r])
(require '[open-company.config :as c])

;; Create DB and tables
(with-open [conn (apply r/connect c/db-options)]
  (r/run (r/db-create c/db-name) conn)
  (-> (r/db c/db-name)
      (r/table-create "companies" {:primary-key "symbol" :durability "hard"})
      (r/run conn))
  (-> (r/db c/db-name)
      (r/table-create "reports" {:primary-key "symbol-year-period" :durability "hard"})
      (r/run conn)))

;; Create table index
(with-open [conn (apply r/connect c/db-options)]
  (-> (r/table "reports")
      (r/index-create "symbol" 
        (r/fn [row]
          (r/get-field row :symbol)))
      (r/run conn))
  (-> (r/table "reports")
      (r/index-wait "symbol")
      (r/run conn)))

;; Insert some companies
(with-open [conn (apply r/connect c/db-options)]
  (-> (r/table "companies")
      (r/insert [
        {:symbol "OPEN" :name "Transparency, LLC" :url "https://opencompany.io/"}
        {:symbol "BUFFR" :name "Buffer" :url "https://open.bufferapp.com/"}        
      ])
      (r/run conn)))

;; Query on companies
(with-open [conn (apply r/connect c/db-options)]
  (-> (r/table "companies")
      (r/count)
      (r/run conn)))

(with-open [conn (apply r/connect c/db-options)]
  (-> (r/table "companies")
      (r/get-all ["OPEN"] {:index "symbol"})
      (r/run conn)))

;; Cleanup
(with-open [conn (apply r/connect c/db-options)]
  (r/run (r/db-drop c/db-name) conn))
```

## Usage

Start a development API server:

```console
lein start
```

Create a company with cURL:

```console
curl -i -X PUT \
-d '{"symbol": "OPEN", "name": "Transparency, LLC", "url": "https://opencompany.io"}' \
--header "Accept: application/vnd.open-company.company+json;version=1" \
--header "Accept-Charset: utf-8" \
--header "Content-Type: application/vnd.open-company.company+json;version=1" \
http://localhost:3000/v1/companies/OPEN
```

Request the company with cURL:

```console
curl -i -X GET \
--header "Accept: application/vnd.open-company.company+json;version=1" \
--header "Accept-Charset: utf-8" \
http://localhost:3000/v1/companies/OPEN
```

Update a company with cURL:

```console
curl -i -X PUT \
-d '{"symbol": "OPEN", "name": "Transparency, LLC", "url": "https://opencompany.io/"}' \
--header "Accept: application/vnd.open-company.company+json;version=1" \
--header "Accept-Charset: utf-8" \
--header "Content-Type: application/vnd.open-company.company+json;version=1" \
http://localhost:3000/v1/companies/OPEN
```

Create a report for the company with cURL:

```console
curl -i -X PUT http://localhost:3000/v1/companies/OPEN/2015/Q2
```

Request the report with cURL:

```console
curl -i -X GET \
--header "Accept: application/vnd.open-company.report+json;version=1" \
--header "Accept-Charset: utf-8" \
http://localhost:3000/v1/companies/OPEN/2015/Q2
```

Delete the company with cURL:

```console
curl -i -X DELETE http://localhost:3000/v1/companies/OPEN
```

Try to get the report and the company with cURL:

```console
curl -i -X GET \
--header "Accept: application/vnd.open-company.report+json;version=1" \
--header "Accept-Charset: utf-8" \
http://localhost:3000/v1/companies/OPEN/2015/Q2

curl -i -X GET \
--header "Accept: application/vnd.open-company.company+json;version=1" \
--header "Accept-Charset: utf-8" \
http://localhost:3000/v1/companies/OPEN
```

## License

Distributed under the [Mozilla Public License v2.0](http://www.mozilla.org/MPL/2.0/).

Copyright © 2015 Transparency, LLC