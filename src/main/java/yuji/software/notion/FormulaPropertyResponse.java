package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(FormulaPropertyResponse.String.class),
        @JsonSubTypes.Type(FormulaPropertyResponse.Date.class),
        @JsonSubTypes.Type(FormulaPropertyResponse.Number.class),
        @JsonSubTypes.Type(FormulaPropertyResponse.Boolean.class),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public interface FormulaPropertyResponse {
    java.lang.String type();

    @JsonTypeName("string")
    record String(java.lang.String type, java.lang.String string) implements FormulaPropertyResponse {
    }

    @JsonTypeName("date")
    record Date(java.lang.String type, Instant date) implements FormulaPropertyResponse {
    }

    @JsonTypeName("number")
    record Number(java.lang.String type, java.lang.Number number) implements FormulaPropertyResponse {
    }

    @JsonTypeName("boolean")
    record Boolean(java.lang.String type, boolean bool) implements FormulaPropertyResponse {
    }
}
