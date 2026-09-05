package io.github.dragon826307.draconictech.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ServerTranslationUtil {
    private static final Language LANGUAGE = Language.getInstance();
    @Nullable
    public static String getOrNull(String key, Object... args) {
        if (LANGUAGE.hasTranslation(key)) {
            if (args.length != 0) {
                return String.format(LANGUAGE.get(key), args);
            }
            return LANGUAGE.get(key);
        }else return null;
    }
    public static MutableText getTranslatedWithFallback(String key, Object... args) {
        return Text.translatableWithFallback(key, LANGUAGE.get(key), args);
    }
    public static String getFullKey(String key) {
        String callerPackage = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(s -> s.filter(f -> f.getDeclaringClass() != ServerTranslationUtil.class).findFirst().map(f -> f.getDeclaringClass().getPackageName())).orElse("UNKNOWN.PACKAGE");
        return callerPackage + "." + key;
    }
    public static MutableText getFullKeyAndTryTranslate(String key, Object... args) {
        return getTranslatedWithFallback(getFullKey(key), args);
    }
}
