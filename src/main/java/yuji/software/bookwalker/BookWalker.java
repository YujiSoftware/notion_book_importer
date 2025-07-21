package yuji.software.bookwalker;

import java.time.LocalDateTime;
import java.util.List;

public record BookWalker(String store, LocalDateTime createdAt, List<BookWalkerItem> items) {
}
