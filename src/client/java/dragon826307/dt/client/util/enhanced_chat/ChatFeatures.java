package dragon826307.dt.client.util.enhanced_chat;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChatFeatures {
    UTF8("utf8",0,null, ((raw_string, args) -> {
        String[] split = raw_string.toUpperCase().split("@");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            Matcher utf_matcher = Pattern.compile("^[\\dA-F]{0,8}$").matcher(s);
            if (utf_matcher.find()) {
                String hex_string = utf_matcher.group();
                sb.append(new String(HexFormat.of().parseHex(hex_string), StandardCharsets.UTF_8));
            }else return ParseResult.error("");
        }
        return ParseResult.success(sb.toString());
    })),
    REVERSE("reverse",0,null, (raw_string, args) -> ParseResult.success(StringUtils.reverse(raw_string)));
    private final String ID;
    private final int ARG_COUNT;
    private final String[] SUGGESTIONS;
    private final ChatFeatureParser PARSER;
    ChatFeatures(String id, int argCount, String[] suggestions, ChatFeatureParser parser) {
        ID = id;
        ARG_COUNT = argCount;
        SUGGESTIONS = suggestions;
        PARSER = parser;
    }
    public String getID() {return ID;}
    public ChatFeatureParser getParser() {return PARSER;}
    public int getArgCount() {return ARG_COUNT;}
    public String[] getSuggestions() {return SUGGESTIONS;}
    private static final Map<String, ChatFeatures> BY_NAME = new HashMap<>();
    static {for (ChatFeatures func : values()) {BY_NAME.put(func.ID, func);}}
    public static ChatFeatures getByID(String id) {return BY_NAME.get(id);}
    public static HashSet<String> getNames() {return new HashSet<>(BY_NAME.keySet());}
}
