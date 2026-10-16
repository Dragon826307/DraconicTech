package io.github.dragon826307.draconictech.api.config;

import io.github.dragon826307.draconictech.api.text.BuiltText;
import io.github.dragon826307.draconictech.api.text.Colors;
import io.github.dragon826307.draconictech.api.text.TextBuilder;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractConfigBuilder<T, A extends AbstractConfigBuilder<T, A>> {
    public static final BuiltText DEFAULT_INVALID_REASON = TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("invalid_reason"),true).setBold(true).setColor(Colors.ARGB.RED).build();

    private final @Getter(AccessLevel.PACKAGE) String ID;
    private final @Getter(AccessLevel.PACKAGE) Class<T> type;
    private final @Getter(AccessLevel.PACKAGE) T defaultValue;
    private final @Getter(AccessLevel.PACKAGE) AbstractConfigType.StringParser<String,@Nullable T> stringParser;

    private final @Getter(AccessLevel.PACKAGE) AbstractConfigType.InvalidReason invalidReason = new AbstractConfigType.InvalidReason().set(() -> DEFAULT_INVALID_REASON);

    private @Getter(AccessLevel.PACKAGE) AbstractConfigType.ConfigValidator<T> configValidator = (value, invalidReason) -> true;
    private @Getter(AccessLevel.PACKAGE) BuiltText name = TextBuilder.empty();
    private @Getter(AccessLevel.PACKAGE) BuiltText description = TextBuilder.empty();
    private @Getter(AccessLevel.PACKAGE) String category = "";
    private @Getter(AccessLevel.PACKAGE) AbstractConfigType.ConfigParser<T> configParser = String::valueOf;
    private @Getter(AccessLevel.PACKAGE) AbstractConfigType.BuildAsCommand shouldBuild = () -> true;
    private @Getter(AccessLevel.PACKAGE) Supplier<List<String>> suggestionsSupplier = Collections::emptyList;
    private @Getter(AccessLevel.PACKAGE) Runnable postProcessor = () -> {};
    private @Getter(AccessLevel.PACKAGE) ConfigProject.UpdateCommandTreeFlags updateCommandTreeFlag = ConfigProject.UpdateCommandTreeFlags.NOTHING;
    protected AbstractConfigBuilder(String id, Class<T> type, T defaultValue, AbstractConfigType.StringParser<String, @Nullable T> stringParser) {
        ID = id;
        this.type = type;
        this.defaultValue = defaultValue;
        this.stringParser = stringParser;
    }

    @SuppressWarnings("unchecked")
    public A self() {
        return (A) this;
    }
    public A setConfigValidator(AbstractConfigType.ConfigValidator<T> configValidator) {
        this.configValidator = configValidator;
        return self();
    }
    public A setName(BuiltText name) {
        if (name == null) return self();
        this.name = name;
        return self();
    }
    public A setDescription(BuiltText description) {
        if (description == null) return self();
        this.description = description;
        return self();
    }
    public A setCategory(String category) {
        if (category == null) return self();
        this.category = category;
        return self();
    }
    public A setConfigParser(AbstractConfigType.ConfigParser<T> configParser) {
        if (configParser == null) return self();
        this.configParser = configParser;
        return self();
    }
    public A shouldBuild(AbstractConfigType.BuildAsCommand shouldBuild) {
        if (shouldBuild == null) return self();
        this.shouldBuild = shouldBuild;
        return self();
    }
    public A setSuggestionsSupplier(Supplier<List<String>> suggestionsSupplier) {
        if (suggestionsSupplier == null) return self();
        this.suggestionsSupplier = suggestionsSupplier;
        return self();
    }
    public A setPostProcessor(Runnable postProcessor) {
        if (postProcessor == null) return self();
        this.postProcessor = postProcessor;
        return self();
    }
    public A setUpdateCommandTreeFlags(ConfigProject.UpdateCommandTreeFlags updateCommandTreeFlag) {
        if (updateCommandTreeFlag == null) return self();
        this.updateCommandTreeFlag = updateCommandTreeFlag;
        return self();
    }
}
