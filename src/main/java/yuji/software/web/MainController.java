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
import yuji.software.google.GoogleBooksAPI;
import yuji.software.model.ISBN;
import yuji.software.notion.PageObjectResponse;
import yuji.software.openbd.OpenBD;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private static final String STORE = "紙書籍";
    private static final String KEY_PREFIX = "PHYSICAL";

    private final GoogleBooksAPI googleBooksAPI;

    @Value("${NOTION_API_KEY}")
    private String apiKey;

    @Value("${NOTION_DATABASE_ID}")
    private String databaseId;

    /**
     * 著者名の生没年の記載（ex: 1985-）
     */
    private static final Pattern PERSON_DATE = Pattern.compile("\\d{4}-.*");

    private record Book(String title, String author) {
    }

    public MainController(GoogleBooksAPI googleBooksAPI) {
        this.googleBooksAPI = googleBooksAPI;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> payload) throws IOException, InterruptedException {
        ISBN isbn = ISBN.of(payload.getOrDefault("isbn", ""));
        if (isbn == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ISBNが不正です。"));
        }

        var book = getFromGoogleBooks(isbn);
        if (book == null) {
            book = getFromOpenBD(isbn);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "書籍情報が見つかりませんでした。"));
            }
        }

        // Google Books API で取得した書籍情報に著者が含まれていない場合があるので、OpenBD で補完する
        if (book.author.isEmpty()) {
            var openBDBook = getFromOpenBD(isbn);
            if (openBDBook != null) {
                book = new Book(book.title, openBDBook.author);
            }
        }

        // 巻数を正規化
        // 例: "銀の匙 (15)" → "銀の匙 15"
        book = new Book(book.title, book.author.replaceFirst("\\s*\\(\\d+\\)", " $1"));

        try (Notion notion = new Notion(apiKey, databaseId)) {
            Optional<PageObjectResponse> page = notion.getPage(KEY_PREFIX, isbn.toString());
            if (page.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", book.title + " (" + book.author + ") は登録済みです"
                ));
            }

            notion.builder()
                    .title(book.title)
                    .author(book.author)
                    .status("未読")
                    .store(STORE)
                    .key(KEY_PREFIX, isbn.toString())
                    .create();
            return ResponseEntity.ok(Map.of(
                    "message", book.title + " (" + book.author + ") を登録しました",
                    "isbn", isbn.toString(),
                    "title", book.title,
                    "author", book.author
            ));
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<Map<String, String>> edit(@RequestBody Map<String, String> payload) throws IOException, InterruptedException {
        ISBN isbn = ISBN.of(payload.getOrDefault("isbn", ""));
        if (isbn == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "ISBNが不正です。"));
        }

        try (Notion notion = new Notion(apiKey, databaseId)) {
            Optional<PageObjectResponse> page = notion.getPage(KEY_PREFIX, isbn.toString());
            if (page.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", isbn + " は登録されていません"));
            }

            notion.builder()
                    .title(payload.get("title"))
                    .author(payload.get("author"))
                    .key(KEY_PREFIX, isbn.toString())
                    .update(page.get().id());
            return ResponseEntity.ok(Map.of(
                    "message", payload.get("title") + " (" + payload.get("author") + ") を更新しました",
                    "isbn", isbn.toString(),
                    "title", payload.get("title"),
                    "author", payload.get("author")
            ));
        }
    }

    private Book getFromGoogleBooks(ISBN isbn) throws IOException, InterruptedException {
        var item = googleBooksAPI.get(isbn);
        if (item == null) {
            return null;
        }

        var title = item.volumeInfo().title();

        // 著者が null の場合がある（未登録？）
        var authors = Optional.ofNullable(item.volumeInfo().authors()).orElse(List.of());
        var author = String.join(", ", authors);

        return new Book(title, author);
    }

    private Book getFromOpenBD(ISBN isbn) throws IOException, InterruptedException {
        try (OpenBD openBD = new OpenBD()) {
            var book = openBD.get(isbn);
            if (book == null) {
                return null;
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

            return new Book(title, author);
        }
    }
}
