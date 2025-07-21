package yuji.software.bookwalker;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yuji.software.Bookshelf;
import yuji.software.BookshelfService;
import yuji.software.Notion;
import yuji.software.notion.PageObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookWalkerService implements BookshelfService {
    private static final String STORE = "BOOK☆WALKER";

    @Value("${NOTION_API_KEY}")
    private String apiKey;

    @Value("${NOTION_DATABASE_ID}")
    private String databaseId;

    @Override
    public void upload(MultipartFile file) throws IOException, InterruptedException {
        List<BookWalker> list = Bookshelf.read(file, new TypeReference<>() {
        });

        try (Notion notion = new Notion(apiKey, databaseId)) {
            Map<UUID, PageObjectResponse> pages = notion.getPages(STORE);
            for (BookWalker bookWalker : list) {
                PageObjectResponse page = pages.get(bookWalker.uuid());
                if (page != null) {
                    Map<?, ?> status = (Map<?, ?>) page.properties().get("ステータス");
                    Map<?, ?> select = (Map<?, ?>) status.get("select");
                    if (select != null) {
                        String name = (String) select.get("name");
                        if (ReadStatus.valueOf(name) == bookWalker.status()) {
                            continue;
                        }
                    }

                    notion.builder()
                            .status(bookWalker.status().name())
                            .update(page.id());
                } else {
                    notion.builder()
                            .title(bookWalker.title())
                            .uuid(bookWalker.uuid())
                            .author(String.join(",", bookWalker.authors()))
                            .buyTime(bookWalker.buyTime())
                            .status(bookWalker.status().name())
                            .store(STORE)
                            .url(bookWalker.url().toString())
                            .create();
                }
            }
        }
    }
}
