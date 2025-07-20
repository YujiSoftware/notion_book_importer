package yuji.software.kindle;

import java.net.URL;
import java.util.UUID;

public record Kindle(
        String title,
        String authors,
        long acquiredTime,
        ReadStatus readStatus,
        String asin,
        URL productImage
) {
    private static final String UUID_PREFIX = "KINDLE";

    public UUID uuid() {
        return UUID.nameUUIDFromBytes((UUID_PREFIX + "." + asin).getBytes());
    }
}
