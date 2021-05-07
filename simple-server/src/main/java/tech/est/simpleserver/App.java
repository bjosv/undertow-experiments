package tech.est.simpleserver;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.util.Headers;
import io.undertow.util.Protocols;

import org.jboss.logging.Logger;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class);
    private static final int PORT = 7777;
    private static final String HOST = "localhost";

    private Undertow server;
    private GracefulShutdownHandler shutdownHandler;

    public static void main(final String[] args) {

        App app = new App();

        LOGGER.info(">>> Start server at: " + HOST + ":" + PORT);
        app.createServer();

        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            System.out.println(e);
        }

        LOGGER.info(">>> Stop server");
        app.stop();
        LOGGER.info(">>> Stop server - DONE");
    }

    public void createServer() {
        HttpHandler requestHandler = new HttpHandler() {
            @Override
            public void handleRequest(final HttpServerExchange exchange) throws Exception {
                LOGGER.info(">>> Handle request from: " + exchange.getConnection().getPeerAddress().toString());
                if (!exchange.getProtocol().equals(Protocols.HTTP_2_0)) {
                    throw new RuntimeException("Not HTTP/2");
                }
                LOGGER.infof(">>> isPersistent() = %b", exchange.isPersistent());
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Hello World");
            }
        };
        shutdownHandler = Handlers.gracefulShutdown(requestHandler);

        // h2 without TLS/SSL
        server = Undertow.builder()
            .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
            .setServerOption(UndertowOptions.SHUTDOWN_TIMEOUT, 10000)
            .addHttpListener(PORT, HOST)
            .setHandler(shutdownHandler).build();

        server.start();
    }

    public void stop() {
        LOGGER.info(">>> STOP called");

        LOGGER.info(">>> CALL shutdown");
        shutdownHandler.shutdown();

        LOGGER.info(">>> CALL await shutdown");
        try {
            shutdownHandler.awaitShutdown(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info(">>> CALL await shutdown - DONE");

        server.stop();
    }
}
