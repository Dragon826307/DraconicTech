package io.github.dragon826307.draconictech.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

/**
 * 服务端命令回调接口
 * <p>
 * 以{@code /draconictech}或{@code /dt}为指令根
 * 通过{@link ServerCommandCallback#setBranchName()}设置一级子命令名称
 * 使用{@link ServerCommandHandler#registerCommand(ServerCommandCallback)}注册指令
 */
public interface ServerCommandCallback {
    LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch);
    String setBranchName();
}

