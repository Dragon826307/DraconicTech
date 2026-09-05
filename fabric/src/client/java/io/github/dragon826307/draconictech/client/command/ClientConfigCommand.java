package io.github.dragon826307.draconictech.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.dragon826307.draconictech.client.config.ClientConfigProjectManager;
import io.github.dragon826307.draconictech.config.ConfigCommandBuilder;
import io.github.dragon826307.draconictech.config.ConfigProjects;
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
