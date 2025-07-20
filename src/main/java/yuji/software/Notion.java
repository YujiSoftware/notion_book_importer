package yuji.software;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Notion {
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

    public static Map<String, Object> makeCreateJson(String databaseId, UUID uuid, String title, String author, LocalDate acquiredTime, String status, String store, String url) {
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
                                Map.of("start", acquiredTime.format(DateTimeFormatter.ISO_DATE))
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
}
