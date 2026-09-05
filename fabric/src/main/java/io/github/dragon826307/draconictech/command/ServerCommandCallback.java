package io.github.dragon826307.draconictech.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public interface ServerCommandCallback {
    LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch);
    String setBranchName();
}

