package dragon826307.dt.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.project.microtick.WorldTickManager;
import dragon826307.dt.util.marker_int.FeatureCommandInt;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TickHaltCommand implements ServerCommandCallback, FeatureCommandInt {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return thisCommandBranch.executes(context -> {
            System.out.println(WorldTickManager.getTickFrozenLevel());
            return 1;
        }).then(CommandManager.argument("halt level", IntegerArgumentType.integer(0,5)).executes(context -> {
            WorldTickManager.setTickFrozenLevel(IntegerArgumentType.getInteger(context,"halt level"));
            return 1;
        }));
    }

    @Override
    public String setBranchName() {
        return "tickhalt";
    }
}
