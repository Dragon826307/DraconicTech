package dragon826307.dt.util;

import dragon826307.dt.DraconicTech;
import net.minecraft.text.Text;

public final class SendMessageHelper {
    public static Text getMessage(Text text, boolean withPrefix) {
        if (withPrefix) {
            return Text.empty().append(DraconicTech.MOD_PREFIX).append(text);
        }else  {
            return text;
        }
    }
    public static Text getMessage(Text text) {
        return getMessage(text,false);
    }
    public static Text getMessage(String message,boolean withPrefix) {
        return getMessage(Text.of(message),withPrefix);
    }
    public static Text getMessage(String message) {
        return getMessage(Text.of(message));
    }
    public static Text getDebug(Text text) {
        Text debug_text = Text.empty().append(TextColorHelper.gradientColor("[DEBUG]: ",0x817D82,0x4A3E4D).styled(style -> style.withBold(true))).append(text);
        return getMessage(debug_text,false);
    }
    public static Text getDebug(String message) {
        return getDebug(Text.of(message));
    }
}
