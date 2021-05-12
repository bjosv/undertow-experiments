# h2load - HTTP/2 benchmarking tool

From the HTTP/2 C Library and tools
https://github.com/nghttp2/nghttp2

## Run (via container)

docker run --network=host --rm -t svagi/h2load -n100000 -c1 -m1 -p h2c http://127.0.0.1:7777/test

## Observations

* h2load disconnects after initial GOAWAY(MAX)

* Not stopping due to requests resulting in HTTP errors, like 5XX.
