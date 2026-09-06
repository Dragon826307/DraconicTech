package io.github.dragon826307.draconictech.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

/**
 * 客户端命令回调接口
 * <p>
 * 以{@code /draconictech}或{@code /dt}为指令根
 * 通过{@link ClientCommandCallback#setBranchName()}设置一级子命令名称
 * 使用{@link ClientCommandHandler#registerCommand(ClientCommandCallback)}注册指令
 */
public interface ClientCommandCallback {
    LiteralArgumentBuilder<FabricClientCommandSource> addClientCommandBranch(LiteralArgumentBuilder<FabricClientCommandSource> thisCommandBranch);
    String setBranchName();
}
