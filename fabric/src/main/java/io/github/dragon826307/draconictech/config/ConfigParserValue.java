package io.github.dragon826307.draconictech.config;

public record ConfigParserValue(boolean isSuccess, String message) {
    public static ConfigParserValue success() {
        return new ConfigParserValue(true, "");
    }
    public static ConfigParserValue failure(String message) {
        return new ConfigParserValue(false, message);
    }
    public static ConfigParserValue failure() {
        return new ConfigParserValue(false, "");
    }
}
