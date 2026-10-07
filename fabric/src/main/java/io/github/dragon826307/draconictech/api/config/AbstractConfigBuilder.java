package io.github.dragon826307.draconictech.api.config;

import io.github.dragon826307.draconictech.api.text.BuiltText;
import io.github.dragon826307.draconictech.api.text.Colors;
import io.github.dragon826307.draconictech.api.text.TextBuilder;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractConfigBuilder<T, A extends AbstractConfigBuilder<T, A>> {
    public static final BuiltText DEFAULT_INVALID_REASON = TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("invalid_reason"),true).setBold(true).setColor(Colors.ARGB.RED).build();

    private final String ID;
    private final Class<T> type;
    private final T defaultValue;
    private final AbstractConfigType.ConfigValidator<T> configValidator;
    private final AbstractConfigType.StringParser<String,@Nullable T> stringParser;

    private final AbstractConfigType.InvalidReason invalidReason = new AbstractConfigType.InvalidReason().set(() -> DEFAULT_INVALID_REASON);

    private BuiltText name = TextBuilder.empty();
    private BuiltText description = TextBuilder.empty();
    private String category = "";
    private AbstractConfigType.ConfigParser<T> configParser = String::valueOf;
    private AbstractConfigType.BuildAsCommand shouldBuild = () -> true;
    private Supplier<List<String>> suggestionsSupplier = Collections::emptyList;
    private Runnable postProcessor = () -> {};
    private ConfigProject.UpdateCommandTreeFlags updateCommandTreeFlag = ConfigProject.UpdateCommandTreeFlags.NOTHING;
    protected AbstractConfigBuilder(String id, Class<T> type, T defaultValue, AbstractConfigType.ConfigValidator<T> configValidator, AbstractConfigType.StringParser<String, @Nullable T> stringParser) {
        ID = id;
        this.type = type;
        this.defaultValue = defaultValue;
        this.configValidator = configValidator;
        this.stringParser = stringParser;
    }

//    public <C extends AbstractConfigType<T>> C build(Function<AbstractConfigBuilder<T>, C> factory) {
//        return factory.apply(this);
//    }
    @SuppressWarnings("unchecked")
    public A self() {
        return (A) this;
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

    String getID() {
        return ID;
    }
    Class<T> getType() {
        return type;
    }
    T getDefaultValue() {
        return defaultValue;
    }
    AbstractConfigType.ConfigValidator<T> getConfigValidator() {
        return configValidator;
    }
    AbstractConfigType.StringParser<String, @Nullable T> getStringParser() {
        return stringParser;
    }
    BuiltText getName() {
        return name;
    }
    BuiltText getDescription() {
        return description;
    }
    String getCategory() {
        return category;
    }
    AbstractConfigType.ConfigParser<T> getConfigParser() {
        return configParser;
    }
    AbstractConfigType.BuildAsCommand getShouldBuild() {
        return shouldBuild;
    }
    AbstractConfigType.InvalidReason getInvalidReason() {
        return invalidReason;
    }
    Supplier<List<String>> getSuggestionsSupplier() {
        return suggestionsSupplier;
    }
    Runnable getPostProcessor() {
        return postProcessor;
    }
    ConfigProject.UpdateCommandTreeFlags getUpdateCommandTreeFlag() {
        return updateCommandTreeFlag;
    }

}
