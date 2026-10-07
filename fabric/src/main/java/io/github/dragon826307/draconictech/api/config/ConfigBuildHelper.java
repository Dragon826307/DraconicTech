package io.github.dragon826307.draconictech.api.config;

import io.github.dragon826307.draconictech.api.text.Colors;
import io.github.dragon826307.draconictech.api.text.TextBuilder;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;

import java.util.List;
import java.util.function.Supplier;

public final class ConfigBuildHelper {
    public static final Supplier<List<String>> BOOLEAN_SUGGESTIONS = () -> List.of("true","false");

    public static AbstractConfigType.ConfigValidator<Integer> INTEGER_VALIDATOR(int min, int max) {
        return (integer, invalidReason) -> {
            boolean valid = integer >= min && integer <= max;
            if (!valid) {
                invalidReason.set(() -> TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("invalid_value"), true, integer).setColor(Colors.ARGB.RED_800).setBold(true).build());
            }
            return valid;
        };
    }

    public static final AbstractConfigType.StringParser<String, Boolean> BOOLEAN_PARSER = (string, invalidReason) -> string.contains("true")? Boolean.TRUE : string.contains("false") ? Boolean.FALSE : null;
}
