package dragon826307.dt.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.PlayerRecorder;
import dragon826307.dt.command.argument.ConfigValueArgumentType;
import dragon826307.dt.util.SendMessageHelper;
import dragon826307.dt.util.ServerTranslationUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public final class ConfigCommandBuilder {
    private static final Text UNKNOW_ERR = Text.translatable("dt.config_value.unknow_err").withColor(Colors.RED);
    public static <S> LiteralArgumentBuilder<S> buildIn(LiteralArgumentBuilder<S> command, BiConsumer<S, Text> feedbackSender, ConfigProjectsInt... configProjectsInts) {
        for (ConfigProjectsInt configProject : configProjectsInts) {
            LiteralArgumentBuilder<S> singleConfigNode = LiteralArgumentBuilder.<S>literal(configProject.getName()).executes(context -> {
                feedbackSender.accept(context.getSource(), SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.current_value", configProject.getName(), String.valueOf(ConfigProjectManager.getConfig(configProject).value())).withColor(0x449CCC),true));
                return 1;
            });
            RequiredArgumentBuilder<S, Object> moddedArg = RequiredArgumentBuilder.<S, Object>argument("value",ConfigValueArgumentType.setConfig(configProject))
                .executes(context -> {
                    Object newValue = ConfigValueArgumentType.getValueOrThrow(context, "value");
                    return executeConfigChange(context.getSource(), configProject, newValue, feedbackSender);
                })
                .suggests((context, builder) -> configSuggestion(builder, configProject));
            RequiredArgumentBuilder<S, String> vanillaArg = RequiredArgumentBuilder.<S, String>argument("new value", StringArgumentType.greedyString())
                .executes(context -> {
                    String rawString = StringArgumentType.getString(context, "new value");
                    Object newValue = ConfigProjectManager.parseValueFromString(rawString, configProject.getConfigType());
                    ParseValue parseValue = ConfigProjectManager.parseValue(newValue, configProject);
                    if (parseValue.isSuccess()) {
                        return executeConfigChange(context.getSource(), configProject, newValue, feedbackSender);
                    }else {
                        feedbackSender.accept(context.getSource(), SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.invalid_range", rawString),true));
                        return 0;
                    }
                })
                .suggests((context, builder) -> configSuggestion(builder, configProject));
            command.then(singleConfigNode.then(moddedArg.requires(s -> {
                if (s instanceof ServerCommandSource serverCommandSource) {
                    return serverCommandSource.hasPermissionLevel(ConfigProjectManager.getConfig(ConfigProjects.Server.STATUS_COMMAND_PERMISSION).asInt()) && PlayerRecorder.isPlayerWithMod(serverCommandSource.getPlayer());
                }else return true;
            })));
            command.then(singleConfigNode.then(vanillaArg.requires(s -> {
                if (s instanceof ServerCommandSource serverCommandSource) {
                    return serverCommandSource.hasPermissionLevel(ConfigProjectManager.getConfig(ConfigProjects.Server.STATUS_COMMAND_PERMISSION).asInt()) && !PlayerRecorder.isPlayerWithMod(serverCommandSource.getPlayer()) && ConfigProjectManager.getConfig(ConfigProjects.Server.ALLOW_PLAYER_WITH_NO_MOD_CHANGE_CONFIG).asBoolean();
                }else return false;
            })));
        }
        return command;
    }
    private static <S> int executeConfigChange(S source, ConfigProjectsInt configProject, Object parsedValue, BiConsumer<S, Text> feedbackSender) {
        if (DraconicTech.DEBUG) {
            feedbackSender.accept(source, SendMessageHelper.getDebug("Project:'" + configProject.getName() + "'   ParseValue:'" + parsedValue + "'"));
        }
        boolean success = ConfigProjectManager.setConfig(configProject, parsedValue);
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