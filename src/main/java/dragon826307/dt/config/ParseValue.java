package dragon826307.dt.config;

public record ParseValue(boolean isSuccess, String message) {
    public static ParseValue success() {
        return new ParseValue(true, "");
    }
    public static ParseValue failure(String message) {
        return new ParseValue(false, message);
    }
    public static ParseValue failure() {
        return new ParseValue(false, "");
    }
}
