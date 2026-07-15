package dragon826307.dt.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.client.network.ClientAsyncServerPinger;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PingCommand implements ClientCommandCallback {
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