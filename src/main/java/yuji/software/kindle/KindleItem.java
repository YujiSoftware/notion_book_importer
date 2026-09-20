package yuji.software.kindle;

import java.net.URL;

public record KindleItem(
        String title,
        String authors,
        long acquiredTime,
        ReadStatus readStatus,
        String asin,
        URL productImage
) {
}
