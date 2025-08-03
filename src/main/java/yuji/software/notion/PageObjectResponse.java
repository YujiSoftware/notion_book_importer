package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/*
export type PageObjectResponse = {
  parent:
    | { type: "database_id"; database_id: string }
    | { type: "page_id"; page_id: string }
    | { type: "block_id"; block_id: string }
    | { type: "workspace"; workspace: true }
  properties: Record<
    string,
    | { type: "number"; number: number | null; id: string }
    | { type: "url"; url: string | null; id: string }
    | { type: "select"; select: PartialSelectResponse | null; id: string }
    | {
        type: "multi_select"
        multi_select: Array<PartialSelectResponse>
        id: string
      }
    | { type: "status"; status: PartialSelectResponse | null; id: string }
    | { type: "date"; date: DateResponse | null; id: string }
    | { type: "email"; email: string | null; id: string }
    | { type: "phone_number"; phone_number: string | null; id: string }
    | { type: "checkbox"; checkbox: boolean; id: string }
    | {
        type: "files"
        files: Array<
          | {
              file: { url: string; expiry_time: string }
              name: StringRequest
              type?: "file"
            }
          | {
              external: { url: TextRequest }
              name: StringRequest
              type?: "external"
            }
        >
        id: string
      }
    | {
        type: "created_by"
        created_by: PartialUserObjectResponse | UserObjectResponse
        id: string
      }
    | { type: "created_time"; created_time: string; id: string }
    | {
        type: "last_edited_by"
        last_edited_by: PartialUserObjectResponse | UserObjectResponse
        id: string
      }
    | { type: "last_edited_time"; last_edited_time: string; id: string }
    | { type: "formula"; formula: FormulaPropertyResponse; id: string }
    | { type: "button"; button: Record<string, never>; id: string }
    | {
        type: "unique_id"
        unique_id: { prefix: string | null; number: number | null }
        id: string
      }
    | {
        type: "verification"
        verification:
          | VerificationPropertyUnverifiedResponse
          | VerificationPropertyResponse
          | null
        id: string
      }
    | { type: "title"; title: Array<RichTextItemResponse>; id: string }
    | { type: "rich_text"; rich_text: Array<RichTextItemResponse>; id: string }
    | {
        type: "people"
        people: Array<PartialUserObjectResponse | UserObjectResponse>
        id: string
      }
    | { type: "relation"; relation: Array<{ id: string }>; id: string }
    | {
        type: "rollup"
        rollup:
          | { type: "number"; number: number | null; function: RollupFunction }
          | {
              type: "date"
              date: DateResponse | null
              function: RollupFunction
            }
          | {
              type: "array"
              array: Array<
                | { type: "number"; number: number | null }
                | { type: "url"; url: string | null }
                | { type: "select"; select: PartialSelectResponse | null }
                | {
                    type: "multi_select"
                    multi_select: Array<PartialSelectResponse>
                  }
                | { type: "status"; status: PartialSelectResponse | null }
                | { type: "date"; date: DateResponse | null }
                | { type: "email"; email: string | null }
                | { type: "phone_number"; phone_number: string | null }
                | { type: "checkbox"; checkbox: boolean }
                | {
                    type: "files"
                    files: Array<
                      | {
                          file: { url: string; expiry_time: string }
                          name: StringRequest
                          type?: "file"
                        }
                      | {
                          external: { url: TextRequest }
                          name: StringRequest
                          type?: "external"
                        }
                    >
                  }
                | {
                    type: "created_by"
                    created_by: PartialUserObjectResponse | UserObjectResponse
                  }
                | { type: "created_time"; created_time: string }
                | {
                    type: "last_edited_by"
                    last_edited_by:
                      | PartialUserObjectResponse
                      | UserObjectResponse
                  }
                | { type: "last_edited_time"; last_edited_time: string }
                | { type: "formula"; formula: FormulaPropertyResponse }
                | { type: "button"; button: Record<string, never> }
                | {
                    type: "unique_id"
                    unique_id: { prefix: string | null; number: number | null }
                  }
                | {
                    type: "verification"
                    verification:
                      | VerificationPropertyUnverifiedResponse
                      | VerificationPropertyResponse
                      | null
                  }
                | { type: "title"; title: Array<RichTextItemResponse> }
                | { type: "rich_text"; rich_text: Array<RichTextItemResponse> }
                | {
                    type: "people"
                    people: Array<
                      PartialUserObjectResponse | UserObjectResponse
                    >
                  }
                | { type: "relation"; relation: Array<{ id: string }> }
              >
              function: RollupFunction
            }
        id: string
      }
  >
  icon: PageIconResponse | null
  cover: PageCoverResponse | null
  created_by: PartialUserObjectResponse
  last_edited_by: PartialUserObjectResponse
  object: "page"
  id: string
  created_time: string
  last_edited_time: string
  archived: boolean
  in_trash: boolean
  url: string
  public_url: string | null
}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PageObjectResponse(
        Parent parent,
        Map<String, Property> properties,
        PartialUserObjectResponse createdBy,
        PartialUserObjectResponse lastEditedBy,
        String object,
        String id,
        Instant createdTime,
        Instant lastEditedTime,
        boolean archived,
        boolean inTrash,
        String url,
        String publicUrl
) {
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(Parent.DatabaseId.class),
            @JsonSubTypes.Type(Parent.PageId.class),
            @JsonSubTypes.Type(Parent.BlockId.class),
            @JsonSubTypes.Type(Parent.Workspace.class),
    })
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public sealed interface Parent {
        String type();

        @JsonTypeName("database_id")
        record DatabaseId(String type, String databaseId) implements Parent {
        }

        @JsonTypeName("page_id")
        record PageId(String type, String pageId) implements Parent {
        }

        @JsonTypeName("block_id")
        record BlockId(String type, String blockId) implements Parent {
        }

        @JsonTypeName("workspace")
        record Workspace(String type, String workspace) implements Parent {
        }
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(Property.Number.class),
            @JsonSubTypes.Type(Property.Url.class),
            @JsonSubTypes.Type(Property.Select.class),
            @JsonSubTypes.Type(Property.MultiSelect.class),
            @JsonSubTypes.Type(Property.Date.class),
            @JsonSubTypes.Type(Property.Checkbox.class),
            @JsonSubTypes.Type(Property.CreatedTime.class),
            @JsonSubTypes.Type(Property.Formula.class),
            @JsonSubTypes.Type(Property.Title.class),
            @JsonSubTypes.Type(Property.RichText.class),
            @JsonSubTypes.Type(Property.People.class),
            @JsonSubTypes.Type(Property.Relation.class),
            @JsonSubTypes.Type(Property.Rollup.class),
    })
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public sealed interface Property {
        String type();

        String id();

        @JsonTypeName("number")
        record Number(String type, java.lang.Number number, String id) implements Property {
        }

        @JsonTypeName("url")
        record Url(String type, String url, String id) implements Property {
        }

        @JsonTypeName("select")
        record Select(String type, PartialSelectResponse select, String id) implements Property {
        }

        @JsonTypeName("multi_select")
        record MultiSelect(String type, List<PartialSelectResponse> multiSelect, String id) implements Property {
        }

        @JsonTypeName("date")
        record Date(String type, DateResponse date, String id) implements Property {
        }

        @JsonTypeName("checkbox")
        record Checkbox(String type, boolean checkbox, String id) implements Property {
        }

        @JsonTypeName("created_time")
        record CreatedTime(String type, Instant createdTime, String id) implements Property {
        }

        @JsonTypeName("formula")
        record Formula(String type, FormulaPropertyResponse formula, String id) implements Property {
        }

        @JsonTypeName("title")
        record Title(String type, List<RichTextItemResponse> title, String id) implements Property {
        }

        @JsonTypeName("rich_text")
        record RichText(String type, List<RichTextItemResponse> richText, String id) implements Property {
        }

        @JsonTypeName("people")
        record People(String type, List<UserObjectResponse> people, String id) implements Property {
        }

        @JsonTypeName("relation")
        record Relation(String type, List<RelationResponse> relation, boolean hasMore,
                        String id) implements Property {
            record RelationResponse(String id) {
            }
        }

        @JsonTypeName("rollup")
        record Rollup(String type, RollupResponse rollup, String id) implements Property {
            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.PROPERTY,
                    property = "type",
                    visible = true
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(RollupResponse.Number.class),
                    @JsonSubTypes.Type(RollupResponse.Date.class),
                    @JsonSubTypes.Type(RollupResponse.Array.class),
            })
            @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
            public sealed interface RollupResponse {
                String type();

                RollupFunction function();

                @JsonTypeName("number")
                record Number(String type, java.lang.Number number, RollupFunction function) implements RollupResponse {
                }

                @JsonTypeName("date")
                record Date(String type, DateResponse date, RollupFunction function) implements RollupResponse {
                }

                @JsonTypeName("array")
                record Array(String type/* TODO */, RollupFunction function) implements RollupResponse {
                }
            }
        }
    }
}
