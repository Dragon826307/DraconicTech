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
import java.util.*;
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
    EFFECT("effect",1,ChatFeaturesUtils.SIMPLE_EFFECTS, (raw_string, args) -> {
        String effect = args[0];
        int effect_index = new ArrayList<>(List.of(ChatFeaturesUtils.SIMPLE_EFFECTS)).indexOf(effect);
        if (effect_index == -1) {
            return EnhancedChatParseResult.error("dt.e_chat.effect.unknow_arg");
        }
        StringBuilder builder = new StringBuilder();
        //TODO : 逐字检验，使用utf8点位
        return EnhancedChatParseResult.error("");
    })
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
    private static final class ChatFeaturesUtils {
        private static final String[] SIMPLE_EFFECTS = new String[]{"invert","mirror","outline"};
        private static final int[] SIMPLE_MAP = new int[]{
            161,840,35,36,37,8523,44,41,40,42,43,39,45,729,92,48,10589,12390,400,12579,987,57,12581,56,54,58,59,62,61,60,191,64,8704,42221,1021,5601,398,8498,8513,72,73,383,42200,741,87,78,79,1280,908,42212,83,42197,8745,923,77,88,8516,90,93,47,91,812,8254,44,592,113,596,112,601,607,387,613,7433,638,670,643,623,117,111,100,98,633,115,647,110,652,653,120,654,122,125,124,123,126,
            33,34,35,36,37,38,39,41,40,42,43,44,45,46,92,48,49,83,400,5416,42564,8706,2006,56,2415,58,59,62,61,60,11822,64,65,42221,390,5601,398,5559,5648,72,73,5264,670,8515,77,7438,79,43004,984,1071,42564,84,85,86,87,88,89,83,91,47,93,94,95,96,594,100,596,98,600,647,1009,5565,105,5264,670,108,109,110,111,113,112,639,42565,4723,117,118,119,120,655,115,125,124,123,126,


        };
    }
}