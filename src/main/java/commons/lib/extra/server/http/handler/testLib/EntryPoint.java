package commons.lib.extra.server.http.handler.testLib;

import com.sun.net.httpserver.*;
import commons.lib.extra.server.http.handler.SelfDescribeHandler;
import commons.lib.extra.server.http.handler.testLib.example.MyEndpoints;
import commons.lib.extra.server.http.handler.testLib.generators.DocumentedEndpoint;
import commons.lib.extra.server.http.handler.testLib.generators.EndpointGenerator;
import commons.lib.extra.server.http.handler.testLib.generators.JsGenerator;
import commons.lib.main.SystemUtils;
import commons.lib.main.console.v1.CliParameterLoader;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntryPoint {
    public static void main(String[] args) throws IOException, UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        final CliParameterLoader cliLoader = new CliParameterLoader(Map.of(), Map.of("-p", "Listening port.", "-t",
                "Number of thread the server has to handle.", "--ksPath",
                "Path where the Java Key Store (jks file) is. This parameter is required if you want to enable encrypted communication (HTTPS).",
                "--ksPass", "The Java Key Store password. It's required if you passed a jks path."),
                Map.of("--ksPath", "--ksPass"));
        // TODO --self-describe
        // TODO --gen-js-path
        final Set<String> providedParameters = Stream.of(args).collect(Collectors.toSet());
        final Map<String, String> parameters = cliLoader.load(args);
        if (providedParameters.contains("-h")) {
            System.out.println(parameters.get("-h"));
            SystemUtils.endOfApp();
        }
        final int port = Integer.parseInt(parameters.getOrDefault("-p", "8080"));
        final int threadCount = Integer.parseInt(parameters.getOrDefault("-t", "5"));
        //final String ksPath = parameters.getOrDefault("-ksPath", "e:/dev/intellij/commons-lib/testkey.jks");
        //final String ksPass = parameters.getOrDefault("-ksPass", "mypassword1");
        loadConfigAndStartWebServer(port, threadCount, null, null);
    }

    private static void loadConfigAndStartWebServer(int port, int nThreads, String keyStorePath, char[] storePassKey)
            throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        final MyEndpoints instanceToProcess = new MyEndpoints();
        final List<DocumentedEndpoint> endpointsDocs = new ArrayList<>();
        final StringBuilder jsScript = new StringBuilder();
        final Map<String, HttpHandler> handlers = EndpointGenerator.loadHttpHandlers(
                List.of(instanceToProcess),
                List.of(
                        endpointsDocs::add,
                        doc -> {
                            jsScript.append(JsGenerator.generateJsCall(doc));
                        }
                )
        );
        handlers.put("/", new SelfDescribeHandler(endpointsDocs));
        boolean initTls = storePassKey != null;
        final HttpServer server;
        if (initTls) {
            server = HttpsServer.create(new InetSocketAddress(port), 0);
            initSSL((HttpsServer) server, storePassKey, keyStorePath);
        } else {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        }

        server.setExecutor(Executors.newFixedThreadPool(nThreads));
        handlers.forEach(server::createContext);
        server.start();
        System.out.printf("Accessible : %s://127.0.0.1:%d/\n", initTls ? "https" : "http", port);
    }


    /*
     * // In order to initial HTTPS you need to generate a certificate. We need to
     * repeat the same password twice
     * keytool -genkeypair -keyalg RSA -alias selfsigned -keystore testkey.jks -storepass mypassword1 -keypass whatever -validity 360 -keysize 2048 -deststoretype pkcs12
     * need to test :
     * -deststorepass:file /PATH/FILE -deststorepass:env ENV_NAME
     */
    private static void initSSL(HttpsServer httpsServer, char[] storePassKey, String keyStorePath)
            throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        // Initialise the HTTPS server
        final SSLContext sslContext = SSLContext.getInstance("TLS");

        // Initialise the keystore
        final KeyStore ks = KeyStore.getInstance("PKCS12");
        final FileInputStream fis = new FileInputStream(keyStorePath);
        ks.load(fis, storePassKey);
        // Set up the key manager factory
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, storePassKey);

        // Set up the trust manager factory
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        // Private keys Public keys
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // Initialise the SSL context
                    final SSLContext c = SSLContext.getDefault();
                    final SSLEngine engine = c.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Get the default parameters
                    final SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                    params.setSSLParameters(defaultSSLParameters);
                } catch (Exception ex) {
                    System.out.println("Failed to create HTTPS port");
                }
            }
        });
    }
}
