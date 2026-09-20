package yuji.software.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import yuji.software.Notion;
import yuji.software.notion.PageObjectResponse;
import yuji.software.openbd.OpenBD;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private static final String STORE = "紙書籍";
    private static final String KEY_PREFIX = "PHYSICAL";

    @Value("${NOTION_API_KEY}")
    private String apiKey;

    @Value("${NOTION_DATABASE_ID}")
    private String databaseId;

    /**
     * 著者名の生没年の記載（ex: 1985-）
     */
    private static final Pattern PERSON_DATE = Pattern.compile("\\d{4}-.*");

    public MainController() {
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> payload) throws IOException, InterruptedException {
        String isbn = payload.getOrDefault("isbn", "");
        if (!isbn.matches("\\d{10}|\\d{13}") || !isbn.startsWith("978")) {
            return ResponseEntity.badRequest().body(Map.of("message", "ISBNが不正です。"));
        }

        try (OpenBD openBD = new OpenBD()) {
            var book = openBD.get(isbn);
            if (book == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "見つかりませんでした。"));
            }

            String title = book.onix().descriptiveDetail().titleDetail().titleElement().titleText().content();
            String author = book.onix().descriptiveDetail().contributor().stream()
                    .map(c ->
                            // 姓と名がカンマ区切りで記載され、かつ生没年も記載されている場合があるので整える
                            // 例："荒川, 弘, 1973-" → "荒川 弘"
                            Arrays.stream(c.personName().content().split(", "))
                                    .filter(n -> !PERSON_DATE.matcher(n).matches())
                                    .collect(Collectors.joining(" "))
                    )
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            try (Notion notion = new Notion(apiKey, databaseId)) {
                Optional<PageObjectResponse> page = notion.getPage(KEY_PREFIX, isbn);
                if (page.isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                            "message", title + " (" + author + ") は登録済みです"
                    ));
                }

                notion.builder()
                        .title(title)
                        .author(author)
                        .status("未読")
                        .store(STORE)
                        .key(KEY_PREFIX, isbn)
                        .create();
                return ResponseEntity.ok(Map.of(
                        "message", title + " (" + author + ") を登録しました"
                ));
            }
        }
    }
}
