# Config Options

## Dataset Options

```xml
<dataSets>
    <columnSensing>true</columnSensing>
    <charset>UTF-8</charset>

    <resources>
        <resource>src/test/resources/tiny-sakila-csv</resource>
        <resource>glob:**/*.xml</resource>
        <resource>glob:*.xml</resource>
        <resource>glob:**/*.xls</resource>
        <resource>glob:*.xls</resource>
    </resources>

    <tableOrder>
        <tableorder>language</tableorder>
        <tableorder>film</tableorder>
        <tableorder>actor</tableorder>
        <tableorder>film_actor</tableorder>
    </tableOrder>
</dataSets>
```

| Option   | Description  |
|--- |:----|
| columnSensing | Turns on column sensing for flat xml datasets.<br/><br/>For details take a look at [DBUnit FAQ](https://dbunit.org/faq.html#differentcolumnnumber)|
| resources | A list of `<resource`> entries that describe which dataset resource should be processed. When the resource path starts with `glob:` it is interpreted as a glob pattern. For details take a look at  [FileSystem.getPathMatcher(String))](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-)  |
| tableOrder | A list of `<tableOrder`> entries that describe which table processing order. Can be used if foreign key constrains make trouble. The configured table order is also the order of the tables in the output datasets.  |


## Flyway Options


```xml
<flyway>
    <sourceVersion>1</sourceVersion>
    <targetVersion>2</targetVersion>
    <locations>
        <location>\${project.basedir}/src/main/resources/db/migration</location>
    </locations>
    <placeholders>
        <new_first_name_column_name>firstname</new_first_name_column_name>
        <new_last_name_column_name>lastname</new_last_name_column_name>
    </placeholders>
</flyway>
```
| Option        | Description                                                                                                                                                                                                                       |
|---------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| sourceVersion | The source database schema version the dataset files are based on.                                                                                                                                                                |
| targetVersion | The target database schema version that the dataset files should be migrated to.                                                                                                                                                  |
| locations    | A list of `<location`> entries where the plugin should look for flyway migration files.<br/><br/>For details take a look at [Flayway Locations](https://flywaydb.org/documentation/configuration/parameters/locations)                     |
| placeholders    | A list of entries where the tag name is the placeholder name and the tag content the placeholder replacement.<br/><br/>For details take a look at [Flyway Placeholders](https://flywaydb.org/documentation/configuration/placeholder) | 


## Testcontainers Options

```xml
<testcontainers>
    <image>postgres:latest</image>
</testcontainers>
```
| Option        | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|---------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| image | The testcontainers image to use to set up a migration database. The `dbunit-migration-maven-plugin` depends on the [`lis-dbunit-testcontainers`](https://mvnrepository.com/artifact/com.link-intersystems.dbunit/lis-dbunit-testcontainers) library that has support for postgres and mysql containers.<br/><br/> If you need support for other containers you can use a `containerConfig` entry or implement your own `com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupportProvider` and make it available via `META-INF/services/com.link_intersystems.dbunit.testcontainers.DatabaseContainerSupportProvider`. <br/>For details take a look at [DatabaseContainerSupportProvider](https://github.com/link-intersystems/dbunit-extensions/blob/master/lis-dbunit-testcontainers/lis-dbunit-testcontainers-core/src/main/java/com/link_intersystems/dbunit/testcontainers/DatabaseContainerSupportProvider.java)         |

### Testcontainers Container Configuration Options

```xml
<testcontainers>
    <containerConfig>
        <dataSource>
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
```
| Option                                     | Description                                                                                                                                                                                                                                                                                                                                                                                               |
|--------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **dataSource**                             | The main configuration element for the data source that the migration plugin should use                                                                                                                                                                                                                                                                                                                   |
| dataSource.**driverClassName**             | The full qualified name of the Driver class.                                                                                                                                                                                                                                                                                                                                                              |
| dataSource.**jdbcUrl**                     | The jdbc url that the plugin should use to connect to the testcontainer. The follwing variables are substituted by the values provided by the testcontainer: `host`, `port`, `env.ENVIRONMENT_VAR_NAME`                                                                                                                                                                                                   |
| dataSource.**username**                    | The username to connect to the database container. The same variable substitution as for dataSource.jdbcurl is supported.                                                                                                                                                                                                                                                                                 |
| dataSource.**password**                    | The password to connect to the database container. The same variable substitution as for dataSource.jdbcurl is supported.                                                                                                                                                                                                                                                                                 |
| dataSource.**testQueryString**             | An sql statement to check if the database is available.                                                                                                                                                                                                                                                                                                                                                   |
| **dockerContainer**                        | The main configuration element for the docker container to use.                                                                                                                                                                                                                                                                                                                                           |
| dockerContainer.**exposedPort**            | The port of the container that should be exposed. This is not the same as the port that is used by the variable substitution `{{port}}`. The exposed port is the port within the container that should be exposted to the outside. Testcontainers selects a port that is available for access from the outside. This selected port is the port that is available by the substitution variable `{{port}}`. |
| dockerContainer.**env**                    | The container environment variables configuration. The tag name of each entry is the environment variable name and the content the value.                                                                                                                                                                                                                                                                 |
| dockerContainer.**command**                | The command that should be used when starting the container. If no command is defined the docker image's entry point is used.                                                                                                                                                                                                                                                                             |
| dockerContainer.**dbunitConfigProperties** | DBUnit configuration for the database container. You can use all DBUnit configuration options listed [here](http://dbunit.sourceforge.net/dbunit/properties.html).                                                                                                                                                                                                                                                                                            |

