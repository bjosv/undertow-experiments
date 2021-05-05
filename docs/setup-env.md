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
> Clean previous builds
`mvn clean`
> Compile and package
`mvn package`
> Compile and package, put package in local repo
`mvn install`
> As above, but with forced checks and without running tests
`mvn -U -B -fae -DskipTests clean install`

> Only build project part `core`
`mvn -U -B -fae -DskipTests -pl core install`

Maven order of execution:
> validate - validate the project is correct and all necessary information is available
> compile - compile the source code of the project
> test - test the compiled source code using a suitable unit testing framework. These tests should not require the code be packaged or deployed
> package - take the compiled code and package it in its distributable format, such as a JAR.
> verify - run any checks on results of integration tests to ensure quality criteria are met
> install - install the package into the local repository, for use as a dependency in other projects locally
> deploy - done in the build environment, copies the final package to the remote repository for sharing with other developers and projects.
