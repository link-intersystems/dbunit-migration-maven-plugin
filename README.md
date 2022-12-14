[![Maven Site](https://img.shields.io/badge/Maven-Site-brightgreen)](https://link-intersystems.github.io/dbunit-migration-maven-plugin/)
[![Maven Central](https://img.shields.io/maven-central/v/com.link-intersystems.dbunit.maven/dbunit-migration-maven-plugin)](https://mvnrepository.com/artifact/com.link-intersystems.dbunit.maven)
![Java CI with Maven](https://github.com/link-intersystems/dbunit-migration-maven-plugin/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/link-intersystems/dbunit-migration-maven-plugin/badge.svg?branch=master)](https://coveralls.io/github/link-intersystems/dbunit-migration-maven-plugin?branch=master)
[![Open Issues](https://img.shields.io/github/issues/link-intersystems/dbunit-migration-maven-plugin)](https://github.com/link-intersystems/dbunit-migration-maven-plugin/issues)


The dbunit-migration-maven-plugin is a tool to migrate a collection of [DBUnit](http://dbunit.sourceforge.net/) data set files from one database schema
version to another with the use of [Flyway](https://flywaydb.org/) and [testcontainers](https://www.testcontainers.org/).

The overall process the plugin implements is:

1. Start a clean database with testcontainers.
2. Use Flyway to migrate the database to the version the data set files are based on.
3. Load a data set from a DBUnit file into the database.
4. Let Flyway migrate the database to a target version.
5. Use DBUnit to extract the database to a DBunit data set file.
6. Repeat for all data set files.

## Run

Run the plugin with

```
mvn com.link-intersystems.dbunit.maven:dbunit-migration-maven-plugin:1.0.3:flyway-migrate.
```

The plugin will output the files it migrates.

```shell
[info] Detected 4 data set resources to migrate
[info] ♻︎ Start migration '...\src\test\resources\flat\tiny-sakila-flat-column-sensing.xml'
[info] ✔︎ Migrated '...\target\flat\tiny-sakila-flat-column-sensing.xml'
[info] ♻︎ Start migration '...\src\test\resources\flat\tiny-sakila-flat.xml'
[info] ✔︎ Migrated '...\target\flat\tiny-sakila-flat.xml'
[info] ♻︎ Start migration '...\src\test\resources\tiny-sakila-csv'
[info] ✔︎ Migrated '...\target\tiny-sakila-csv'
[info] ♻︎ Start migration '...\src\test\resources\xml\tiny-sakila.xml'
[info] ✔︎ Migrated '...\target\xml\tiny-sakila.xml'
[info] Migrated 4 data set resources 
```

## Minimal Plugin Configuration

This minimal plugin configuration will pick up all data sets under `${project.basedir}/src/test/resources`
that are based on flyway version 1 and migrate them to `${project.build.directory}`. The source directory structure will
be maintained.

```
<build>
    <plugins>
        <plugin>
            <groupId>com.link-intersystems.dbunit.maven</groupId>
            <artifactId>dbunit-migration-maven-plugin</artifactId>
            <version>RELEASE</version>
            <configuration>
                <flyway>
                    <sourceVersion>1</sourceVersion>
                </flyway>
                <testcontainers>
                    <!-- ls-dbunit-testcontainers ships with support for postgres and mysql -->
                    <!-- If you need to use another container please read the Complete Plugin Configuration section. -->
                    <image>postgres:latest</image>
                </testcontainers>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Complete Plugin Configuration

The next section shows you all possible configuration options.

```
<build>
    <plugins>
        <plugin>
            <groupId>com.link-intersystems.dbunit.maven</groupId>
            <artifactId>dbunit-migration-maven-plugin</artifactId>
            <version>RELEASE</version>
            <configuration>
                <flyway>
                    <sourceVersion>1</sourceVersion>
                    <targetVersion>2</targetVersion>
			        <locations>
					    <location>${project.basedir}/src/main/resources/db/migration</location>
					</locations>
                    <placeholders>
                        <new_first_name_column_name>firstname</new_first_name_column_name>
                        <new_last_name_column_name>
                            lastname
                        </new_last_name_column_name>
                    </placeholders>
                </flyway>
                <dataSets>
                    <columnSensing>true</columnSensing> <!-- Turns on column sensing for flat xml data sets -->
                    <charset>UTF-8</charset> <!-- the charset used to read text based data sets -->
                    
                    <!-- the data sets to migrate --> 
                    <resources>
                        <resource>src/test/resources/tiny-sakila-csv</resource>
                        <resource>glob:**/*.xml</resource>
                        <resource>glob:*.xml</resource>
                        <resource>glob:**/*.xls</resource>
                        <resource>glob:*.xls</resource>
                    </resources>
                    
                    <!-- ensure the table order for the data sets if foreign key constrains make trouble  -->
                    <tableOrder>
                        <tableorder>language</tableorder>
                        <tableorder>film</tableorder>
                        <tableorder>actor</tableorder>
                        <tableorder>film_actor</tableorder>
                    </tableOrder>
                </dataSets>
                <testcontainers>
                    <!-- configure an arbitary jdbc container that is not supported out of the box -->
                    <containerConfig>
                        <dataSource>
                            <!-- you can use placeholders enclosed in {{PLACEHOLDER}} to access values that the container provides -->
                            <driverClassName>org.postgresql.Driver</driverClassName>
                            <jdbcUrl>jdbc:postgresql://{{host}}:{{port}}/{{env.POSTGRES_DB}}?loggerLevel=OFF</jdbcUrl>
                            <username>{{env.POSTGRES_USER}}</username>
                            <password>{{env.POSTGRES_PASSWORD}}</password>
                            <testQueryString>SELECT 1</testQueryString>
                        </dataSource>
                        <dockerContainer>
                            <exposedPort>5432</exposedPort>
                            <env>
                                <POSTGRES_DB>test</POSTGRES_DB>
                                <POSTGRES_USER>test</POSTGRES_USER>
                                <POSTGRES_PASSWORD>test</POSTGRES_PASSWORD>
                            </env>
                            <command>
                                <value>postgres</value>
                                <value>-c </value>
                                <value>fsync=off</value>
                            </command>
                        </dockerContainer>
                        <dbunitConfigProperties>
                            <!-- DBUnit configuration properties -->
                            <property>
                                <name>http://www.dbunit.org/properties/datatypeFactory</name>
                                <value>org.dbunit.ext.postgresql.PostgresqlDataTypeFactory</value>
                            </property>
                        </dbunitConfigProperties>
                    </containerConfig>
                </testcontainers>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Alternatively you can implement your own container support by implementing an 
`com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupportProvider` 
and putting it on the classpath. It is registered using a `META-INF/services/com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupportProvider`
file. 

dbunit testcontainers comes with support for postgres and mysql.
