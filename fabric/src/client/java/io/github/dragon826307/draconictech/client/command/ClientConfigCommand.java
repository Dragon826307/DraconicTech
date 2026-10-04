package io.github.dragon826307.draconictech.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.client.config.ClientConfigProjectManager;
import io.github.dragon826307.draconictech.config.ConfigCommandBuilder;
import io.github.dragon826307.draconictech.config.ConfigProject;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class ClientConfigCommand implements ClientCommandCallback{
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT)
    private static void init() {
        ClientCommandHandler.registerCommand(new ClientConfigCommand());
    }
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> addClientCommandBranch(LiteralArgumentBuilder<FabricClientCommandSource> thisCommandBranch) {
        return thisCommandBranch.then(ConfigCommandBuilder.buildIn(ClientCommandManager.literal("client"), ((fabricClientCommandSource, builtText, aBoolean) -> fabricClientCommandSource.sendFeedback(builtText.parse().get())), ClientConfigProjectManager::setConfig,ClientConfigProjectManager::getConfig , ConfigProject.Client.values()));
    }
    @Override
    public String setBranchName() {
        return "config";
    }
}
