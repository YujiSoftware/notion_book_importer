package yuji.software.kindle;

import java.time.LocalDateTime;
import java.util.List;

public record Kindle(String store, LocalDateTime createdAt, List<KindleItem> items) {
}
