# h2c-client

Simple plaintext HTTP/2 client for manual tests of graceful server shutdown using GOAWAY.

## Build and run

```
env GODEBUG=http2debug=2 go run main.go
```

Levels:

```
GODEBUG=http2debug=1   # enable verbose HTTP/2 debug logs
GODEBUG=http2debug=2   # ... even more verbose, with frame dumps

GODEBUG=httpdebug=X   # enable verbose HTTP debug logs
```
