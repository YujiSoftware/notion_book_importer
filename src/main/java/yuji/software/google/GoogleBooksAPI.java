package yuji.software.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yuji.software.model.ISBN;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GoogleBooksAPI implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksAPI.class);

    private static final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

    private final HttpClient client = HttpClient.newHttpClient();

    @Value("${GOOGLE_BOOKS_API_KEY}")
    private String apiKey;

    @PreDestroy
    public void close() {
        client.close();
    }

    public GoogleBook.Item get(ISBN isbn) throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn + "&key=" + apiKey))
                .GET()
                .build();
        logger.debug("{}", req.uri());

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(res.body());
        }

        var books = mapper.readValue(res.body(), GoogleBook.class);
        if (books.totalItems() == 0) {
            return null;
        }

        return books.items().getFirst();
    }
}
