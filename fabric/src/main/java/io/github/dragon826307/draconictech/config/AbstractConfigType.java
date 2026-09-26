package io.github.dragon826307.draconictech.config;

import io.github.dragon826307.draconictech.util.ServerTranslationUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractConfigType<T> {
    private static final Text DEFAULT_INVALID_REASON = ServerTranslationUtil.getFullKeyAndTryTranslate("invalid_reason").formatted(Formatting.RED);

    private static final HashSet<String> ID_RECORDER = new HashSet<>();

    private static final ConfigValidator<Object> NULL_CONFIG_VALIDATOR = o -> true;
    private static final BuildAsCommand ALWAYS_BUILD = () -> true;

    private Text InvalidReason;
    private Supplier<List<String>> suggestionsSupplier;
    private Runnable postProcessor;
    private ConfigProject.UpdateCommandTreeFlags updateCommandTreeFlag = ConfigProject.UpdateCommandTreeFlags.NOTHING;

    private final String ID;
    private final Class<T> type;
    private final T defaultValue;
    private final @Nullable ConfigValidator<T> configValidator;
    private final @NonNull StringParser<String,@Nullable T> parser;
    private final @Nullable BuildAsCommand shouldBuild;

    @FunctionalInterface
    public interface StringParser<@NonNull String,@NonNull T> {
        @Nullable T parse(@NonNull String string);
    }

    @FunctionalInterface
    public interface ConfigValidator<@NonNull T> {
        boolean check(@NonNull T value);
    }

    @FunctionalInterface
    public interface BuildAsCommand {
        boolean shouldBuild();
    }

    protected AbstractConfigType(String ID, Class<T> type, @NonNull T defaultValue, @Nullable ConfigValidator<T> configValidator, @NonNull StringParser<String, T> parser, @Nullable BuildAsCommand shouldBuild) {
        if (!ID_RECORDER.add(ID)) {
            throw new IllegalStateException("Duplicate ID: " + ID);
        }
        this.ID = Objects.requireNonNull(ID, "ID must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.defaultValue = Objects.requireNonNull(defaultValue, "Default value must not be null");
        this.configValidator = configValidator;
        this.parser = Objects.requireNonNull(parser, "Parser must not be null");
        this.shouldBuild = shouldBuild;
    }

    @NonNull
    public String getID() {
        return ID;
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
    @NonNull
    public StringParser<String, T> getParser() {
        return parser;
    }
    public BuildAsCommand shouldBuildAsCommand() {
        return Objects.requireNonNullElse(shouldBuild, ALWAYS_BUILD);
    }
    public Text getInvalidReason() {
        return Objects.requireNonNullElse(InvalidReason, DEFAULT_INVALID_REASON);
    }
    protected void setInvalidReason(String reason) {
        this.InvalidReason = ServerTranslationUtil.getTranslatedWithFallback("invalid_value",reason).formatted(Formatting.RED);
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
}
