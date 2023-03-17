package commons.lib.extra.server.http.handler.testLib;

import com.sun.net.httpserver.*;
import commons.lib.extra.server.http.handler.SelfDescribeHandler;
import commons.lib.extra.server.http.handler.testLib.example.MyEndpoints;
import commons.lib.extra.server.http.handler.testLib.generators.DocumentedEndpoint;
import commons.lib.extra.server.http.handler.testLib.generators.EndpointGenerator;
import commons.lib.extra.server.http.handler.testLib.generators.JsGenerator;
import commons.lib.main.SystemUtils;
import commons.lib.main.console.v1.CliParameterLoader;
import commons.lib.main.console.v3.AppInfo;
import commons.lib.main.os.LogUtils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntryPoint {
	private static Logger logger = LogUtils.initLogs();

	public static void main(String[] args) throws IOException, UnrecoverableKeyException, CertificateException,
			NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		final CliParameterLoader cliLoader = new CliParameterLoader(Map.of(), Map.of(//
				"-p", "Listening port.", //
				"-t", "Number of thread the server has to handle.", //
				"--ksPath",
				"Path where the Java Key Store (jks file) is. This parameter is required if you want to enable encrypted communication (HTTPS).", //
				"--ksPass", "The Java Key Store password. It's required if you passed a jks path.",//
				"--self-describe", "Expose an endpoint to list all existing endpoints.", //
				"--videos-path", "A path where all videos will be available for streaming.", //
				"--gen-js-path", "Generate a Javascript library to allow web client to call this server." //
				),
				Map.of("--ksPath", "--ksPass"));
		final Set<String> providedParameters = Stream.of(args).collect(Collectors.toSet());
		final Map<String, String> parameters = cliLoader.load(args);
		if (providedParameters.contains("-h")) {
			System.out.println(parameters.get("-h"));
			SystemUtils.endOfApp();
		}
		final int port = Integer.parseInt(parameters.getOrDefault("-p", "8080"));
		final int threadCount = Integer.parseInt(parameters.getOrDefault("-t", "5"));
		// final String ksPath = parameters.getOrDefault("-ksPath",
		// "e:/dev/intellij/commons-lib/testkey.jks");
		// final String ksPass = parameters.getOrDefault("-ksPass", "mypassword1");
		loadConfigAndStartWebServer(port, threadCount, null, null);
	}

	private static void loadConfigAndStartWebServer(int port, int nThreads, String keyStorePath, char[] storePassKey)
			throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException,
			UnrecoverableKeyException, KeyManagementException {
		final MyEndpoints instanceToProcess = new MyEndpoints();
		final List<DocumentedEndpoint> endpointsDocs = new ArrayList<>();
		final StringBuilder jsScript = new StringBuilder();
		jsScript.append(JsGenerator.asyncCallSource());
		final Map<String, HttpHandler> handlers = EndpointGenerator.loadHttpHandlers(List.of(instanceToProcess),
				List.of(endpointsDocs::add, doc -> {
					jsScript.append(JsGenerator.generateJsCall(doc));
				}, doc -> {
					LogUtils.info("Loaded endpoint {0} {1}", doc.getHttpMethod(), doc.getPath());
				}));
		final String jsSourceCode = jsScript.toString();
		LogUtils.info("Initializing self describer endpoint...");
		handlers.put("/", new SelfDescribeHandler(endpointsDocs));
		LogUtils.info("Initializing Javascript library...");
		handlers.put("/fds.js", new HttpHandler() {
			public void handle(HttpExchange httpExchange) throws IOException {
				httpExchange.sendResponseHeaders(200, jsSourceCode.length());
				final OutputStream os = httpExchange.getResponseBody();
				os.write(jsSourceCode.getBytes());
				os.close();
			}
		});
		boolean initTls = storePassKey != null;
		final HttpServer server;
		if (initTls) {
			LogUtils.info("Initializing HTTP over TLS on port {0}...", port);
			server = HttpsServer.create(new InetSocketAddress(port), 0);
			initSSL((HttpsServer) server, storePassKey, keyStorePath);
		} else {
			LogUtils.info("Initializing HTTP on port {0}...", port);
			server = HttpServer.create(new InetSocketAddress(port), 0);
		}

		LogUtils.info("Create thread pool with a capacity of {0}...", nThreads);
		server.setExecutor(Executors.newFixedThreadPool(nThreads));
		handlers.forEach(server::createContext);
		server.start();
		LogUtils.info("Accessible : {0}://127.0.0.1:{1}/\n", initTls ? "https" : "http", port);
	}

	/*
	 * // In order to initial HTTPS you need to generate a certificate. We need to
	 * repeat the same password twice keytool -genkeypair -keyalg RSA -alias
	 * selfsigned -keystore testkey.jks -storepass mypassword1 -keypass whatever
	 * -validity 360 -keysize 2048 -deststoretype pkcs12 need to test :
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
					ex.printStackTrace();
				}
			}
		});
	}
}
