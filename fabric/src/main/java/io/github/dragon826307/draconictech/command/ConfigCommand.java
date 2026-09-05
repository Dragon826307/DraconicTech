package io.github.dragon826307.draconictech.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.dragon826307.draconictech.config.ConfigCommandBuilder;
import io.github.dragon826307.draconictech.config.ConfigProjectManager;
import io.github.dragon826307.draconictech.config.ConfigProjects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ConfigCommand implements ServerCommandCallback {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return thisCommandBranch.then(ConfigCommandBuilder.buildIn(CommandManager.literal("main"), ((serverCommandSource, text, updateCommandTree) -> {
            serverCommandSource.sendFeedback(() -> text, true);
            if (updateCommandTree) serverCommandSource.getServer().getPlayerManager().sendCommandTree(serverCommandSource.getPlayer());
        }), ConfigProjectManager::setConfig, ConfigProjectManager::getConfig, ConfigProjects.Main.values()));
    }
    @Override
    public String setBranchName() {
        return "config";
    }
}