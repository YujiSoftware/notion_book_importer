package yuji.software;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import yuji.software.bookwalker.BookWalkerService;
import yuji.software.kindle.KindleService;

import java.io.IOException;

@Controller
public class MainController {
    private final KindleService kindleService;

    private final BookWalkerService bookWalkerService;

    public MainController(KindleService service, BookWalkerService bookWalkerService) {
        this.kindleService = service;
        this.bookWalkerService = bookWalkerService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type") String type) throws IOException, InterruptedException {
        BookshelfService service = getService(type);
        service.upload(file);

        return "index";
    }

    private BookshelfService getService(String type) {
        return switch (type) {
            case "kindle" -> kindleService;
            case "book-walker" -> bookWalkerService;
            default -> throw new RuntimeException("Unknown type: " + type);
        };
    }
}
