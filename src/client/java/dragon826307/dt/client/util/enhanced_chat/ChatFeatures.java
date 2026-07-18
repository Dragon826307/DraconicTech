package dragon826307.dt.client.util.enhanced_chat;

import com.google.common.io.BaseEncoding;
import dragon826307.dt.client.ClientConfigProjectManager;
import dragon826307.dt.client.DraconicTechClient;
import dragon826307.dt.client.util.ClientChatHudHelper;
import dragon826307.dt.config.ConfigProjects;
import net.minecraft.util.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChatFeatures {
    UTF8("utf8",0,null, ((raw_string, args) -> {
        Matcher utf_matcher = Pattern.compile("^[\\dA-F]+$").matcher(raw_string);
        if (utf_matcher.find()) {
            String hex_string = utf_matcher.group();
            String parse = new String(BaseEncoding.base16().decode(hex_string), StandardCharsets.UTF_8);
            if (DraconicTechClient.DEBUG) ClientChatHudHelper.sendDebugMessageInChat("UTF8 value:" + parse);
            if (ClientConfigProjectManager.getConfig(ConfigProjects.Client.ALLOW_ILLEGAL_CHAT_CHARACTER).asBoolean()) return EnhancedChatParseResult.success(parse);
            for (int i = 0; i < parse.length(); i++) if (!StringHelper.isValidChar(parse.charAt(i))) return EnhancedChatParseResult.error("dt.e_chat.utf.illegal_char", parse);
            return EnhancedChatParseResult.success(parse);
        }else return EnhancedChatParseResult.error("dt.e_chat.utf.format_err");
    })),
    REVERSE("reverse",0,null, (raw_string, args) -> EnhancedChatParseResult.success(StringUtils.reverse(raw_string))),
    REPEAT("repeat",1,new String[]{"1","2","3"}, (raw_string, args) -> {
        int repeat = args[0].matches("^[-+]?[0-9]+$")?Integer.parseInt(args[0]):-1;
        if (repeat < 0) return EnhancedChatParseResult.error("dt.e_chat.repeat.invalid_num");
        return EnhancedChatParseResult.success(raw_string.repeat(repeat));
    }),
    ;
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
    @Nullable
    public String[] getSuggestions() {return SUGGESTIONS;}
    private static final Map<String, ChatFeatures> BY_NAME = new HashMap<>();
    static {for (ChatFeatures func : values()) {BY_NAME.put(func.ID, func);}}
    @Nullable
    public static ChatFeatures getByID(String id) {return BY_NAME.get(id);}
    public static HashSet<String> getNames() {return new HashSet<>(BY_NAME.keySet());}
}
