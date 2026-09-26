package io.github.dragon826307.draconictech.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.dragon826307.draconictech.command.argument.ConfigValueArgumentType;
import io.github.dragon826307.draconictech.util.SendMessageHelper;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ConfigCommandBuilder {
    private static final Text UNKNOW_ERR = ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.unknow_err").withColor(Colors.RED);

    @FunctionalInterface
    public interface CommandFeedbackSender<S> {
        void send(S source, Text text, boolean updateCommandTree);
    }
    @FunctionalInterface
    public interface ConfigGetter {
        Object get(AbstractConfigType<?> project);
    }
    @FunctionalInterface
    public interface ConfigSetter {
        boolean set(AbstractConfigType<?> project, Object value);
    }
    @SuppressWarnings("unchecked")
    public static <S,T extends AbstractConfigType<?>,U> LiteralArgumentBuilder<S> buildIn(LiteralArgumentBuilder<S> branchRoot, CommandFeedbackSender<S> feedbackSender , ConfigSetter setter, ConfigGetter getter, T[] configList) {
        for (AbstractConfigType<?> configProject : configList) {
            AbstractConfigType<U> typedConfigProject = (AbstractConfigType<U>) configProject;
            LiteralArgumentBuilder<S> singleConfigNode = LiteralArgumentBuilder.<S>literal(typedConfigProject.getID()).executes(context -> {
                feedbackSender.send(context.getSource(), SendMessageHelper.getMessage(ServerTranslationUtil.getFullKeyAndTryTranslate("current_value", typedConfigProject.getID(), String.valueOf(getter.get(typedConfigProject))).withColor(0x449CCC),true),false);
                return 1;
            });
            RequiredArgumentBuilder<S, Object> moddedArg = RequiredArgumentBuilder.<S, Object>argument("value",ConfigValueArgumentType.config(typedConfigProject))
                    .executes(context -> {
                        Object newValue = ConfigValueArgumentType.getValueOrThrow(context, "value");
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
                        U newValue = typedConfigProject.getParser().parse(rawString);
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
    private static <S, T extends AbstractConfigType<?>> int executeConfigChange(S source, T configProject, Object parsedValue, CommandFeedbackSender<S> feedbackSender, ConfigSetter setter) {
        ConfigProject.UpdateCommandTreeFlags flag = configProject.getUpdateCommandTreeFlag();
        boolean success = setter.set(configProject, parsedValue);
        boolean update = flag != ConfigProject.UpdateCommandTreeFlags.NOTHING && (flag != ConfigProject.UpdateCommandTreeFlags.IF_SUCCESS || success);
        if (success) {
            configProject.getPostProcessor().run();
            feedbackSender.send(source, SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.set_config_to", configProject.getID(), String.valueOf(parsedValue)).withColor(0x00AA00),true), update);
            return 1;
        } else {
            feedbackSender.send(source, SendMessageHelper.getMessage(UNKNOW_ERR,true), update);
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