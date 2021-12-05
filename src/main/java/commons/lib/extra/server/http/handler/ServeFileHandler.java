package commons.lib.extra.server.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import commons.lib.main.os.LogUtils;
import java.util.logging.Logger;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ServeFileHandler implements HttpHandler {

    private static final Logger logger = LogUtils.initLogs();

    private final Path baseDir;
    private final String relativePath;

    public ServeFileHandler(List<String> parameters) {
        final String baseDir = "./server/src/web" ;//parameters.get(0);
        final String relativePath = parameters.get(1);
        this.baseDir = Path.of(baseDir);
        if (relativePath.startsWith("./")) {
            this.relativePath = relativePath.substring(2);
        } else {
            this.relativePath = relativePath;
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final URI requestURI = exchange.getRequestURI();
        logger.info("OK");
        logger.info(String.format("Requested URI : %s", requestURI.getPath()));
        logger.info(String.format("Looking for matching pattern : '%s'...", relativePath));
        int i = requestURI.getPath().indexOf(relativePath);
        if (i >= 0) {
            LogUtils.debug("Pattern found !");
            final String substring = requestURI.getPath().substring(i);
            final Path filePath = baseDir.resolve(substring);
            final File file = filePath.toFile();
            logger.info(String.format("Following file requested : '%s'...", file.getAbsolutePath()));
            if (file.exists()) {
                logger.info("File found !");
                try (OutputStream os = exchange.getResponseBody()) {
                    byte[] bytes = Files.readAllBytes(filePath);
                    exchange.sendResponseHeaders(200, bytes.length);
                    os.write(bytes);
                }
                return;
            }
            logger.info("File not found :(");
        }
        exchange.sendResponseHeaders(400, 0);
        exchange.getResponseBody().close();
    }
}
