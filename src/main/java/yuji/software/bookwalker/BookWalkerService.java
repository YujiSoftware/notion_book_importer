package yuji.software.bookwalker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yuji.software.Bookshelf;
import yuji.software.BookshelfService;
import yuji.software.Notion;
import yuji.software.notion.PageObjectResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookWalkerService implements BookshelfService {
    private static final Logger logger = LoggerFactory.getLogger(BookWalkerService.class);

    private static final String STORE = "BOOK☆WALKER";

    @Value("${NOTION_API_KEY}")
    private String apiKey;

    @Value("${NOTION_DATABASE_ID}")
    private String databaseId;

    @Autowired
    private Notion notion;

    @Override
    public void upload(MultipartFile file) throws IOException, InterruptedException {
        List<BookWalker> list = Bookshelf.read(file, new TypeReference<>() {
        });

        ObjectMapper mapper = new ObjectMapper();
        Map<UUID, PageObjectResponse> pages = notion.getPages(STORE);

        try (var client = HttpClient.newHttpClient()) {
            for (BookWalker bookWalker : list) {
                PageObjectResponse page = pages.get(bookWalker.uuid());
                if (page != null) {
                    Map<?, ?> status = (Map<?, ?>) page.properties().get("ステータス");
                    Map<?, ?> select = (Map<?, ?>) status.get("select");
                    String name = (String) select.get("name");
                    if (ReadStatus.valueOf(name) == bookWalker.status()) {
                        continue;
                    }

                    var data = Notion.makeUpdateJson(bookWalker.status().name());
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
                            bookWalker.uuid(),
                            bookWalker.title(),
                            String.join(",", bookWalker.authors()),
                            bookWalker.buyTime(),
                            bookWalker.status().name(),
                            STORE,
                            bookWalker.url().toString()
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
}
