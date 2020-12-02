package commons.lib.extra.server.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

public class FileUploadHandler implements HttpHandler {

    private static Logger logger = Logger.getLogger(FileUploadHandler.class.getName());


    public FileUploadHandler(List<String> parameters) {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final URI requestURI = exchange.getRequestURI();
        InputStream requestBody = exchange.getRequestBody();
        v6(requestBody, new File("./test.mp3"));
        exchange.sendResponseHeaders(200, 0);
        final OutputStream os = exchange.getResponseBody();
        //os.write(responseBody.getBytes());
        os.close();

    }


    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {
        File tmpFile = new File(file.getAbsolutePath() + ".web");

        try (FileOutputStream outputStream = new FileOutputStream(tmpFile)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                logger.warning("loop");
                outputStream.write(bytes, 0, read);
            }

            // commons-io
            //IOUtils.copy(inputStream, outputStream);
        }
        // TODO clean 4 first lines and 2 last lines
    }

    private static void v5(InputStream inputStream, File file)
            throws IOException {
        File tmpFile = new File(file.getAbsolutePath() + ".web");

        try (FileOutputStream outputStream = new FileOutputStream(tmpFile)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        // TODO clean 4 first lines and 2 last lines
    }

    /**
     * Main complexity is to remove the boundaries added by the web browser. e.g. :
     * ------WebKitFormBoundary8aIC79sOUcaSU9WS <br>
     * Content-Disposition: form-data; name="NAME"; filename="FILE"<br>
     * Content-Type: MIME TYPE
     *
     * @param inputStream
     * @param file
     * @throws IOException e
     */
    private static void v6(InputStream inputStream, File file)
            throws IOException {
        int lineRead = 0;
        int lineToAvoid = 4;
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1 && lineRead < lineToAvoid) {
                for (int i = 0; i < bytes.length; i++) {
                    if (bytes[i] == 10) {
                        lineRead++;
                        if (lineRead == lineToAvoid) {
                            byte[] keep = new byte[bytes.length - i];
                            try {
                                System.arraycopy(bytes, i + 1, keep, 0, bytes.length - i - 1);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                            outputStream.write(keep, 0, keep.length);
                        }
                    }
                }
            }
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        final RandomAccessFile f = new RandomAccessFile(file, "rw");
        long length = f.length() - 1;
        byte b;
        int nbLastLine = 0;
        do {
            length -= 1;
            f.seek(length);
            b = f.readByte();
            if (b == 10) {
                nbLastLine++;
            }
        } while (nbLastLine < 2);
        f.setLength(length + 1);
        f.close();
    }

    public void v2(InputStream inputStream, File file) {
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            int lineRead = 0;
            FileWriter fileWriter = new FileWriter(file);

            while ((line = bufferedReader.readLine()) != null) {
                logger.warning(line);
                lineRead++;
                if (lineRead > 4) {
                    fileWriter.append(line);
                    fileWriter.append("\n");
                }
            }
            fileWriter.close();

            final RandomAccessFile f = new RandomAccessFile(file, "rw");
            long length = f.length() - 1;
            byte b;
            int nbLastLine = 0;
            do {
                length -= 1;
                f.seek(length);
                b = f.readByte();
                if (b == 10) {
                    nbLastLine++;
                }
            } while (nbLastLine < 2);
            f.setLength(length + 1);
            f.close();
            //copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    private static void v3(InputStream inputStream, File file)
            throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int lineRead = 0;
            int maxLineToSkip = 4;
            int b;
            while ((b = inputStream.read()) != -1) {
                logger.warning("loop");
                if (b == 10) {
                    lineRead++;
                }
                if (lineRead > maxLineToSkip) {
                    outputStream.write(b);
                }
            }
        }
        final RandomAccessFile f = new RandomAccessFile(file, "rw");
        long length = f.length() - 1;
        byte b;
        int nbLastLine = 0;
        do {
            length -= 1;
            f.seek(length);
            b = f.readByte();
            if (b == 10) {
                nbLastLine++;
            }
        } while (nbLastLine < 2);
        f.setLength(length + 1);
        f.close();
    }

}
