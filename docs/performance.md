# Performance

## Tools

### h2load
```
git clone git@github.com:nghttp2/nghttp2.git
cd nghttp2
sudo apt-get install g++ make binutils autoconf automake autotools-dev libtool pkg-config \\n  zlib1g-dev libcunit1-dev libssl-dev libxml2-dev libev-dev libevent-dev libjansson-dev \\n  libc-ares-dev libjemalloc-dev libsystemd-dev \\n  cython python3-dev python-setuptools
git submodule update --init
autoreconf -i
automake
autoconf
./configure
make -j
```


## Prepare

### Remove debug logs

Update
`simple-server/src/main/resources/logging.properties`

and rebuild jar package containing logging.properties
`mvn -U -B -fae -DskipTests clean install`

### Use correct library (legacy or a changed one)
Update `simple-server/pom.xml`

```
  <dependencies>
    <dependency>
      <groupId>io.undertow</groupId>
      <artifactId>undertow-core</artifactId>
      <version>2.2.7.Final</version>
or
      <version>2.2.8.Final-SNAPSHOT</version>
```


## Run tests

java -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar target/simple-server-1.0-SNAPSHOT.jar -c 2>&1 | tee log.txt

./src/h2load -n1000000 -c100 -m100 https://localhost:7777/


## Test results

### Using legacy undertow without GracefulShutdown
#### ./src/h2load -n100000000 -c100 -m100 https://localhost:7777/
finished in 437.27s, 228692.61 req/s, 15.69MB/s
requests: 100000000 total, 100000000 started, 100000000 done, 100000000 succeeded, 0 failed, 0 errored, 0 timeout
status codes: 100000000 2xx, 0 3xx, 0 4xx, 0 5xx
traffic: 6.70GB (7196004702) total, 2.98GB (3200001100) headers (space savings 60.49%), 2.05GB (2196000002) data
                     min         max         mean         sd        +/- sd
time for request:     1.28ms    126.35ms     42.84ms      4.61ms    74.57%
time for connect:    27.05ms    142.38ms     72.91ms     29.32ms    51.00%
time to 1st byte:    64.57ms    186.26ms    114.19ms     31.18ms    56.00%
req/s           :    2286.94     2289.46     2287.67        0.66    73.00%

### Using legacy undertow with GracefulShutdown
#### ./src/h2load -n100000000 -c100 -m100 https://localhost:7777/
finished in 441.13s, 226688.95 req/s, 15.59MB/s
requests: 100000000 total, 100000000 started, 100000000 done, 100000000 succeeded, 0 failed, 0 errored, 0 timeout
status codes: 100000000 2xx, 0 3xx, 0 4xx, 0 5xx
traffic: 6.71GB (7210004701) total, 2.98GB (3200001100) headers (space savings 60.49%), 2.06GB (2210000001) data
                     min         max         mean         sd        +/- sd
time for request:     1.04ms    114.87ms     43.10ms      4.62ms    71.84%
time for connect:    45.05ms    100.30ms     80.41ms     15.08ms    67.00%
time to 1st byte:    83.84ms    140.32ms    119.91ms     15.16ms    66.00%
req/s           :    2266.92     2269.59     2267.61        0.57    74.00%

finished in 438.27s, 228171.25 req/s, 15.66MB/s
requests: 100000000 total, 100000000 started, 100000000 done, 100000000 succeeded, 0 failed, 0 errored, 0 timeout
status codes: 100000000 2xx, 0 3xx, 0 4xx, 0 5xx
traffic: 6.70GB (7196004703) total, 2.98GB (3200001100) headers (space savings 60.49%), 2.05GB (2196000003) data
                     min         max         mean         sd        +/- sd
time for request:      845us    144.10ms     42.89ms      4.43ms    73.34%
time for connect:    37.26ms    129.76ms     95.04ms     20.44ms    69.00%
time to 1st byte:    89.92ms    170.76ms    139.02ms     15.50ms    67.00%
req/s           :    2281.73     2283.91     2282.39        0.51    70.00%

### Using updated GracefulHandler
#### ./src/h2load -n100000000 -c100 -m100 https://localhost:7777/
finished in 443.25s, 225603.90 req/s, 15.48MB/s
requests: 100000000 total, 100000000 started, 100000000 done, 100000000 succeeded, 0 failed, 0 errored, 0 timeout
status codes: 100000000 2xx, 0 3xx, 0 4xx, 0 5xx
traffic: 6.70GB (7196004702) total, 2.98GB (3200001100) headers (space savings 60.49%), 2.05GB (2196000002) data
                     min         max         mean         sd        +/- sd
time for request:      954us    126.46ms     43.39ms      4.72ms    72.79%
time for connect:    49.05ms    119.06ms     93.19ms     19.51ms    68.00%
time to 1st byte:    99.74ms    164.23ms    136.20ms     18.31ms    64.00%
req/s           :    2256.06     2258.21     2256.79        0.56    61.00%
