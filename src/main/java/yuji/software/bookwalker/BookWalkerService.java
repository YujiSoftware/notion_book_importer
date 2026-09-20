package yuji.software.bookwalker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yuji.software.Bookshelf;
import yuji.software.BookshelfService;
import yuji.software.Notion;
import yuji.software.notion.PageObjectResponse;
import yuji.software.notion.PartialSelectResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Service
public class BookWalkerService implements BookshelfService {
    private static final String STORE = "BOOK☆WALKER";
    private static final String KEY_PREFIX = "BOOKWALKER";

    @Value("${NOTION_API_KEY}")
    private String apiKey;

    @Value("${NOTION_DATABASE_ID}")
    private String databaseId;

    @Override
    public void process(Path path) throws IOException, InterruptedException {
        BookWalker bookWalker = Bookshelf.read(path, BookWalker.class);
        process(bookWalker);
    }

    public void process(BookWalker bookWalker) throws IOException, InterruptedException {
        try (Notion notion = new Notion(apiKey, databaseId)) {
            Map<String, PageObjectResponse> pages = notion.getPages(KEY_PREFIX);
            for (BookWalkerItem item : bookWalker.items()) {
                PageObjectResponse page = pages.get(item.uuid().toString());
                if (page != null) {
                    PageObjectResponse.Property.Select status = (PageObjectResponse.Property.Select) page.properties().get("ステータス");
                    PartialSelectResponse select = status.select();
                    if (select != null) {
                        if (ReadStatus.valueOf(select.name()) == item.status()) {
                            continue;
                        }
                    }

                    notion.builder()
                            .status(item.status().name())
                            .update(page.id());
                } else {
                    notion.builder()
                            .title(item.title())
                            .author(String.join(",", item.authors()))
                            .category(item.category())
                            .buyTime(item.buyTime())
                            .status(item.status().name())
                            .store(STORE)
                            .url(item.url().toString())
                            .key(KEY_PREFIX, item.uuid().toString())
                            .create();
                }
            }
        }
    }
}
