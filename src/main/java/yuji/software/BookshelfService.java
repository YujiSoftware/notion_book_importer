package yuji.software;

import java.io.IOException;
import java.nio.file.Path;

public interface BookshelfService {
    void process(Path path) throws IOException, InterruptedException;
}
