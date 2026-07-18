package dragon826307.dt.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ServerTranslationUtil {
    private static final Language LANGUAGE = Language.getInstance();
    @Nullable
    public static String get(String key) {
        if (LANGUAGE.hasTranslation(key)) {
            return LANGUAGE.get(key);
        }else return null;
    }
    public static MutableText getTranslatedWithFallback(String key, Object... args) {
        return Text.translatableWithFallback(key, LANGUAGE.get(key), args);
    }
}
