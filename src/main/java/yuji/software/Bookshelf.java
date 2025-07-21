package yuji.software;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Bookshelf {
    public static <T> T read(MultipartFile file, Class<T> type) throws IOException {
        try (InputStream stream = new BufferedInputStream(file.getInputStream())) {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            return mapper.readValue(stream, type);
        }
    }
}
