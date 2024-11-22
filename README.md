# kosa [m.]: _store-room; treasury_

[![Kosa Backend Tests](https://github.com/pariyatti/kosa/actions/workflows/kosa-tests.yml/badge.svg)](https://github.com/pariyatti/kosa/actions/)

**⚠️ ⚠️ ⚠️ PLEASE NOTE: This service is deprecated in favour of [Kosa2](https://github.com/pariyatti/kosa2) ⚠️ ⚠️ ⚠️**

Pariyatti's Library and Mobile services.

## Mobile App API

The Pariyatti mobile app will consume the API as specified [here](https://github.com/pariyatti/kosa/blob/master/docs/api.md).

### Option 1: Use the Sandbox server

[http://kosa-sandbox.pariyatti.org](http://kosa-sandbox.pariyatti.org) - Use this option if you will working exclusively on the mobile app without modifying or debugging the server.

### Option 2: Set up a local development server

Follow the instructions under [Development](https://github.com/pariyatti/kosa#development). Use this option if you need to modify or debug the server itself.

## Deployment

Follow the [Ops Readme](https://github.com/pariyatti/kosa/blob/master/ops/README.md).

## Quick Start

To the kosa api in development mode, you can use docker-compose. Please note that it uses tcp ports 3000 and 9999 on localhost while running. If these are already occupied in your local machine, please adjust the docker-compose.yml ports locally.

To run the container image in dev mode:

`docker-compose up`

This will pull the latest image from [Docker Hub](https://hub.docker.com/repository/docker/pariyatti/kosa-dev/tags?page=1&ordering=last_updated) and start kosa locally.

If you are making changes locally and would like to test that in docker as well, there is a variant of docker-compose file that can be used as well:

`docker-compose -f docker-compose-local.yml up`

## Development

### Install Java

Kosa will work with any Java 11 or higher. Java 17 is the latest LTS, and is preferred.

Install OpenJDK 17 for:

- [Mac OS](https://gist.github.com/deobald/00b16090a932c793379cae6422206491)
- Linux: `sudo apt-get install openjdk-17-jdk`

WARNING: <https://github.com/xtdb/xtdb/issues/1462> may cause:

```
Could not open ConcurrentHashMap.table field - falling back to LRU caching. Use '--add-opens java.base/java.util.concurrent=ALL-UNNAMED' to use the second-chance cache.
```

Use Java as executed in the Makefile to avoid this.

### Install Clojure

Install `clojure` as described [here](https://www.clojure.org/guides/getting_started)

### Install Leinengen

Install `lein` as described [here](https://leiningen.org/#install)

### Install NPM

Install `npm` (nodejs LTS) as described [here](https://nodejs.org/en/download/). Sorry.

### Add Secrets

Create a Gmail [app password](https://myaccount.google.com/apppasswords) and add the
following to `~/.kosa/secrets.edn` on your local machine:

```clojure
{:mailer {:user "YOUR_NAME@gmail.com"
          :pass "GMAIL_APP_PASSWORD"
          :default-options {:to "some-other@email.com"
                            :tls true
                            :port 587}}}
```

Add the Ansible password from the KeePass Vault to:

```
~/.kosa/ansible-password
```

### Build kosa

If you intend to use the "looped cards" (Pali Word a Day, Daily Words
of the Buddha, or Daily Dohas), you will need access to the private
repository containing the input files (<https://github.com/pariyatti/Daily_emails_RSS>)
before you can run `make txt-clone`, as explained below. Speak to Pariyatti Staff to
obtain access. If you do not have access to these files, Kosa will still run
without them.

To see "looped cards" published to the mobile app, find `{:job-name :txt-publisher-*}`
in `kosa/config/config.{env}.edn` and set `{:enabled true}`.

```shell
git clone git@github.com:pariyatti/kosa.git
cd kosa

make help # see all the build commands
make init # setup your system

make test # run the tests (did you install JDK 11?)

make db-clean      # delete any old db files you might have
make db-migrate    # install db schema
make db-seed       # add some sample data (optional)

make txt-clean     # completely reset TXT files (optional)
make txt-clone     # clone+copy TXT files for looped cards (optional)
                   # NOTE: this command only works if you have
                   #       access to the private repo described above
make db-txt-pali   # add looped pali word cards (optional)
make db-txt-buddha # add looped buddha word cards (optional)
make db-txt-doha   # add looped doha word cards (optional)

make run           # run the server (or)
make repl          # run the server interactively
```

### Try it out

**To add sample data from a REPL:**

```clojure
dev.repl> (migrate)
dev.repl> (seed)
```

Once you have run these, close the REPL and restart it or your XTDB node will be locked. Migrations work from the command line but seeding does not at the moment.

**To login:**

```
username: admin
password: <found in the KeePass vault>
```

## Design Wireframes

- [Mobile (Admin)](https://whimsical.com/4tTbGHDiYkYXj7cUnTBSTb)
- [Library](https://whimsical.com/6LN2LDkv1bRyyuojyiJ8oV)

## Architectural Thinking

- [Architecture Decision Records (ADRs)](https://github.com/pariyatti/kosa/tree/master/docs/arch)
- [Pariyatti Network Topology](https://github.com/pariyatti/agga/blob/master/docs/network-topology.pdf)
- [The Pariyatti Data Model](https://github.com/pariyatti/agga/blob/master/docs/data-models.pdf)
- [What is a (relational) database?](https://docs.google.com/document/d/1QuiWPaAUH9_UOeBouGGCgF_FyRRhoL4uLkfKvSsbw2o/edit#)
- [What is a graph database?](https://neo4j.com/developer/graph-database/)

## Old RSS Feeds

- [Dhamma Podcast Feed](http://feeds.pariyatti.org/dhammapodcasts)
- [Daily RSS Feed](https://www.pariyatti.org/Free-Resources/Daily-Words/RSS-Feeds)

## License and Copyright

AGPL-3
Copyright (c) 2019-present, Pariyatti
