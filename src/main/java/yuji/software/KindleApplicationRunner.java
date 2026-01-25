package yuji.software;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import yuji.software.kindle.KindleService;

@Component
@ConditionalOnProperty(value = {"kindle"})
public class KindleApplicationRunner extends BookshelfApplicationRunner {
    public KindleApplicationRunner(KindleService service) {
        super(service);
    }
}
