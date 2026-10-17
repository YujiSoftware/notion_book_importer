package yuji.software.web;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainControllerTest {
    @Nested
    class BookTest {
        @Test
        public void book() {
            MainController.Book book = new MainController.Book("銀の匙", "荒川弘");
            assertEquals("銀の匙", book.title());
            assertEquals("荒川弘", book.author());
        }

        @Test
        public void title_exclamation() {
            MainController.Book book = new MainController.Book("テスト!", "著者");
            assertEquals("テスト！", book.title());
        }

        @Test
        public void title_question() {
            MainController.Book book = new MainController.Book("テスト?", "著者");
            assertEquals("テスト？", book.title());
        }

        @Test
        public void title_parentheses() {
            MainController.Book book = new MainController.Book("銀の匙(15)", "著者");
            assertEquals("銀の匙 15", book.title());
        }

        @Test
        public void title_parenthesesWithSpace() {
            MainController.Book book = new MainController.Book("銀の匙 (15)", "著者");
            assertEquals("銀の匙 15", book.title());
        }
    }
}