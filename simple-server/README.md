# simple-server using Undertow

## Build

## Build and install Undertow

```
cd <undertow repo>
mvn -U -B -fae -DskipTests -pl core install`
```

Will be installed to ~/.m2/repository/io/undertow/undertow-core/<VERSION>

## Build simple-server

```
mvn -U -B -fae -DskipTests clean install
```


## Run

```
java -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar target/simple-server-1.0-SNAPSHOT.jar

alt.

java -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar target/simple-server-1.0-SNAPSHOT.jar 2>&1 | tee log.txt
```
