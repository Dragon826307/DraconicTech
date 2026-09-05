package dragon826307.dt.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.features.microtick.MicroTickManager;
import dragon826307.dt.features.microtick.MicroTickingFlags;
import dragon826307.dt.util.SendMessageHelper;
import dragon826307.dt.util.ServerTranslationUtil;
import dragon826307.dt.util.TextColorHelper;
import dragon826307.dt.util.marker_int.FeatureCommandInt;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class TickHaltCommand implements ServerCommandCallback, FeatureCommandInt {
    private static final Text PREFIX = TextColorHelper.gradientColor("[MicroTickManager]", 14609141, 3820121);
    private static final Text CANT_FREEZE = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.cant_halt_in_freeze"),true);
    private static final Text CANT_STEP = ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.cant_step").withColor(Colors.RED);
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return thisCommandBranch.executes(context -> {
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
            return Integer.MIN_VALUE;
        }).then(CommandManager.literal("halt").then(CommandManager.argument("level",IntegerArgumentType.integer(0,5)).executes(commandContext -> {
            int lvl = IntegerArgumentType.getInteger(commandContext,"level");
            if (lvl == 0) {
                if (MicroTickManager.INSTANCE.isFreeze()) MicroTickManager.INSTANCE.unfreeze();
                return Integer.MIN_VALUE;
            }
            ServerTickManager serverTickManager = commandContext.getSource().getServer().getTickManager();
            if (serverTickManager.isFrozen()) {
                commandContext.getSource().sendFeedback(() -> CANT_FREEZE,false);
                return Integer.MIN_VALUE;
            }
            MicroTickManager.INSTANCE.setCommandSource(commandContext.getSource());
            DraconicTech.getMicroTickManager().setTickFrozenLevel(lvl);
            serverTickManager.stopSprinting();
            return Integer.MIN_VALUE;
        })).requires(s -> s.hasPermissionLevel(4))).then(CommandManager.literal("step").then(CommandManager.argument("step",IntegerArgumentType.integer(1)).executes(commandContext -> {
            int step = IntegerArgumentType.getInteger(commandContext,"step");
            if (!MicroTickManager.INSTANCE.isFreeze()) {
                commandContext.getSource().sendFeedback(() -> Text.empty().append(PREFIX).append(CANT_STEP),false);
                return Integer.MIN_VALUE;
            }
            MicroTickManager.INSTANCE.setCommandSource(commandContext.getSource());
            MicroTickManager.INSTANCE.step(step);
            commandContext.getSource().sendFeedback(() -> Text.empty().append(PREFIX).append(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.step",step)),false);
            return Integer.MIN_VALUE;
        })).requires(s -> s.hasPermissionLevel(4))).then(Util.flagCommand());
    }
    @Override
    public String setBranchName() {
        return "tickhalt";
    }
    private static final class Util {
        private static LiteralArgumentBuilder<ServerCommandSource> flagCommand() {
            LiteralArgumentBuilder<ServerCommandSource> node = CommandManager.literal("flag");
            MicroTickingFlags.getFlags().forEach((i, s) -> {
                node.then(CommandManager.literal(s).executes(context -> {
                    context.getSource().sendFeedback(() -> ServerTranslationUtil.getFullKeyAndTryTranslate("current_micro_tick_flag",String.valueOf(MicroTickManager.INSTANCE.getMicroTickFlag(i))).withColor(6750130),false);
                    return Integer.MIN_VALUE;
                }).then(CommandManager.argument("flag", BoolArgumentType.bool()).executes(context -> {
                    MicroTickManager.INSTANCE.setCommandSource(context.getSource());
                    MicroTickManager.INSTANCE.setMicroTickFlag(i,BoolArgumentType.getBool(context,"flag"));
                    context.getSource().sendFeedback(() -> ServerTranslationUtil.getFullKeyAndTryTranslate("set_micro_tick_flag",s,String.valueOf(MicroTickManager.INSTANCE.getMicroTickFlag(i))).withColor(6750130),true);
                    return Integer.MIN_VALUE;
                })));
            });
            return node;
        }
    }
}
