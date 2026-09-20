package yuji.software.openbd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenBDBook(
        @JsonProperty("onix") Onix onix,
        @JsonProperty("summary") Summary summary
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Onix(
            @JsonProperty("DescriptiveDetail") DescriptiveDetail descriptiveDetail
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DescriptiveDetail(
            @JsonProperty("TitleDetail") TitleDetail titleDetail,
            @JsonProperty("Contributor") List<Contributor> contributor
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TitleDetail(
            @JsonProperty("TitleType") String titleType,
            @JsonProperty("TitleElement") TitleElement titleElement
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TitleElement(
            @JsonProperty("TitleElementLevel") String titleElementLevel,
            @JsonProperty("TitleText") TitleText titleText
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TitleText(
            @JsonProperty("collationkey") String collationkey,
            @JsonProperty("content") String content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Contributor(
            @JsonProperty("SequenceNumber") String sequenceNumber,
            @JsonProperty("PersonName") PersonName personName
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PersonName(
            @JsonProperty("content") String content,
            @JsonProperty("collationkey") String collationkey
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Summary(
            @JsonProperty("isbn") String isbn,
            @JsonProperty("title") String title,
            @JsonProperty("volume") String volume,
            @JsonProperty("series") String series,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("pubdate") String pubdate,
            @JsonProperty("cover") String cover,
            @JsonProperty("author") String author
    ) {
    }
}
