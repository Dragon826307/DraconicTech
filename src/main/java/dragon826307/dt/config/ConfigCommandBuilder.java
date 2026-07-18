package dragon826307.dt.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.PlayerRecorder;
import dragon826307.dt.command.argument.ConfigValueArgumentType;
import dragon826307.dt.server.ServerConfigProjectManager;
import dragon826307.dt.util.SendMessageHelper;
import dragon826307.dt.util.ServerTranslationUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ConfigCommandBuilder {
    private static final Text UNKNOW_ERR = ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.unknow_err").withColor(Colors.RED);
    @SafeVarargs
    public static <S, T extends ConfigProjectsInt> LiteralArgumentBuilder<S> buildIn(LiteralArgumentBuilder<S> command, BiConsumer<S, Text> feedbackSender, BiFunction<T, Object,@NonNull Boolean> setter, Function<T, ConfigGetterValue> getter, T... configProjectsInts) {
        for (T configProject : configProjectsInts) {
            LiteralArgumentBuilder<S> singleConfigNode = LiteralArgumentBuilder.<S>literal(configProject.getName()).executes(context -> {
                feedbackSender.accept(context.getSource(), SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.current_value", configProject.getName(), String.valueOf(getter.apply(configProject).value())).withColor(0x449CCC),true));
                return 1;
            });
            RequiredArgumentBuilder<S, Object> moddedArg = RequiredArgumentBuilder.<S, Object>argument("value",ConfigValueArgumentType.setConfig(configProject))
                    .executes(context -> {
                        Object newValue = ConfigValueArgumentType.getValueOrThrow(context, "value");
                        return executeConfigChange(context.getSource(), configProject, newValue, feedbackSender, setter);
                    })
                    .suggests((context, builder) -> configSuggestion(builder, configProject));
            RequiredArgumentBuilder<S, String> vanillaArg = RequiredArgumentBuilder.<S, String>argument("new value", StringArgumentType.greedyString())
                    .executes(context -> {
                        String rawString = StringArgumentType.getString(context, "new value");
                        Object newValue = ConfigProjectManager.parseValueFromString(rawString, configProject.getConfigType());
                        ConfigParserValue configParserValue = ConfigProjectManager.parseValue(newValue, configProject);
                        if (configParserValue.isSuccess()) {
                            return executeConfigChange(context.getSource(), configProject, newValue, feedbackSender, setter);
                        }else {
                            feedbackSender.accept(context.getSource(), SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.invalid_range", rawString),true));
                            return 0;
                        }
                    })
                    .suggests((context, builder) -> configSuggestion(builder, configProject));
            command.then(singleConfigNode.then(moddedArg.requires(s -> {
                if (s instanceof ServerCommandSource serverCommandSource) {
                    return serverCommandSource.hasPermissionLevel(ServerConfigProjectManager.getConfig(ConfigProjects.Server.STATUS_COMMAND_PERMISSION).asInt()) && PlayerRecorder.isPlayerWithMod(serverCommandSource.getPlayer());
                }else return true;
            })));
            command.then(singleConfigNode.then(vanillaArg.requires(s -> {
                if (s instanceof ServerCommandSource serverCommandSource) {
                    return serverCommandSource.hasPermissionLevel(ServerConfigProjectManager.getConfig(ConfigProjects.Server.STATUS_COMMAND_PERMISSION).asInt()) && !PlayerRecorder.isPlayerWithMod(serverCommandSource.getPlayer()) && ServerConfigProjectManager.getConfig(ConfigProjects.Server.ALLOW_PLAYER_WITH_NO_MOD_CHANGE_CONFIG).asBoolean();
                }else return false;
            })));
        }
        return command;
    }
    private static <S, T extends ConfigProjectsInt> int executeConfigChange(S source, T configProject, Object parsedValue, BiConsumer<S, Text> feedbackSender, BiFunction<T, Object, Boolean> setter) {
        if (DraconicTech.DEBUG) {
            feedbackSender.accept(source, SendMessageHelper.getDebug("Project:'" + configProject.getName() + "'   ParseValue:'" + parsedValue + "'"));
        }
        boolean success = setter.apply(configProject, parsedValue);
        if (success) {
            feedbackSender.accept(source, SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.set_config_to", configProject.getName(), String.valueOf(parsedValue)).withColor(0x00AA00),true));
            return 1;
        } else {
            feedbackSender.accept(source, SendMessageHelper.getMessage(UNKNOW_ERR,true));
            return 0;
        }
    }
    private static CompletableFuture<Suggestions> configSuggestion(SuggestionsBuilder builder, ConfigProjectsInt project) {
        for (String s: project.getSuggestList()) builder.suggest(s);
        return builder.buildFuture();
    }
}