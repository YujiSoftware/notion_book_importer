package yuji.software.notion;

public record AnnotationResponse(
        boolean bold,
        boolean italic,
        boolean strikethrough,
        boolean underline,
        boolean code,
        ApiColor color
) {
}
