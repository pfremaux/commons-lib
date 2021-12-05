package commons.lib.extra.server.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import commons.lib.main.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Html5VideoStreaming implements HttpHandler {

    private static final int BUFFER_SIZE = 4096;

    private static final Logger logger = LoggerFactory.getLogger(Html5VideoStreaming.class);

    private final Path baseDir;
    private final String token;

    public Html5VideoStreaming(List<String> parameters) {
        final String baseDir = parameters.get(0);
        this.baseDir = Path.of(baseDir);
        this.token = parameters.get(1); // keyword that must be present in the url and that will allow us to split the URL
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final URI requestURI = exchange.getRequestURI();
        final List<String> rangeValues = exchange.getRequestHeaders().get("Range");
        int startingPointer = 0;
        int endingPointer = -1;
        if (rangeValues != null) {
            logger.warn("Range: {}", rangeValues.get(0));
            String rangeParameters = rangeValues.get(0).substring("bytes=".length());
            String strStartingPointer = rangeParameters.substring(0, rangeParameters.indexOf("-"));
            startingPointer = Integer.parseInt(strStartingPointer);
            String strEndingPointer = rangeParameters.substring(rangeParameters.indexOf("-"));
            if (StringUtils.isNumber(strEndingPointer)) {
                endingPointer = Integer.parseInt(strStartingPointer);
            } else {
                endingPointer = startingPointer + BUFFER_SIZE * 100;
            }
        } else {
            for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
                logger.warn("{} -> {}", entry.getKey(), entry.getValue());
            }
        }

        logger.warn("OK");
        logger.warn("Requested URI : {}", requestURI.getPath());
        logger.warn("Looking for matching pattern : '{}'...", token);
        int i = requestURI.getPath().indexOf(token);
        if (i >= 0) {
            logger.debug("Pattern found !");
            final String substring = requestURI.getPath().substring(i + token.length() + 1);
            logger.warn("substring : '{}'...", substring);
            final Path filePath = baseDir.resolve(substring);
            final File file = filePath.toFile();
            logger.warn("Following file requested : '{}'...", file.getAbsolutePath());
            if (file.exists()) {
                logger.warn("File found !");
                if (endingPointer > 0) {
                    supportFileChunk(exchange, file, startingPointer, endingPointer);
                } else {
                    dontSupportFileChunk(exchange, file);
                }

                return;

            }
            logger.warn("File not found :(");
        }
        exchange.sendResponseHeaders(400, 0);
        exchange.getResponseBody().close();
    }

    private void dontSupportFileChunk(HttpExchange exchange, File file) throws IOException {
        final OutputStream os = exchange.getResponseBody();
        long length = file.length();
        exchange.sendResponseHeaders(200, length);
        FileInputStream inputStream = new FileInputStream(file);

        int bytesRead = -1;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            os.write(buffer, 0, bytesRead);
        }

        os.close();
        inputStream.close();
    }

    private void supportFileChunk(HttpExchange exchange, File file, int startingPointer, int endingPointer) throws IOException {
        final FileInputStream in = new FileInputStream(file);
        String name = file.getName();
        int extension = name.lastIndexOf(".");
        String fileExtension = name.substring(extension + 1);
        int realEndingPointer = Math.min(endingPointer, Long.valueOf(file.length()).intValue() - 1); // content range is 0-indexed but the content length is 1-indexed.
        boolean finalChunk = realEndingPointer < endingPointer || endingPointer == (Long.valueOf(file.length()).intValue() - 1);
        exchange.getResponseHeaders().add("Accept-Ranges", "bytes");
        exchange.getResponseHeaders().add("Content-Type", "video/" + fileExtension);
        exchange.getResponseHeaders().add("Content-Range", "bytes " + startingPointer + "-" + (realEndingPointer) + "/" + (file.length()));

        // This method must be called prior getResponseBody()
        exchange.sendResponseHeaders(finalChunk ? 200 : 206, realEndingPointer - startingPointer);

        final OutputStream os = exchange.getResponseBody();
        int bytesRead = 0;
        byte[] buffer = new byte[BUFFER_SIZE];


        try {
            int remainingBytesToRead = realEndingPointer - startingPointer;
            int i = 0;
            in.skip(startingPointer);
            while ((bytesRead = in.read(buffer, 0, Math.min(BUFFER_SIZE, remainingBytesToRead))) > 0  /*&& seek < endingPointer*/) {
                os.write(buffer, 0, bytesRead);
                remainingBytesToRead = remainingBytesToRead - bytesRead;
            }
            os.flush();
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println(t);
            logger.error(t.getMessage());
        }


        os.close();
        in.close();

    }
}
