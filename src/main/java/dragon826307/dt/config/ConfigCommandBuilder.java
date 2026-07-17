package dragon826307.dt.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.PlayerRecorder;
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
    private static final Text UNKNOW_ERR = Text.translatable("dt.config_value.unknow_err").withColor(Colors.RED);
    @SafeVarargs
    public static <S, T extends ConfigProjectsInt> LiteralArgumentBuilder<S> buildIn(LiteralArgumentBuilder<S> command, BiConsumer<S, Text> feedbackSender, BiFunction<T, Object,@NonNull Boolean> setter, Function<T, ConfigGetterValue> getter, T... configProjectsInts) {
        for (T configProject : configProjectsInts) {
            LiteralArgumentBuilder<S> singleConfigNode = LiteralArgumentBuilder.<S>literal(configProject.getName()).executes(context -> {
                feedbackSender.accept(context.getSource(), SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.current_value", configProject.getName(), String.valueOf(getter.apply(configProject).value())).withColor(0x449CCC),true));
                return 1;
            });
            RequiredArgumentBuilder<S, String> valueArg = RequiredArgumentBuilder.<S, String>argument("value", StringArgumentType.greedyString())
                    .executes(context -> {
                        String rawString = StringArgumentType.getString(context, "value");
                        Object newValue = ConfigProjectManager.parseValueFromString(rawString, configProject.getConfigType());
                        if (newValue == null && configProject.getConfigType() != ConfigType.STRING) {
                            feedbackSender.accept(context.getSource(), SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.invalid_range", rawString).withColor(Colors.LIGHT_RED),true));
                            return 0;
                        }
                        ConfigParserValue configParserValue = ConfigProjectManager.parseValue(newValue, configProject);
                        if (configParserValue.isSuccess()) {
                            return executeConfigChange(context.getSource(), configProject, newValue, feedbackSender, setter);
                        } else {
                            String errMsg = configParserValue.message() != null && !configParserValue.message().isEmpty() ? configParserValue.message() : rawString;
                            feedbackSender.accept(context.getSource(), SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.config_value.invalid_range", errMsg).withColor(Colors.LIGHT_RED),true));
                            return 0;
                        }
                    })
                    .suggests((context, builder) -> configSuggestion(builder, configProject));
            command.then(singleConfigNode.then(valueArg.requires(s -> {
                if (s instanceof ServerCommandSource serverCommandSource) {
                    boolean hasPerm = serverCommandSource.hasPermissionLevel(ServerConfigProjectManager.getConfig(ConfigProjects.Server.STATUS_COMMAND_PERMISSION).asInt());
                    boolean canModify = PlayerRecorder.isPlayerWithMod(serverCommandSource.getPlayer()) || ServerConfigProjectManager.getConfig(ConfigProjects.Server.ALLOW_PLAYER_WITH_NO_MOD_CHANGE_CONFIG).asBoolean();
                    return hasPerm && canModify;
                } else {
                    return true;
                }
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