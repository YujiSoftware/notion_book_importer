package yuji.software;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class BookshelfApplicationRunner implements ApplicationRunner, ExitCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(BookshelfApplicationRunner.class);

    private final BookshelfService service;

    private int exitCode = 0;

    public BookshelfApplicationRunner(BookshelfService service) {
        this.service = service;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> argsList = args.getNonOptionArgs();
        if (argsList.isEmpty()) {
            logger.error("Please provide the path to the JSON file.");
            exitCode = 1;
            return;
        }

        Path path = Path.of(argsList.getFirst());
        if (!Files.exists(path)) {
            logger.error("File does not exist: {}", path);
            exitCode = 1;
            return;
        }

        logger.info("Processing file: {}", path);
        service.process(path);
        logger.info("Processed");
    }
}

