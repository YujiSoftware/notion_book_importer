package yuji.software.notion;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.TimeZone;

// TODO: start, date を Tmporal にする（2022-02-22 / 2025-04-23T17:05:00.000+00:00 の両方がありえる）
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DateResponse(String start, String end, TimeZone timeZone) {
}
