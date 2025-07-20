package yuji.software;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yuji.software.notion.PageObjectResponse;
import yuji.software.notion.QueryDatabaseResponse;

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

@Component
public class Notion {
    @Value("${NOTION_API_KEY}")
    private String apiKey;

    @Value("${NOTION_DATABASE_ID}")
    private String databaseId;

    public static Map<String, Object> makeUpdateJson(String status) {
        return Map.of(
                "properties",
                Map.of(
                        "ステータス",
                        Map.of(
                                "select",
                                Map.of("name", status)
                        )
                )
        );
    }

    public static Map<String, Object> makeCreateJson(String databaseId, UUID uuid, String title, String author, ZonedDateTime acquiredTime, String status, String store, String url) {
        return Map.of(
                "parent",
                Map.of(
                        "database_id", databaseId
                ),
                "properties",
                Map.of(
                        "UUID",
                        Map.of(
                                "rich_text",
                                List.of(
                                        Map.of(
                                                "type", "text",
                                                "text", Map.of("content", uuid.toString())
                                        )
                                )
                        ),
                        "タイトル",
                        Map.of(
                                "title",
                                List.of(
                                        Map.of(
                                                "text",
                                                Map.of("content", title)
                                        )
                                )
                        ),
                        "ステータス",
                        Map.of(
                                "select",
                                Map.of("name", status)
                        ),
                        "著者",
                        Map.of(
                                "rich_text",
                                List.of(
                                        Map.of(
                                                "type", "text",
                                                "text", Map.of("content", author)
                                        )
                                )
                        ),
                        "購入日",
                        Map.of(
                                "date",
                                Map.of("start", acquiredTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        ),
                        "ストア",
                        Map.of(
                                "select",
                                Map.of("name", store)
                        ),
                        "URL",
                        Map.of("url", url)
                )
        );
    }

    public Map<UUID, PageObjectResponse> getPages(String store) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        Map<UUID, PageObjectResponse> pages = new HashMap<>();
        String cursor = null;
        try (var client = HttpClient.newHttpClient()) {
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
        }

        return pages;
    }
}
