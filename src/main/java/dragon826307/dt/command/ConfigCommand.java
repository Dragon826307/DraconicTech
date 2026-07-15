package dragon826307.dt.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.config.ConfigCommandBuilder;
import dragon826307.dt.config.ConfigProjects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ConfigCommand implements ServerCommandCallback {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return thisCommandBranch.then(ConfigCommandBuilder.buildIn(CommandManager.literal("main"), ((serverCommandSource, text) -> serverCommandSource.sendFeedback(() -> text,true)), ConfigProjects.Main.values()));
    }
    @Override
    public String setBranchName() {
        return "config";
    }
}