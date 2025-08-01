package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/*
export type QueryDatabaseResponse = {
  type: "page_or_database"
  page_or_database: EmptyObject
  object: "list"
  next_cursor: string | null
  has_more: boolean
  results: Array<
    | PageObjectResponse
    | PartialPageObjectResponse
    | PartialDatabaseObjectResponse
    | DatabaseObjectResponse
  >
}
 */
@JsonIgnoreProperties({"page_or_database"})
public record QueryDatabaseResponse(
        String type,
        String object,
        String nextCursor,
        boolean hasMore,
        String requestId,
        List<PageObjectResponse> results) {
}
