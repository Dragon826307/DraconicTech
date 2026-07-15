package dragon826307.dt.client.util.enhanced_chat;

public record ParseResult(boolean isSuccess, String parseValue, String errMessage) {
    public static ParseResult success(String value) {
        return new ParseResult(true, value, null);
    }
    public static ParseResult error(String errMessage) {
        return new ParseResult(false, null, errMessage);
    }
}
