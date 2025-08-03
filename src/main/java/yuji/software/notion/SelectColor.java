package yuji.software.notion;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SelectColor {
    @JsonProperty("default")
    DEFAULT,
    @JsonProperty("gray")
    GRAY,
    @JsonProperty("brown")
    BROWN,
    @JsonProperty("orange")
    ORANGE,
    @JsonProperty("yellow")
    YELLOW,
    @JsonProperty("green")
    GREEN,
    @JsonProperty("blue")
    BLUE,
    @JsonProperty("purple")
    PURPLE,
    @JsonProperty("pink")
    PINK,
    @JsonProperty("red")
    RED,
}
