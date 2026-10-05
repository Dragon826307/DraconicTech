package io.github.dragon826307.draconictech.config;

import io.github.dragon826307.draconictech.platform.text.BuiltText;
import io.github.dragon826307.draconictech.platform.text.Colors;
import io.github.dragon826307.draconictech.platform.text.TextBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractConfigType<T> {
    private static final BuiltText DEFAULT_INVALID_REASON = TextBuilder.start().applyTranslatable("invalid_reason",true).setColor(Colors.ARGB.RED).build();

    private static final HashSet<Integer> ID_RECORDER = new HashSet<>();

    private static final ConfigValidator<Object> NULL_CONFIG_VALIDATOR = o -> true;
    private static final BuildAsCommand ALWAYS_BUILD = () -> true;

    private Supplier<List<String>> suggestionsSupplier;
    private Runnable postProcessor;
    private ConfigProject.UpdateCommandTreeFlags updateCommandTreeFlag = ConfigProject.UpdateCommandTreeFlags.NOTHING;

    private final ConfigInfo<T> info;
    private final Class<T> type;
    private final T defaultValue;
    private final @Nullable ConfigValidator<T> configValidator;
    private final @NonNull StringParser<String,@Nullable T> parser;
    private final @Nullable BuildAsCommand shouldBuild;
    private final InvalidReason invalidReason;

    @FunctionalInterface
    public interface StringParser<@NonNull String,@NonNull T> {
        @Nullable T parse(@NonNull String string,InvalidReason invalidReason);
    }

    @FunctionalInterface
    public interface ConfigValidator<@NonNull T> {
        boolean check(@NonNull T value);
    }

    @FunctionalInterface
    public interface BuildAsCommand {
        boolean shouldBuild();
    }

    protected AbstractConfigType(ConfigInfo<T> info, Class<T> type, @NonNull T defaultValue, @Nullable ConfigValidator<T> configValidator, @NonNull StringParser<String, T> parser, @Nullable BuildAsCommand shouldBuild) {
        if (!ID_RECORDER.add(info.getID().hashCode())) {
            throw new IllegalStateException("Duplicate ID: " + info.getID());
        }
        this.info = Objects.requireNonNull(info, "ID must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.defaultValue = Objects.requireNonNull(defaultValue, "Default value must not be null");
        this.configValidator = configValidator;
        this.parser = Objects.requireNonNull(parser, "Parser must not be null");
        this.shouldBuild = shouldBuild;
        this.invalidReason = new InvalidReason();
    }

    @NonNull
    public String getID() {
        return info.getID();
    }
    @Nullable
    public BuiltText getName() {
        return info.getName();
    }
    @NonNull
    public Class<T> getConfigType() {
        return type;
    }
    @NonNull
    public T getDefaultValue() {
        return defaultValue;
    }
    @NonNull
    @SuppressWarnings("unchecked")
    public ConfigValidator<T> getConfigValidator() {
        return Objects.requireNonNullElse(configValidator, (ConfigValidator<T>) NULL_CONFIG_VALIDATOR);
    }
    public String[] getCategory() {
        return info.getCategory();
    }
    public BuiltText getDescription() {
        return info.getDescription();
    }
    @Nullable
    public T parseStringToConfigValue(@NonNull String string) {
        return parser.parse(string,invalidReason);
    }
    public BuildAsCommand shouldBuildAsCommand() {
        return Objects.requireNonNullElse(shouldBuild, ALWAYS_BUILD);
    }
    public BuiltText getInvalidReason() {
        return Objects.requireNonNullElse(invalidReason.getBuiltText(), DEFAULT_INVALID_REASON);
    }
    @NonNull
    public Supplier<List<String>> getSuggestionsSupplier() {
        return Objects.requireNonNullElse(suggestionsSupplier, ConfigBuildHelper.NULL_SUGGESTIONS_SUPPLIER);
    }
    protected void setSuggestionsSupplier(Supplier<List<String>> suggestionsSupplier) {
        this.suggestionsSupplier = suggestionsSupplier;
    }
    @NonNull
    public Runnable getPostProcessor() {
        return Objects.requireNonNullElse(postProcessor, ConfigBuildHelper.NULL_RUNNABLE);
    }
    protected void setPostProcessor(Runnable postProcessor) {
        this.postProcessor = postProcessor;
    }
    public ConfigProject.@NonNull UpdateCommandTreeFlags getUpdateCommandTreeFlag() {
        return updateCommandTreeFlag;
    }
    protected void setUpdateCommandTreeFlag(ConfigProject.UpdateCommandTreeFlags updateCommandTreeFlag) {
        this.updateCommandTreeFlag = updateCommandTreeFlag;
    }
    public static class InvalidReason {
        private BuiltText text;
        public void set(Supplier<BuiltText> text) {
            if (text != null) {
                this.text = text.get();
            } else {
                this.text = TextBuilder.empty();
            }
        }
        public BuiltText getBuiltText() {
            return text;
        }
    }
}
