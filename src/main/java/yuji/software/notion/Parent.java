package yuji.software.notion;

/*
  parent:
    | { type: "database_id"; database_id: string }
    | { type: "page_id"; page_id: string }
    | { type: "block_id"; block_id: string }
    | { type: "workspace"; workspace: true }
 */
public record Parent(ParentType type, String databaseId, String pageId, String blockId, boolean workspace) {
}
