package dragon826307.dt.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.features.microtick.MicroTickManager;
import dragon826307.dt.util.SendMessageHelper;
import dragon826307.dt.util.ServerTranslationUtil;
import dragon826307.dt.util.marker_int.FeatureCommandInt;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TickHaltCommand implements ServerCommandCallback, FeatureCommandInt {
    private static final Text CANT_FREEZE = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.cant_halt_in_freeze"),true);
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return thisCommandBranch.executes(context -> {
            //TODO : 冻结等级查询
            return 1;
        }).then(CommandManager.argument("halt level", IntegerArgumentType.integer(0,5)).executes(context -> {
            int lvl = IntegerArgumentType.getInteger(context,"halt level");
            if (lvl == 0) {
                if (MicroTickManager.INSTANCE.isFreeze()) MicroTickManager.INSTANCE.unfreeze();
                return Integer.MIN_VALUE;
            }
            ServerTickManager serverTickManager = context.getSource().getServer().getTickManager();
            if (serverTickManager.isFrozen()) {
                context.getSource().sendFeedback(() -> CANT_FREEZE,false);
                return 0;
            }
            MicroTickManager.INSTANCE.setCommandSource(context.getSource());
            DraconicTech.getMicroTickManager().setTickFrozenLevel(lvl);
            serverTickManager.stopSprinting();
            return Integer.MIN_VALUE;
        }).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)));
    }

    @Override
    public String setBranchName() {
        return "tickhalt";
    }
}
