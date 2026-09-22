package yuji.software.model;

import java.util.regex.Pattern;

public class ISBN {
    private static final Pattern ISBN_PATTERN = Pattern.compile("\\d{13}");

    private final String isbn;

    private ISBN(String isbn) {
        this.isbn = isbn;
    }

    public static ISBN of(String isbn) {
        if (isbn == null || !ISBN_PATTERN.matcher(isbn).matches()) {
            return null;
        }

        // ISBN-13 のチェックディジット
        // 先頭12桁を左から 1, 3, 1, 3... の重みで計算し、最後の1桁と合わせて10の倍数になれば正しい
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int digit = isbn.charAt(i) - '0';

            if (i < 12) {
                sum += digit * (i % 2 == 0 ? 1 : 3);
            } else {
                sum += digit;
            }
        }

        if (sum % 10 != 0) {
            return null;
        }

        return new ISBN(isbn);

    }

    @Override
    public String toString() {
        return isbn;
    }
}
