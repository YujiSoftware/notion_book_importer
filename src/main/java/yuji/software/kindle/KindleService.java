package yuji.software.kindle;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yuji.software.Bookshelf;
import yuji.software.BookshelfService;
import yuji.software.Notion;
import yuji.software.notion.PageObjectResponse;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
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
        List<Kindle> list = Bookshelf.read(file, new TypeReference<>() {
        });

        try (Notion notion = new Notion(apiKey, databaseId)) {
            Map<UUID, PageObjectResponse> pages = notion.getPages(STORE);
            for (Kindle kindle : list) {
                PageObjectResponse page = pages.get(kindle.uuid());
                if (page != null) {
                    Map<?, ?> status = (Map<?, ?>) page.properties().get("ステータス");
                    Map<?, ?> select = (Map<?, ?>) status.get("select");
                    if (select != null) {
                        String name = (String) select.get("name");
                        if (ReadStatus.fromText(name) == kindle.readStatus()) {
                            continue;
                        }
                    }

                    notion.builder()
                            .status(kindle.readStatus().getText())
                            .update(page.id());
                } else {
                    notion.builder()
                            .uuid(kindle.uuid())
                            .title(kindle.title())
                            .author(kindle.authors())
                            .buyTime(ZonedDateTime.ofInstant(Instant.ofEpochMilli(kindle.acquiredTime()), ZoneId.of("Asia/Tokyo")))
                            .status(kindle.readStatus().getText())
                            .store(STORE)
                            .url("https://read.amazon.co.jp/?asin=" + kindle.asin())
                            .create();
                }
            }
        }
    }
}
