package dragon826307.dt.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.util.marker_int.FeatureCommandInt;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

public class ServerCommandHandler {
    public static final LiteralArgumentBuilder<ServerCommandSource> commandRoot = CommandManager.literal(DraconicTech.MOD_ID).executes(ServerCommandHandler::executeBaseCommand);
    public static final LiteralArgumentBuilder<ServerCommandSource> commandRoot_copy = CommandManager.literal("dt").executes(ServerCommandHandler::executeBaseCommand);
    private static final List<ServerCommandCallback> callbacks = new ArrayList<>();
    private static boolean initialized = false;
    private static void addCommandBranch(ServerCommandCallback c){
        callbacks.add(c);
    }
    private static int executeBaseCommand(CommandContext<ServerCommandSource> context){
        DraconicTech.LOGGER.info("test");
        return 1;
    }
    public static void init(){
        if (!initialized){
            addCommandBranch(new TickHaltCommand());
            addCommandBranch(new StatusCommand());
            addCommandBranch(new ConfigCommand());
            initialized = true;
        }
        for(ServerCommandCallback c : callbacks){
            LiteralArgumentBuilder<ServerCommandSource> branch;
            if (c instanceof FeatureCommandInt) {
                branch = CommandManager.literal("feature").then(CommandManager.literal(c.setBranchName()));
            }else {
                branch = CommandManager.literal(c.setBranchName());
            }
            commandRoot.then(c.addCommandBranch(branch));
            commandRoot_copy.then(c.addCommandBranch(branch));
        }
    }
}
