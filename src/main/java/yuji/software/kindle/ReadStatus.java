package yuji.software.kindle;

public enum ReadStatus {
    READ("既読"),
    UNKNOWN("未読");

    private final String text;

    ReadStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ReadStatus fromText(String text) {
        for (ReadStatus status : ReadStatus.values()) {
            if (status.text.equals(text)) {
                return status;
            }
        }

        throw new RuntimeException("Unknown status: " + text);
    }
}
