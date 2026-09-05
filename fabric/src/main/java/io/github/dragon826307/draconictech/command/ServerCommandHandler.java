package io.github.dragon826307.draconictech.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.dragon826307.draconictech.AutoInitialize;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.InitializePhase;
import io.github.dragon826307.draconictech.util.marker_int.FeatureCommandInt;
import net.minecraft.server.MinecraftServer;
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
    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING)
    private static void init(){
        if (!initialized){
            addCommandBranch(new TickHaltCommand());
            addCommandBranch(new StatusCommand());
            addCommandBranch(new ConfigCommand());
            initialized = true;
        }
        for(ServerCommandCallback c : callbacks) {
            LiteralArgumentBuilder<ServerCommandSource> branch;
            if (c instanceof FeatureCommandInt) branch = CommandManager.literal("features").then(c.addCommandBranch(CommandManager.literal(c.setBranchName())));
            else branch = c.addCommandBranch(CommandManager.literal(c.setBranchName()));
            commandRoot.then(branch);
            commandRoot_copy.then(branch);
        }
    }
    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING, priority = 1001)
    private static void registerAllCommands(MinecraftServer server){
        server.getCommandManager().getDispatcher().register(ServerCommandHandler.commandRoot);
        server.getCommandManager().getDispatcher().register(ServerCommandHandler.commandRoot_copy);
    }
}
