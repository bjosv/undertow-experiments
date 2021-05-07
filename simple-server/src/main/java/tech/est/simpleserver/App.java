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
import org.xnio.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class);
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private static final String SERVER_KEY_STORE = "server.keystore";
    private static final String SERVER_TRUST_STORE = "server.truststore";
    private static final char[] STORE_PASSWORD = "password".toCharArray();

    private Undertow server;
    private GracefulShutdownHandler shutdownHandler;

    public static void main(final String[] args) {

        // NOT WORKING!?
        // Enabling using: logging.properties
        System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");

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
        SSLContext sslContext;
        try {
            sslContext = createSSLContext(loadKeyStore(SERVER_KEY_STORE), loadKeyStore(SERVER_TRUST_STORE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

        // // h2 with TLS/SSL only
        // server = Undertow.builder().setServerOption(UndertowOptions.ENABLE_HTTP2,
        // true)
        // .addHttpsListener(PORT, HOST,
        // sslContext).setHandler(shutdownHandler).build();

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

        LOGGER.info(">>> CALL stop server");
        server.stop();
    }

    private static KeyStore loadKeyStore(final String name) throws IOException {
        final InputStream stream = App.class.getClassLoader().getResourceAsStream(name);
        if (stream == null) {
            throw new RuntimeException("Could not load keystore: " + name);
        }

        try {
            KeyStore loadedKeystore = KeyStore.getInstance("JKS");
            loadedKeystore.load(stream, STORE_PASSWORD);
            return loadedKeystore;

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new IOException(String.format("Unable to load KeyStore %s", name), e);
        } finally {
            IoUtils.safeClose(stream);
        }
    }

    private static SSLContext createSSLContext(final KeyStore keyStore, final KeyStore trustStore) throws IOException {
        KeyManager[] keyManagers;
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, STORE_PASSWORD);
            keyManagers = keyManagerFactory.getKeyManagers();
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new IOException("Unable to initialise KeyManager[]", e);
        }

        TrustManager[] trustManagers;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            trustManagers = trustManagerFactory.getTrustManagers();
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new IOException("Unable to initialise TrustManager[]", e);
        }

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IOException("Unable to create and initialise the SSLContext", e);
        }
        // Use SNI?
        // SNIContextMatcher matcher = new
        // SNIContextMatcher.Builder().setDefaultContext(sslContext)
        // .addMatch("localhost", sslContext).build();
        // return new SNISSLContext(matche
        // );

        return sslContext;
    }
}
