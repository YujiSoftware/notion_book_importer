package yuji.software;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yuji.software.notion.PageObjectResponse;
import yuji.software.notion.QueryDatabaseResponse;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Notion implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(Notion.class);

    private final String apiKey;

    private final String databaseId;

    private final ObjectMapper mapper = new ObjectMapper();

    private final HttpClient client = HttpClient.newHttpClient();

    public Notion(String apiKey, String databaseId) {
        this.apiKey = apiKey;
        this.databaseId = databaseId;
    }

    @Override
    public void close() {
        client.close();
    }

    public Builder builder() {
        return new Builder();
    }

    public class Builder {
        private final Map<String, Object> properties = new HashMap<>();

        public Builder title(String title) {
            properties.put(
                    "タイトル",
                    Map.of(
                            "title",
                            List.of(
                                    Map.of(
                                            "text",
                                            Map.of("content", title)
                                    )
                            )
                    )
            );
            return this;
        }

        public Builder status(String status) {
            properties.put(
                    "ステータス",
                    Map.of(
                            "select",
                            Map.of("name", status)
                    )
            );
            return this;
        }

        public Builder uuid(UUID uuid) {
            properties.put(
                    "UUID",
                    Map.of(
                            "rich_text",
                            List.of(
                                    Map.of(
                                            "type", "text",
                                            "text", Map.of("content", uuid.toString())
                                    )
                            )
                    )
            );
            return this;
        }

        public Builder author(String author) {
            properties.put(
                    "著者",
                    Map.of(
                            "rich_text",
                            List.of(
                                    Map.of(
                                            "type", "text",
                                            "text", Map.of("content", author)
                                    )
                            )
                    )
            );
            return this;
        }

        public Builder buyTime(ZonedDateTime buyTime) {
            properties.put(
                    "購入日",
                    Map.of(
                            "date",
                            Map.of("start", buyTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    )
            );
            return this;
        }

        public Builder store(String store) {
            properties.put(
                    "ストア",
                    Map.of(
                            "select",
                            Map.of("name", store)
                    )
            );
            return this;
        }

        public Builder url(String url) {
            properties.put(
                    "URL",
                    Map.of("url", url)
            );
            return this;
        }

        public void create() throws IOException, InterruptedException {
            String json = mapper.writeValueAsString(
                    Map.of(
                            "parent", Map.of("database_id", databaseId),
                            "properties", properties
                    )
            );
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

        public void update(String pageId) throws IOException, InterruptedException {
            String json = mapper.writeValueAsString(
                    Map.of(
                            "properties", properties
                    )
            );
            logger.debug(json);

            var req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.notion.com/v1/pages/" + pageId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Notion-Version", "2022-06-28")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            var res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(res.body());
            }
        }
    }

    public Map<UUID, PageObjectResponse> getPages(String store) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        Map<UUID, PageObjectResponse> pages = new HashMap<>();
        String cursor = null;
        do {
            // TODO: オブジェクト化
            String json;
            if (cursor == null) {
                json = "{\"filter\": { \"property\": \"ストア\", \"select\": { \"equals\": \"" + store + "\" }}}";
            } else {
                json = "{\"start_cursor\": \"" + cursor + "\"}";
            }

            var req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.notion.com/v1/databases/" + databaseId + "/query"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Notion-Version", "2022-06-28")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
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

            cursor = response.hasMore() ? response.nextCursor() : null;
        } while (cursor != null);

        return pages;
    }
}
