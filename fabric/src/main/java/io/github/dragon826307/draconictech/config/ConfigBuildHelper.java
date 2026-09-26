package io.github.dragon826307.draconictech.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ConfigBuildHelper {
    public static final Supplier<List<String>> NULL_SUGGESTIONS_SUPPLIER = ArrayList::new;
    public static final Runnable NULL_RUNNABLE = () -> {};

    public static final List<String> BOOLEAN_SUGGESTIONS = List.of("true","false");

    public static AbstractConfigType.ConfigValidator<Integer> INTEGER_VALIDATOR(int min, int max) {
        return integer -> integer >= min && integer <= max;
    }

    public static final AbstractConfigType.StringParser<String, Boolean> BOOLEAN_PARSER = s -> s.contains("true")? Boolean.TRUE : s.contains("false") ? Boolean.FALSE : null;
}
