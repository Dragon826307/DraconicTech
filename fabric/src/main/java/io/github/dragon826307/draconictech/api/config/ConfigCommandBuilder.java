package io.github.dragon826307.draconictech.api.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dragon826307.draconictech.api.text.BuiltText;
import io.github.dragon826307.draconictech.api.text.Colors;
import io.github.dragon826307.draconictech.api.text.TextBuilder;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;

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
                TextBuilder hover = TextBuilder.start().apply("ID: ").setBold(true).setColor(Colors.ARGB.GOLDENROD);
                TextBuilder root = TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("name"),true).setHover(hover.apply(configProject.getID()).build()).setColor(Colors.ARGB.PURPLE_500).setBold(true);
                if (!configProject.getName().isEmpty()) {
                    root.applyBuiltText(configProject.getName()).done();
                } else {
                    root.apply(configProject.getID()).setColor(Colors.ARGB.PURPLE_700);
                }
                feedbackSender.send(context.getSource(), root.build(), false);
                if (!configProject.getDescription().isEmpty()) {
                    feedbackSender.send(context.getSource(), TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("description"), true).setBold(true).setColor(Colors.ARGB.PURPLE_300).applyBuiltText(configProject.getDescription()).done().build(), true);
                }
                return 1;
            });
            RequiredArgumentBuilder<S, String> vanillaArg = RequiredArgumentBuilder.<S, String>argument("new value", StringArgumentType.greedyString())
                    .executes(context -> {
                        String rawString = StringArgumentType.getString(context, "new value");
                        if (rawString == null) {
                            feedbackSender.send(context.getSource(),UNKNOW_ERR,false);
                            return 0;
                        }
                        T newValue = typedConfigProject.parseStringToConfig(rawString);
                        if (newValue == null || !typedConfigProject.isConfigValueValid(typedConfigProject.getConfigType().cast(newValue))) {
                            feedbackSender.send(context.getSource(), typedConfigProject.getInvalidReason(),false);
                            return 0;
                        }
                        return executeConfigChange(context.getSource(), typedConfigProject, newValue, feedbackSender, setter);
                    })
                    .suggests((context, builder) -> configSuggestion(builder, typedConfigProject));
            LiteralArgumentBuilder<S> prefixNode = null;
            for (String string : configProject.getCategory()) {
                if (prefixNode == null) {
                    prefixNode = LiteralArgumentBuilder.literal(string);
                }
                //TODO : 前缀
            }
//            if (prefixNode != null) singleConfigNode = prefixNode.then(singleConfigNode);
            branchRoot.then(singleConfigNode.then(vanillaArg.requires(s -> {
                //TODO
                return true;
            }))).requires(s -> configProject.shouldBuildAsCommand());
        }
        return branchRoot;
    }
    private static <S, T> int executeConfigChange(S source, AbstractConfigType<T> configProject, T newValue, CommandFeedbackSender<S> feedbackSender, ConfigSetter<T> setter) {
        ConfigProject.UpdateCommandTreeFlags flag = configProject.getUpdateCommandTreeFlag();
        boolean success = setter.set(configProject, newValue);
        boolean update = flag != ConfigProject.UpdateCommandTreeFlags.NOTHING && (flag != ConfigProject.UpdateCommandTreeFlags.IF_SUCCESS || success);
        if (success) {
            configProject.runPostProcessor();
            feedbackSender.send(source, TextBuilder.start().applyTranslatable(ServerTranslationUtil.getFullKey("set_config_to"), true, configProject.parseConfigToString(newValue)).setHover(TextBuilder.start().apply("ID: ").setBold(true).setColor(Colors.ARGB.GOLDENROD).apply(configProject.getID()).build()).setColor(Colors.ARGB.DARKGREEN).build(),true);
            return 1;
        } else {
            feedbackSender.send(source, UNKNOW_ERR, update);
            return 0;
        }
    }
    private static CompletableFuture<Suggestions> configSuggestion(SuggestionsBuilder builder, AbstractConfigType<?> project) {
        for (String s : project.getSuggestions()) builder.suggest(s);
        return builder.buildFuture();
    }
}