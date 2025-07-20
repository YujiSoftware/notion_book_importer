package yuji.software.bookwalker;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record BookWalker(
        String title,
        List<String> authors,
        String category,
        String label,
        String company,
        ReadStatus status,
        ZonedDateTime buyTime,
        URL url,
        UUID uuid
) {
}
