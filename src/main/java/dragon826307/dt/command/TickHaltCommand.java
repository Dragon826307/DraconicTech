package dragon826307.dt.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.util.marker_int.FeatureCommandInt;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TickHaltCommand implements ServerCommandCallback, FeatureCommandInt {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return thisCommandBranch.executes(context -> {
            //TODO : 冻结等级查询
            return 1;
        }).then(CommandManager.argument("halt level", IntegerArgumentType.integer(0,5)).executes(context -> {
            int lvl = IntegerArgumentType.getInteger(context,"halt level");
            DraconicTech.getWorldTickManager().setTickFrozenLevel(lvl);
            ServerTickManager serverTickManager = context.getSource().getServer().getTickManager();
            serverTickManager.stopSprinting();
            serverTickManager.stopStepping();
            serverTickManager.setFrozen(lvl != 0);
            return 1;
        }));
    }

    @Override
    public String setBranchName() {
        return "tickhalt";
    }
}
