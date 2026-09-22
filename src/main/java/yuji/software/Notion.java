package yuji.software;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.Optional;

public class Notion implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(Notion.class);

    private final String apiKey;

    private final String databaseId;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

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

        public Builder key(String prefix, String key) {
            properties.put(
                    "Key",
                    Map.of(
                            "rich_text",
                            List.of(
                                    Map.of(
                                            "type", "text",
                                            "text", Map.of("content", prefix + ":" + key)
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

        public Builder category(String category) {
            properties.put(
                    "カテゴリー",
                    Map.of(
                            "select",
                            Map.of("name", category)
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

            var req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.notion.com/v1/pages/" + pageId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Notion-Version", "2022-06-28")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();
            logger.debug("{}, PATCH: {}", req.uri().toString(), json);

            var res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(res.body());
            }
        }
    }

    public Optional<PageObjectResponse> getPage(String key, String value) throws IOException, InterruptedException {
        return getPages(key, value).values().stream().findFirst();
    }

    public Map<String, PageObjectResponse> getPages(String key) throws IOException, InterruptedException {
        return getPages(key, null);
    }

    private Map<String, PageObjectResponse> getPages(String key, String value) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        Map<String, PageObjectResponse> pages = new HashMap<>();
        String cursor = null;
        do {
            // TODO: オブジェクト化
            String json;
            String startCursor = cursor == null ? "" : ", \"start_cursor\": \"" + cursor + "\"";
            if (value == null) {
                json = "{\"filter\": { \"property\": \"Key\", \"rich_text\": { \"starts_with\": \"" + key + ":\" }}" + startCursor + "}";
            } else {
                json = "{\"filter\": { \"property\": \"Key\", \"rich_text\": { \"equals\": \"" + key + ":" + value + "\" }}" + startCursor + "}";
            }

            var req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.notion.com/v1/databases/" + databaseId + "/query"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Notion-Version", "2022-06-28")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            logger.debug("{}, POST: {}", req.uri(), json);

            var res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(res.body());
            }

            QueryDatabaseResponse response = mapper.readValue(res.body(), QueryDatabaseResponse.class);
            for (PageObjectResponse result : response.results()) {
                PageObjectResponse.Property.RichText prop = (PageObjectResponse.Property.RichText) result.properties().get("Key");
                String plainText = prop.richText().getFirst().plainText();

                pages.put(plainText.split(":")[1], result);
            }

            cursor = response.hasMore() ? response.nextCursor() : null;
        } while (cursor != null);

        return pages;
    }
}
