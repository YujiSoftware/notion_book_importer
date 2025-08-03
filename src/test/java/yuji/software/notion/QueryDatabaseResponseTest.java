package yuji.software.notion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class QueryDatabaseResponseTest {
    @Test
    public void test() throws IOException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        URL resource = this.getClass().getResource("QueryDatabaseResponseTest.json");
        QueryDatabaseResponse response = mapper.readValue(resource, QueryDatabaseResponse.class);

        assertEquals("page_or_database", response.type(), "type");
        assertEquals("list", response.object(), "object");
        assertEquals("236efb4a-c045-815d-b123-c42f57bad30b", response.nextCursor(), "next_cursor");
        assertTrue(response.hasMore(), "has_more");
        assertEquals("b3fab30d-7659-40e3-822e-c8e4a7d4229f", response.requestId(), "request_id");
        assertEquals(1, response.results().size());

        PageObjectResponse result = response.results().getFirst();
        assertEquals(
                new PageObjectResponse.Parent.DatabaseId("database_id", "d9824bdc-8445-4327-be8b-5b47500af6ce"),
                result.parent(),
                "parent"
        );
        assertEquals(12, result.properties().size(), "properties.size");
        assertEquals(
                new PartialUserObjectResponse("ee5f0f84-409a-440f-983a-a5315961c6e4", "user"),
                result.createdBy(),
                "created_by"
        );
        assertEquals(
                new PartialUserObjectResponse("0c3e9826-b8f7-4f73-927d-2caaf86f1103", "user"),
                result.lastEditedBy(),
                "last_edited_by"
        );
        assertEquals("page", result.object(), "object");
        assertEquals("59833787-2cf9-4fdf-8782-e53db20768a5", result.id(), "id");
        assertEquals(Instant.parse("2022-03-01T19:05:00.000Z"), result.createdTime(), "created_time");
        assertEquals(Instant.parse("2022-07-06T20:25:00.000Z"), result.lastEditedTime(), "last_edited_time");
        assertFalse(result.archived(), "archived");
        assertFalse(result.inTrash(), "in_trash");
        assertEquals("https://www.notion.so/Tuscan-kale-598337872cf94fdf8782e53db20768a5", result.url(), "url");
        assertNull(result.publicUrl(), "public_url");
    }
}