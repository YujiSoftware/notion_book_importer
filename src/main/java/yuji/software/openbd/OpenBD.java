package yuji.software.openbd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenBD implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(OpenBD.class);

    private final HttpClient client = HttpClient.newHttpClient();

    private final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);

    public void close() {
        client.close();
    }

    public OpenBDBook get(String isbn) throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openbd.jp/v1/get?isbn=" + isbn))
                .GET()
                .build();
        logger.debug("{}", req.uri());

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(res.body());
        }

        var books = mapper.readValue(res.body(), OpenBDBook[].class);
        if (books.length == 0) {
            return null;
        }

        return books[0];
    }
}
