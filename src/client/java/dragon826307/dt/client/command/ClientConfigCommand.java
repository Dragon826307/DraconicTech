package dragon826307.dt.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.client.config.ClientConfigProjectManager;
import dragon826307.dt.config.ConfigCommandBuilder;
import dragon826307.dt.config.ConfigProjects;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class ClientConfigCommand implements ClientCommandCallback{
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> addClientCommandBranch(LiteralArgumentBuilder<FabricClientCommandSource> thisCommandBranch) {
        return thisCommandBranch.then(ConfigCommandBuilder.buildIn(ClientCommandManager.literal("client"), ((fabricClientCommandSource, text, b) -> fabricClientCommandSource.sendFeedback(text)), ClientConfigProjectManager::setConfig, ClientConfigProjectManager::getConfig, ConfigProjects.Client.values()));
    }
    @Override
    public String setBranchName() {
        return "config";
    }
}
