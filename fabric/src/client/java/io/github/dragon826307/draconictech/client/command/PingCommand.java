package io.github.dragon826307.draconictech.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.dragon826307.draconictech.client.network.ClientAsyncServerPinger;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PingCommand implements ClientCommandCallback {
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT)
    private static void init() {
        ClientCommandHandler.registerCommand(new PingCommand());
    }
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> addClientCommandBranch(LiteralArgumentBuilder<FabricClientCommandSource> thisCommandBranch) {
        return thisCommandBranch.then(ClientCommandManager.argument("address", StringArgumentType.greedyString()).executes(commandContext -> {
            ClientAsyncServerPinger.ping(StringArgumentType.getString(commandContext, "address"));
            return 1;
        }));
    }

    @Override
    public String setBranchName() {
        return "ping";
    }
}