package yuji.software;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import yuji.software.kindle.Kindle;
import yuji.software.kindle.KindleService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class MainController {
    private final KindleService service;

    public MainController(KindleService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type") String type) throws IOException, InterruptedException {
        List<Kindle> list;
        try (InputStream stream = file.getInputStream()) {
            list = service.read(stream);
        }
        service.upload(list);

        return "index";
    }
}
