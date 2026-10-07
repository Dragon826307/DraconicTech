package io.github.dragon826307.draconictech.api.config;

import io.github.dragon826307.draconictech.api.text.BuiltText;
import io.github.dragon826307.draconictech.api.text.TextBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class AbstractConfigType<T> {
    //必要
    private final String ID;
    private final Class<T> type;
    private final T defaultValue;
    private final ConfigValidator<T> configValidator;
    private final StringParser<String,@Nullable T> stringParser;
    //可选
    private final BuiltText name;
    private final BuiltText description;
    private final String category;
    private final ConfigParser<T> configParser;
    private final BuildAsCommand shouldBuild;
    private final InvalidReason invalidReason;
    private final Supplier<List<String>> suggestionsSupplier;
    private final Runnable postProcessor;
    private final ConfigProject.UpdateCommandTreeFlags updateCommandTreeFlag;

    protected AbstractConfigType(AbstractConfigBuilder<T, ?> builder) {
        this.ID = builder.getID();
        this.type = builder.getType();
        this.defaultValue = builder.getDefaultValue();
        this.configValidator = builder.getConfigValidator();
        this.stringParser = builder.getStringParser();
        this.name = builder.getName();
        this.description = builder.getDescription();
        this.category = builder.getCategory();
        this.configParser = builder.getConfigParser();
        this.shouldBuild = builder.getShouldBuild();
        this.invalidReason = builder.getInvalidReason();
        this.suggestionsSupplier = builder.getSuggestionsSupplier();
        this.postProcessor = builder.getPostProcessor();
        this.updateCommandTreeFlag = builder.getUpdateCommandTreeFlag();
    }

    @FunctionalInterface
    public interface ConfigParser<T> {
        @NonNull String parseConfigToString(@NonNull T configValue);
    }
    @FunctionalInterface
    public interface StringParser<@NonNull String,@NonNull T> {
        @Nullable T parseStringToConfig(@NonNull String string, InvalidReason invalidReason);
    }
    @FunctionalInterface
    public interface ConfigValidator<@NonNull T> {
        boolean check(@NonNull T value, InvalidReason invalidReason);
    }
    @FunctionalInterface
    public interface BuildAsCommand {
        boolean shouldBuild();
    }

    public String getID() {
        return ID;
    }
    public Class<T> getConfigType() {
        return type;
    }
    public T getDefaultValue() {
        return defaultValue;
    }
    public boolean isConfigValueValid(T value) {
        invalidReason.text = AbstractConfigBuilder.DEFAULT_INVALID_REASON;
        return configValidator.check(value, invalidReason);
    }
    public T parseStringToConfig(@NonNull String string) {
        invalidReason.text = AbstractConfigBuilder.DEFAULT_INVALID_REASON;
        return stringParser.parseStringToConfig(string, invalidReason);
    }
    public BuiltText getInvalidReason() {
        return invalidReason.getBuiltText();
    }
    public BuiltText getName() {
        return name;
    }
    public BuiltText getDescription() {
        return description.parse();
    }
    public String[] getCategory() {
        if (category == null || category.isEmpty()) return ArrayUtils.EMPTY_STRING_ARRAY;
        if (!category.contains("§")) return new String[]{category};
        return category.split("§");
    }
    public String parseConfigToString(@Nullable T configValue) {
        if (configValue == null) return null;
        return configParser.parseConfigToString(configValue);
    }
    public boolean shouldBuildAsCommand() {
        return shouldBuild.shouldBuild();
    }
    public String[] getSuggestions() {
        return suggestionsSupplier.get().toArray(new String[0]);
    }
    public void runPostProcessor() {
        postProcessor.run();
    }
    public ConfigProject.UpdateCommandTreeFlags getUpdateCommandTreeFlag() {
        return updateCommandTreeFlag;
    }

    public static class InvalidReason {
        private BuiltText text;
        public InvalidReason set(Supplier<BuiltText> text) {
            if (text != null) {
                this.text = text.get();
            } else {
                this.text = TextBuilder.empty();
            }
            return this;
        }
        public BuiltText getBuiltText() {
            return text;
        }
    }
}
