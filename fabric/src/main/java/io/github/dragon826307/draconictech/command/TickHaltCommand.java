package io.github.dragon826307.draconictech.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.features.microtick.MicroTickManager;
import io.github.dragon826307.draconictech.features.microtick.MicroTickingFlags;
import io.github.dragon826307.draconictech.util.*;
import io.github.dragon826307.draconictech.util.marker_int.FeatureCommandInt;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class TickHaltCommand implements ServerCommandCallback, FeatureCommandInt {
    private static final Text PREFIX = TextColorHelper.gradientColor("[MicroTickManager]", 14609141, 3820121);
    private static final Text CANT_FREEZE = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.cant_halt_in_freeze"),true);
    private static final Text CANT_STEP = ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.cant_step").withColor(Colors.RED);
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_MAIN)
    private static void init() {
        ServerCommandHandler.registerCommand(new TickHaltCommand());
    }
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return ServerCommandHandler.instantRun(thisCommandBranch.executes(context -> {
            int lvl = MicroTickManager.INSTANCE.getTickFrozenLevel();
            context.getSource().sendFeedback(() -> Text.empty().append(PREFIX).append(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.frozen_lvl",switch (lvl){
                case 0 -> "normal(0)";
                case 1 -> "global(1)";
                case 2 -> "phase(2)";
                case 3 -> "event(3)";
                case 4 -> "update(4)";
                case 5 -> "(5)";
                default -> "unknow";
            }).withColor(7574450)),false);
            return 1;
        }).then(CommandManager.literal("halt").then(ServerCommandHandler.instantRun(CommandManager.argument("level",IntegerArgumentType.integer(0,5)).executes(commandContext -> {
            int lvl = IntegerArgumentType.getInteger(commandContext,"level");
            if (lvl == 0) {
                if (MicroTickManager.INSTANCE.isFreeze()) MicroTickManager.INSTANCE.unfreeze();
                return 1;
            }
            ServerTickManager serverTickManager = commandContext.getSource().getServer().getTickManager();
            if (serverTickManager.isFrozen()) {
                commandContext.getSource().sendFeedback(() -> CANT_FREEZE,false);
                return 1;
            }
            MicroTickManager.INSTANCE.setCommandSource(commandContext.getSource());
            DraconicTech.getMicroTickManager().setTickFrozenLevel(lvl);
            serverTickManager.stopSprinting();
            return 1;
        }))).requires(s -> s.hasPermissionLevel(4))).then(CommandManager.literal("step").then(ServerCommandHandler.instantRun(CommandManager.argument("step",IntegerArgumentType.integer(1)).executes(commandContext -> {
            int step = IntegerArgumentType.getInteger(commandContext,"step");
            if (!MicroTickManager.INSTANCE.isFreeze()) {
                commandContext.getSource().sendFeedback(() -> Text.empty().append(PREFIX).append(CANT_STEP),false);
                return 1;
            }
            MicroTickManager.INSTANCE.setCommandSource(commandContext.getSource());
            MicroTickManager.INSTANCE.step(step);
            commandContext.getSource().sendFeedback(() -> Text.empty().append(PREFIX).append(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.step",step)),false);
            return 1;
        }))).requires(s -> s.hasPermissionLevel(4))).then(ServerCommandHandler.instantRun(Util.flagCommand())));
    }
    @Override
    public String setBranchName() {
        return "tick";
    }
    private static final class Util {
        private static LiteralArgumentBuilder<ServerCommandSource> flagCommand() {
            LiteralArgumentBuilder<ServerCommandSource> node = CommandManager.literal("flag");
            MicroTickingFlags.getFlags().forEach((i, s) -> {
                node.then(CommandManager.literal(s).executes(context -> {
                    context.getSource().sendFeedback(() -> ServerTranslationUtil.getFullKeyAndTryTranslate("current_micro_tick_flag",String.valueOf(MicroTickManager.INSTANCE.getMicroTickFlag(i))).withColor(6750130),false);
                    return 1;
                }).then(CommandManager.argument("flag", BoolArgumentType.bool()).executes(context -> {
                    MicroTickManager.INSTANCE.setCommandSource(context.getSource());
                    MicroTickManager.INSTANCE.setMicroTickFlag(i,BoolArgumentType.getBool(context,"flag"));
                    context.getSource().sendFeedback(() -> ServerTranslationUtil.getFullKeyAndTryTranslate("set_micro_tick_flag",s,String.valueOf(MicroTickManager.INSTANCE.getMicroTickFlag(i))).withColor(6750130),true);
                    return 1;
                })));
            });
            return node;
        }
    }
}
