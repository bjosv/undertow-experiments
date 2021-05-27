# Setup environment

### Install JDK

```
sudo apt update
sudo apt install default-jdk
javac -version
```

### Setup JAVA_HOME path
`sudo update-alternatives --config java`

Add to ~/.zshrc
`export JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"`

or to /etc/environment
`JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"`


### Install maven
`sudo apt install maven`

Check version, needs >= 3.1
`mvn -version`

Maven creates:
~/.m2/


### Build command examples

```
> Clean previous builds
mvn clean
> Compile and package
mvn package
> Compile and package, put package in local repo
mvn install
> As above, but with forced checks, batch mode and without running tests
mvn -U -B -fae -DskipTests clean install

> Only build project part `core`
mvn -U -B -fae -DskipTests -pl core install
```

Maven order of execution:
> validate - validate the project is correct and all necessary information is available
> compile - compile the source code of the project
> test - test the compiled source code using a suitable unit testing framework. These tests should not require the code be packaged or deployed
> package - take the compiled code and package it in its distributable format, such as a JAR.
> verify - run any checks on results of integration tests to ensure quality criteria are met
> install - install the package into the local repository, for use as a dependency in other projects locally
> deploy - done in the build environment, copies the final package to the remote repository for sharing with other developers and projects.

### Test examples

```
> Run all tests in `core` (656 + 6)
mvn -pl core test

> Run all tests in `servlet` (302 + 2)
mvn -pl servlet test

> Run all unittests in `core` (212 + 1)
mvn -P only-unit-tests -pl core test

> Run non-unittests in `core` (444 + 5)
mvn -P skip-unit-tests -pl core test

> Run test cases in specified class
mvn -pl core -Dtest=io.undertow.server.handlers.GracefulShutdownTestCase test

> Run single test case
mvn -pl core -Dtest=io.undertow.server.handlers.GracefulShutdownTestCase#simpleGracefulShutdownTestCase test
```

### Test logs

```
> Logs from test class:
core/target/surefire-reports/io.undertow.server.handlers.GracefulShutdownTestCase-output.txt

> Logs from non-test code:
core/target/surefire-reports/null-output.txt

> Modify log levels
core/src/test/resources/logging.properties
```

### Profiles

### spotbugs

```
> Run and open GUI
mvn -P spotbugs -pl core spotbugs:gui
```

### Create a project example

* Perform the `mvn archetype:generate` step describe in:
  https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

* Update java version in pom
  https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html#java-9-or-later

* Add deps
  https://undertow.io/undertow-docs/undertow-docs-2.1.0/index.html#introduction-to-undertow
  <dependency>
     <groupId>io.undertow</groupId>
     <artifactId>undertow-core</artifactId>
     <version>2.2.7.Final</version>
  </dependency>

* Build
`mvn package`

* Run
`mvn exec:java -Dexec.mainClass=xx`
