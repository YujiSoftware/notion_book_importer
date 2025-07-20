package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public record PageObjectResponse(String id, Parent parent, Map<String, Object> properties) {
}
