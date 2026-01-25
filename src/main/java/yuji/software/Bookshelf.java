package yuji.software;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Bookshelf {
    public static <T> T read(Path path, Class<T> type) throws IOException {
        try (InputStream stream = new BufferedInputStream(Files.newInputStream(path))) {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            return mapper.readValue(stream, type);
        }
    }
}
