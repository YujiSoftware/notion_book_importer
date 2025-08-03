package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(RichTextItemResponse.TextRichTextItemResponse.class),
        @JsonSubTypes.Type(RichTextItemResponse.MentionRichTextItemResponse.class),
        @JsonSubTypes.Type(RichTextItemResponse.EquationRichTextItemResponse.class),
})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public sealed interface RichTextItemResponse {
    String type();

    String plainText();

    String href();

    AnnotationResponse annotations();

    @JsonTypeName("text")
    record TextRichTextItemResponse(
            String type,
            Text text,
            String plainText,
            String href,
            AnnotationResponse annotations
    ) implements RichTextItemResponse {
        record Text(String content, Link link) {
            record Link(String url) {
            }
        }
    }

    @JsonTypeName("mention")
    record MentionRichTextItemResponse(
            String type,
            Mention mention,
            String plainText,
            String href,
            AnnotationResponse annotations
    ) implements RichTextItemResponse {
        record Mention(String type/* TODO */) {
        }
    }

    @JsonTypeName("equation")
    record EquationRichTextItemResponse(
            String type,
            Equation equation,
            String plainText,
            String href,
            AnnotationResponse annotations
    ) implements RichTextItemResponse {
        record Equation(String expression) {
        }
    }
}
