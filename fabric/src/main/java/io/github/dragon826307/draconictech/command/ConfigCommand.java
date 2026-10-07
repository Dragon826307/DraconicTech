package io.github.dragon826307.draconictech.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.config.ConfigCommandBuilder;
import io.github.dragon826307.draconictech.api.config.ConfigProject;
import io.github.dragon826307.draconictech.api.config.ConfigProjectManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ConfigCommand implements ServerCommandCallback {
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_MAIN)
    private static void init() {
        ServerCommandHandler.registerCommand(new ConfigCommand());
    }
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return thisCommandBranch.then(ConfigCommandBuilder.buildIn(CommandManager.literal("main"), ((serverCommandSource, builtText, updateCommandTree) -> {
            serverCommandSource.sendFeedback(() -> builtText.parse().get(), true);
            if (updateCommandTree) serverCommandSource.getServer().getPlayerManager().sendCommandTree(serverCommandSource.getPlayer());
        }), (ConfigProjectManager::setConfig), ConfigProjectManager::getConfig, ConfigProject.Main.values()));
    }
    @Override
    public String setBranchName() {
        return "config";
    }
}