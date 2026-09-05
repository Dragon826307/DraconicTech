package io.github.dragon826307.draconictech.client.util.enhanced_chat;

public record EnhancedChatParseResult(boolean isSuccess, String parseValue, String errMessage, Object... args) {
    public static EnhancedChatParseResult success(String value) {
        return new EnhancedChatParseResult(true, value, null);
    }
    public static EnhancedChatParseResult error(String errMessage) {
        return new EnhancedChatParseResult(false, null, errMessage);
    }
    public static EnhancedChatParseResult error(String errMessage, Object... args) {
        return new EnhancedChatParseResult(false, null, errMessage, args);
    }
}
