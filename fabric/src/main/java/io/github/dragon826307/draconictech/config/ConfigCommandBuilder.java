package io.github.dragon826307.draconictech.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dragon826307.draconictech.command.argument.ConfigValueArgumentType;
import io.github.dragon826307.draconictech.platform.text.BuiltText;
import io.github.dragon826307.draconictech.platform.text.Colors;
import io.github.dragon826307.draconictech.platform.text.TextBuilder;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ConfigCommandBuilder {
    private static final BuiltText UNKNOW_ERR = TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("unknow_err"),true).setColor(Colors.ARGB.RED).build();

    @FunctionalInterface
    public interface CommandFeedbackSender<S> {
        void send(S source, BuiltText text, boolean updateCommandTree);
    }
    @FunctionalInterface
    public interface ConfigGetter<T> {
        Object get(AbstractConfigType<T> project);
    }
    @FunctionalInterface
    public interface ConfigSetter<T> {
        boolean set(AbstractConfigType<T> project, T value);
    }
    @SuppressWarnings("unchecked")
    public static <S, T> LiteralArgumentBuilder<S> buildIn(LiteralArgumentBuilder<S> branchRoot, CommandFeedbackSender<S> feedbackSender , ConfigSetter<T> setter, ConfigGetter<T> getter, AbstractConfigType<?>[] configList) {
        for (AbstractConfigType<?> configProject : configList) {
            AbstractConfigType<T> typedConfigProject = (AbstractConfigType<T>) configProject;
            LiteralArgumentBuilder<S> singleConfigNode = LiteralArgumentBuilder.<S>literal(typedConfigProject.getID()).executes(context -> {
                feedbackSender.send(context.getSource(), TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("current_value"), true, typedConfigProject.getID(), String.valueOf(getter.get(typedConfigProject))).setColor(Colors.ARGB.CYAN_300).build(),false);
                return 1;
            });
            RequiredArgumentBuilder<S, Object> moddedArg = RequiredArgumentBuilder.<S, Object>argument("value",ConfigValueArgumentType.config(typedConfigProject))
                    .executes(context -> {
                        T newValue = (T) ConfigValueArgumentType.getValueOrThrow(context, "value");
                        return executeConfigChange(context.getSource(), typedConfigProject, newValue, feedbackSender, setter);
                    })
                    .suggests((context, builder) -> configSuggestion(builder, typedConfigProject));
            RequiredArgumentBuilder<S, String> vanillaArg = RequiredArgumentBuilder.<S, String>argument("new value", StringArgumentType.greedyString())
                    .executes(context -> {
                        String rawString = StringArgumentType.getString(context, "new value");
                        if (rawString == null) {
                            feedbackSender.send(context.getSource(),UNKNOW_ERR,false);
                            return 0;
                        }
                        T newValue = typedConfigProject.parseStringToConfigValue(rawString);
                        if (newValue == null || !typedConfigProject.getConfigValidator().check(typedConfigProject.getConfigType().cast(newValue))) {
                            feedbackSender.send(context.getSource(), typedConfigProject.getInvalidReason(),false);
                            return 0;
                        }
                        return executeConfigChange(context.getSource(), typedConfigProject, newValue, feedbackSender, setter);
                    })
                    .suggests((context, builder) -> configSuggestion(builder, typedConfigProject));
            branchRoot.then(singleConfigNode.then(moddedArg.requires(s -> {
                //TODO
                return false;
            })).then(vanillaArg.requires(s -> {
                //TODO
                return true;
            }))).requires(s -> configProject.shouldBuildAsCommand().shouldBuild());
        }
        return branchRoot;
    }
    private static <S, T> int executeConfigChange(S source, AbstractConfigType<T> configProject, T parsedValue, CommandFeedbackSender<S> feedbackSender, ConfigSetter<T> setter) {
        ConfigProject.UpdateCommandTreeFlags flag = configProject.getUpdateCommandTreeFlag();
        boolean success = setter.set(configProject, parsedValue);
        boolean update = flag != ConfigProject.UpdateCommandTreeFlags.NOTHING && (flag != ConfigProject.UpdateCommandTreeFlags.IF_SUCCESS || success);
        if (success) {
            configProject.getPostProcessor().run();
            TextBuilder textBuilder = TextBuilder.start();
            if (configProject.getName() == null) {
                textBuilder.apply(configProject.getID()).setHover(TextBuilder.start().apply("ID: ").setColor(Colors.ARGB.CYAN_100).apply(configProject.getID()).setColor(Colors.ARGB.BLUE_50).build());
            } else {
                textBuilder.apply(configProject.getName()).setHover(TextBuilder.start().apply("ID: ").setColor(Colors.ARGB.CYAN_100).apply(configProject.getID()).setColor(Colors.ARGB.BLUE_50).build());
            }
            feedbackSender.send(source, TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("set_config_to"), true, configProject.getID(), String.valueOf(parsedValue)).setColor(Colors.ARGB.GREEN_A200).build(),true);
            return 1;
        } else {
            feedbackSender.send(source, UNKNOW_ERR, update);
            return 0;
        }
    }
    private static CompletableFuture<Suggestions> configSuggestion(SuggestionsBuilder builder, AbstractConfigType<?> project) {
        List<String> suggestions = project.getSuggestionsSupplier().get();
        if (suggestions != null) {
            for (String s: suggestions) builder.suggest(s);
        }
        return builder.buildFuture();
    }
}