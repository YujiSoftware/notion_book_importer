package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RollupFunction {
    @JsonProperty("count")
    COUNT,
    @JsonProperty("count_values")
    COUNT_VALUES,
    @JsonProperty("empty")
    EMPTY,
    @JsonProperty("not_empty")
    NOT_EMPTY,
    @JsonProperty("unique")
    UNIQUE,
    @JsonProperty("show_unique")
    SHOW_UNIQUE,
    @JsonProperty("percent_empty")
    PERCENT_EMPTY,
    @JsonProperty("percent_not_empty")
    PERCENT_NOT_EMPTY,
    @JsonProperty("sum")
    SUM,
    @JsonProperty("average")
    AVERAGE,
    @JsonProperty("median")
    MEDIAN,
    @JsonProperty("min")
    MIN,
    @JsonProperty("max")
    MAX,
    @JsonProperty("range")
    RANGE,
    @JsonProperty("earliest_date")
    EARLIEST_DATE,
    @JsonProperty("latest_date")
    LATEST_DATE,
    @JsonProperty("date_range")
    DATE_RANGE,
    @JsonProperty("checked")
    CHECKED,
    @JsonProperty("unchecked")
    UNCHECKED,
    @JsonProperty("percent_checked")
    PERCENT_CHECKED,
    @JsonProperty("percent_unchecked")
    PERCENT_UNCHECKED,
    @JsonProperty("count_per_group")
    COUNT_PER_GROUP,
    @JsonProperty("percent_per_group")
    PERCENT_PER_GROUP,
    @JsonProperty("show_original")
    SHOW_ORIGINAL,
}
