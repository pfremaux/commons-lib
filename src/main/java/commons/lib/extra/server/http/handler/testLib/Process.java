package commons.lib.extra.server.http.handler.testLib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import commons.lib.extra.server.http.Mapping;
import commons.lib.extra.server.http.handler.SelfDescribeHandler;
import commons.lib.main.SystemUtils;
import commons.lib.main.console.v1.CliParameterLoader;
import commons.lib.tooling.documentation.MdDoc;

public class Process {

	private static List<DocumentedEndpoint> endpointsDocs = new ArrayList<>();

	public static void handleException(HttpExchange exchange, Throwable t) throws IOException {

		final String msg = t.getLocalizedMessage() + "\n"
				+ Arrays.stream(t.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
		exchange.sendResponseHeaders(400, msg.length());
		final OutputStream os = exchange.getResponseBody();
		os.write(msg.getBytes());
		os.close();
		exchange.getResponseBody().close();
	}

	public static void main(String[] args) throws IOException, UnrecoverableKeyException, CertificateException,
			NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		final CliParameterLoader cliLoader = new CliParameterLoader(Map.of(), Map.of("-p", "Listening port.", "-t",
				"Number of thread the server has to handle.", "--ksPath",
				"Path where the Java Key Store (jks file) is. This parameter is required if you want to enable encrypted communication (HTTPS).",
				"--ksPass", "The Java Key Store password. It's required if you passed a jks path."),
				Map.of("--ksPath", "--ksPass"));
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
		final Map<String, HttpHandler> handlers = loadHttpHandlers(instanceToProcess);
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
		System.out.printf("Accessible : %s://127.0.0.1:%d/toto\n", initTls ? "https" : "http", port);
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

	private static Map<String, HttpHandler> loadHttpHandlers(MyEndpoints instanceToProcess) {
		final Map<String, HttpHandler> handlers = new HashMap<>();
		Class<? extends MyEndpoints> class1 = instanceToProcess.getClass();
		final Map<String, Field> nameToField = Stream.of(class1.getDeclaredFields())
				.collect(Collectors.toMap(f -> f.getName().toLowerCase(), f -> f));
		for (Method declaredMethod : class1.getDeclaredMethods()) {

			Endpoint declaredAnnotation = declaredMethod.getDeclaredAnnotation(Endpoint.class);
			final DocumentedEndpoint documentedEndpoint = new DocumentedEndpoint();
			if (declaredAnnotation != null) {
				final String method = declaredAnnotation.method();
				final String path = declaredAnnotation.path();
				documentedEndpoint.setMethod(method);
				documentedEndpoint.setPath(path);
				final Parameter bodyParameter = Stream.of(declaredMethod.getParameters())
						.filter((Parameter p) -> !p.getType().equals(Map.class)).findFirst().orElse(null);
				if (bodyParameter == null) {
					throw new NullPointerException(
							"body attribute of @Endpoint is null for method '" + declaredMethod.getName() + "'");
				}
				System.out.println(bodyParameter.getName());
				try {
					documentedEndpoint.setBodyExample(Mapping.objectToJsonExample(bodyParameter.getType()).toString());
					documentedEndpoint
							.setResponseExample(Mapping.objectToJsonExample(declaredMethod.getReturnType()).toString());
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				final HttpHandler handler = exchange -> {
					if (!exchange.getRequestMethod().equals(method)) {
						final String msg = "Method not allowed";
						exchange.sendResponseHeaders(409, msg.length());
						final OutputStream os = exchange.getResponseBody();
						os.write(msg.getBytes());
						os.close();
						exchange.getResponseBody().close();
						return;
					}
					final Map<String, List<String>> headers = new HashMap<>();
					for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
						final String key = entry.getKey();
						final List<String> value = entry.getValue();
						headers.put(key, value);
					}

					final byte[] bytes = exchange.getRequestBody().readAllBytes();
					System.out.println("body data length = " + bytes.length);

					final String data = new String(bytes, StandardCharsets.UTF_8);

					final Object b;
					try {
						b = Mapping.trivialJsonMappingV2(new StringBuilder(data), bodyParameter.getType());
						System.out.println(b);
					} catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException
							| InstantiationException | IllegalAccessException e) {
						handleException(exchange, e);
						return;
					}

					try {
						final Object result = declaredMethod.invoke(instanceToProcess, headers, b);
						final OutputStream os = exchange.getResponseBody();
						final String responseText;
						if (result != null) {
							responseText = Mapping.objectToJson(result).toString();
						} else {
							responseText = "{}";
						}
						exchange.sendResponseHeaders(200, responseText.length());
						os.write(responseText.getBytes());
						os.close();
						exchange.getResponseBody().close();
						return;
					} catch (IllegalAccessException | InvocationTargetException e) {
						handleException(exchange, e);
						return;
					}
				};
				handlers.put(path, handler);
			}

			///////
			MdDoc declaredDoc = declaredMethod.getDeclaredAnnotation(MdDoc.class);
			if (declaredDoc != null) {
				documentedEndpoint.setDescription(Objects.requireNonNullElse(declaredDoc.description(), ""));
			}
			endpointsDocs.add(documentedEndpoint);
		}
		SelfDescribeHandler describeHandler = new SelfDescribeHandler(endpointsDocs);
		handlers.put("/", describeHandler);
		return handlers;
	}

}
