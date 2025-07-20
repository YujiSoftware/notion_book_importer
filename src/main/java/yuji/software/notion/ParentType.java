package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ParentType {
    @JsonProperty("database_id")
    DatabaseId,
    @JsonProperty("page_id")
    PageId,
    @JsonProperty("block_id")
    BlockId,
    @JsonProperty("workspace")
    Workspace
}
