# Http2 testcases

### Conformance test using h2spec
./h2spec -t -k -p 7777

#### Results
Hypertext Transfer Protocol Version 2 (HTTP/2)
  6. Frame Definitions
    6.9. WINDOW_UPDATE
      6.9.1. The Flow-Control Window
        using source address 127.0.0.1:44392
        × 3: Sends multiple WINDOW_UPDATE frames increasing the flow control window to above 2^31-1 on a stream
          -> The endpoint MUST sends a RST_STREAM frame with a FLOW_CONTROL_ERROR code.
             Expected: RST_STREAM Frame (Error Code: FLOW_CONTROL_ERROR)
               Actual: DATA Frame (length:17, flags:0x01, stream_id:1)

HPACK: Header Compression for HTTP/2
  5. Primitive Type Representations
    5.2. String Literal Representation
      using source address 127.0.0.1:44486
      × 3: Sends a Huffman-encoded string literal representation containing the EOS symbol
        -> The endpoint MUST treat this as a decoding error.
           Expected: GOAWAY Frame (Error Code: COMPRESSION_ERROR)
                     Connection closed
             Actual: DATA Frame (length:17, flags:0x01, stream_id:1)



### Undertow testscases

servlet -> PushPromisesTestCase.java

## UT:    @Category(UnitTest.class)
core/src/test/java/io/undertow/protocols/http2/HpackHuffmanEncodingUnitTestCase.java
 - encode & decode: check if buffer size ok
core/src/test/java/io/undertow/protocols/http2/HpackSpecExamplesUnitTestCase.java
 - 11 tests from spec

## Non-UT:
core/src/test/java/io/undertow/server/handlers/proxy/LoadBalancingProxyHTTP2TestCase.java
core/src/test/java/io/undertow/server/handlers/proxy/LoadBalancingProxyHTTP2ViaUpgradeTestCase.java
core/src/test/java/io/undertow/server/protocol/http2/HTTP2ViaUpgradeTestCase.java (2014)

> mvn -pl core -Dtest=io.undertow.server.protocol.http2.Http2EndExchangeTestCase test
> ADD TO loggers:  io.undertow.server.protocol.http2.Http2EndExchangeTestCase
> ADD LINE:        logger.io.undertow.server.protocol.http2.Http2EndExchangeTestCase.level=DEBUG

core/src/test/java/io/undertow/server/protocol/http2/Http2EndExchangeTestCase.java (2020)
- 1 test: testHttp2EndExchangeWithBrokenConnection(
- @HttpOneOnly ??
- start server (ENABLE HTTP2)
  handleRequest: check http2, sleep 2s -> request should have ended in background
- create address
- create worker
- create SSL client
- Connection thread: send request
  completed: wait 10s, close connection
