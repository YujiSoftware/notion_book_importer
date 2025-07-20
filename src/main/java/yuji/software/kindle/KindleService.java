package yuji.software.kindle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yuji.software.Notion;
import yuji.software.notion.PageObjectResponse;
import yuji.software.notion.QueryDatabaseResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KindleService {
    private static final Logger logger = LoggerFactory.getLogger(KindleService.class);

    @Value("${NOTION_API_KEY}")
    private String apiKey;

    @Value("${NOTION_DATABASE_ID}")
    private String databaseId;

    public List<Kindle> read(InputStream stream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(stream, new TypeReference<>() {
        });
    }

    public void upload(List<Kindle> list) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        Map<UUID, PageObjectResponse> pages = getPages();

        try (var client = HttpClient.newHttpClient()) {
            for (Kindle kindle : list) {
                PageObjectResponse page = pages.get(kindle.uuid());
                if (page != null) {
                    Map<?, ?> status = (Map<?, ?>) page.properties().get("ステータス");
                    Map<?, ?> select = (Map<?, ?>) status.get("select");
                    String name = (String) select.get("name");
                    if (ReadStatus.fromText(name) == kindle.readStatus()) {
                        continue;
                    }

                    var data = Notion.makeUpdateJson(kindle.readStatus().getText());
                    String json = mapper.writeValueAsString(data);
                    logger.debug(json);

                    var req = HttpRequest.newBuilder()
                            .uri(URI.create("https://api.notion.com/v1/pages/" + page.id()))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + apiKey)
                            .header("Notion-Version", "2022-06-28")
                            .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                            .build();

                    var res = client.send(req, HttpResponse.BodyHandlers.ofString());
                    if (res.statusCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(res.body());
                    }
                } else {
                    var data = Notion.makeCreateJson(
                            databaseId,
                            kindle.uuid(),
                            kindle.title(),
                            kindle.authors(),
                            LocalDate.ofInstant(Instant.ofEpochMilli(kindle.acquiredTime()), ZoneId.of("Asia/Tokyo")),
                            kindle.readStatus().getText(),
                            "Kindle",
                            "https://read.amazon.co.jp/?asin=" + kindle.asin()
                    );

                    String json = mapper.writeValueAsString(data);
                    logger.debug(json);

                    var req = HttpRequest.newBuilder()
                            .uri(URI.create("https://api.notion.com/v1/pages"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + apiKey)
                            .header("Notion-Version", "2022-06-28")
                            .POST(HttpRequest.BodyPublishers.ofString(json))
                            .build();
                    var res = client.send(req, HttpResponse.BodyHandlers.ofString());
                    if (res.statusCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(res.body());
                    }
                }
            }
        }
    }

    private Map<UUID, PageObjectResponse> getPages() throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        Map<UUID, PageObjectResponse> pages = new HashMap<>();

        try (var client = HttpClient.newHttpClient()) {
            var req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.notion.com/v1/databases/" + databaseId + "/query"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Notion-Version", "2022-06-28")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"filter\": { \"property\": \"ストア\", \"select\": { \"equals\": \"Kindle\" }}}"))
                    .build();

            var res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(res.body());
            }

            QueryDatabaseResponse response = mapper.readValue(res.body(), QueryDatabaseResponse.class);
            for (PageObjectResponse result : response.results()) {
                Map<?, ?> uuid = (Map<?, ?>) result.properties().get("UUID");
                List<?> richText = (List<?>) uuid.get("rich_text");
                Map<?, ?> block = (Map<?, ?>) richText.getFirst();
                String plainText = (String) block.get("plain_text");

                pages.put(UUID.fromString(plainText), result);
            }
        }

        return pages;
    }
}
