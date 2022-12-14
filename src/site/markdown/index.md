## Introduction

The dbunit-migration-maven-plugin is a tool to migrate a collection of [DBUnit](http://dbunit.sourceforge.net/) data
set files from one database schema
version to another with the use of [Flyway](https://flywaydb.org/) and
[testcontainers](https://www.testcontainers.org/).

Currently, it supports the following dataset types:

* XML
* Flat XML
* XLS (Excel)
* CSV
* SQL insert scripts

## How it works?

The overall process the plugin implements is:

1. Start a clean database with testcontainers.
2. Use Flyway to migrate the database to the version the dataset files are based on.
3. Load a dataset into the database.
4. Let Flyway migrate the database to a target version.
5. Extract the database to a dataset file.
6. Repeat for all dataset files.
