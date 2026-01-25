package yuji.software;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import yuji.software.bookwalker.BookWalkerService;

@Component
@ConditionalOnProperty(value = {"bookwalker"})
public class BookWalkerApplicationRunner extends BookshelfApplicationRunner {
    public BookWalkerApplicationRunner(BookWalkerService service) {
        super(service);
    }
}
