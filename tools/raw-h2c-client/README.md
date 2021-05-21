# raw-h2c-client

Raw plaintext HTTP/2 client for manual tests of graceful server shutdown using GOAWAY.

Encode/decode http2 frames using the h2spec implementation instead of Go's http2 impl.
which allows different behaviour.

## Build and run

```
go run main.go
```
