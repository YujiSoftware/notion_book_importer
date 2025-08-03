package yuji.software.kindle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yuji.software.Bookshelf;
import yuji.software.BookshelfService;
import yuji.software.Notion;
import yuji.software.notion.PageObjectResponse;
import yuji.software.notion.PartialSelectResponse;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class KindleService implements BookshelfService {
    private static final String STORE = "Kindle";

    @Value("${NOTION_API_KEY}")
    private String apiKey;

    @Value("${NOTION_DATABASE_ID}")
    private String databaseId;

    public void upload(MultipartFile file) throws IOException, InterruptedException {
        Kindle kindle = Bookshelf.read(file, Kindle.class);

        try (Notion notion = new Notion(apiKey, databaseId)) {
            Map<UUID, PageObjectResponse> pages = notion.getPages(STORE);
            for (KindleItem item : kindle.items()) {
                PageObjectResponse page = pages.get(item.uuid());
                if (page != null) {
                    PageObjectResponse.Property.Select status = (PageObjectResponse.Property.Select) page.properties().get("ステータス");
                    PartialSelectResponse select = status.select();
                    if (select != null) {
                        if (ReadStatus.fromText(select.name()) == item.readStatus()) {
                            continue;
                        }
                    }

                    notion.builder()
                            .status(item.readStatus().getText())
                            .update(page.id());
                } else {
                    notion.builder()
                            .uuid(item.uuid())
                            .title(item.title())
                            .author(item.authors())
                            .buyTime(ZonedDateTime.ofInstant(Instant.ofEpochMilli(item.acquiredTime()), ZoneId.of("Asia/Tokyo")))
                            .status(item.readStatus().getText())
                            .store(STORE)
                            .url("https://read.amazon.co.jp/?asin=" + item.asin())
                            .create();
                }
            }
        }
    }
}
