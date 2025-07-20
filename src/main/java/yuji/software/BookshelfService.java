package yuji.software;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BookshelfService {
    void upload(MultipartFile file) throws IOException, InterruptedException;
}
