Usage

# Minimal Plugin Configuration

This minimal plugin configuration will pick up all data sets under <<<$\{project.basedir\}/src/test/resources>>>
that are based on flyway version 1 and migrate them to to the latest flyway version. The result datasets are placed in <<<$\{project.build.directory\}>>>. The source directory structure will
be maintained.


```xml
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

# Complete Plugin Configuration

The next section shows you all possible [configuration options](./config-options.html).

```xml
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
                        <location>\${project.basedir}/src/main/resources/db/migration</location>
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