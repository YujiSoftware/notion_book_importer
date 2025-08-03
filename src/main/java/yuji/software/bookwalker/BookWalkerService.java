package yuji.software.bookwalker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yuji.software.Bookshelf;
import yuji.software.BookshelfService;
import yuji.software.Notion;
import yuji.software.notion.PageObjectResponse;
import yuji.software.notion.PartialSelectResponse;

import java.io.IOException;
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
        BookWalker bookWalker = Bookshelf.read(file, BookWalker.class);

        try (Notion notion = new Notion(apiKey, databaseId)) {
            Map<UUID, PageObjectResponse> pages = notion.getPages(STORE);
            for (BookWalkerItem item : bookWalker.items()) {
                PageObjectResponse page = pages.get(item.uuid());
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
                            .uuid(item.uuid())
                            .author(String.join(",", item.authors()))
                            .buyTime(item.buyTime())
                            .status(item.status().name())
                            .store(STORE)
                            .url(item.url().toString())
                            .create();
                }
            }
        }
    }
}
