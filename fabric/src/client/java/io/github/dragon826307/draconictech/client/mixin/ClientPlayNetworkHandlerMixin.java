package io.github.dragon826307.draconictech.client.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import io.github.dragon826307.draconictech.client.DraconicTechClient;
import io.github.dragon826307.draconictech.client.command.ClientCommandHandler;
import io.github.dragon826307.draconictech.client.util.ClientChatHudHelper;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = ClientPlayNetworkHandler.class,priority = 999)
public class ClientPlayNetworkHandlerMixin {
    @Unique
    private static final CommandNode<FabricClientCommandSource> clientCommandTreeBranch = new CommandDispatcher<FabricClientCommandSource>().register(ClientCommandHandler.commandRoot);
    @Unique
    private static CommandNode<FabricClientCommandSource> serverCommandTreeBranch;
    @Shadow
    private CommandDispatcher<FabricClientCommandSource> commandDispatcher;
    @Inject(method = "sendChatCommand",at = @At("HEAD"),cancellable = true)
    private void onSendChatCommand(String command, CallbackInfo ci){
        if (command.startsWith("draconictech") || command.startsWith("dt")) {
            int index = command.indexOf(" ");
            if (index == -1) return;
            String string = command.substring(index).trim();
//            if (serverCommandTreeBranch != null && isInCommandTree(string,serverCommandTreeBranch)) {
//                if (DraconicTechClient.DEBUG) ClientChatHudHelper.sendDebugMessageInChat("is server command!");
//                ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
//                if (clientPlayNetworkHandler != null) {
//                    clientPlayNetworkHandler.sendPacket(new CommandExecutionC2SPacket(command));
//                }
//            }
            if (clientCommandTreeBranch != null && isInCommandTree(string, clientCommandTreeBranch)) {
                if (DraconicTechClient.DEBUG) ClientChatHudHelper.sendDebugMessageInChat("is client command!");
                return;
            }else {
                //TODO : 判断不严谨！需要单独保存客户端与服务端指令树，随后各自判断
                ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
                if (clientPlayNetworkHandler != null) {
                    clientPlayNetworkHandler.sendPacket(new CommandExecutionC2SPacket(command));
                }
                if (DraconicTechClient.DEBUG) ClientChatHudHelper.sendDebugMessageInChat("is server command!");
            }
            ci.cancel();
        }
    }
    @Inject(method = "onCommandTree",at = @At("TAIL"))
    private void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci){
        serverCommandTreeBranch = new CommandDispatcher<FabricClientCommandSource>().getRoot();
        CommandNode<FabricClientCommandSource> node = this.commandDispatcher.getRoot().getChild("draconictech");
        if (node == null) return;
        else node.getChildren().forEach(serverCommandTreeBranch::addChild);
        this.commandDispatcher.register(ClientCommandHandler.commandRoot);
        node = this.commandDispatcher.getRoot().getChild("dt");
        if (node == null) return;
        this.commandDispatcher.register(ClientCommandHandler.commandRoot_copy);
    }
    @Unique
    private static <S> boolean isInCommandTree(String command, CommandNode<S> commandNode){
        List<String> argument = new ArrayList<>(List.of(command.split(" ")));
        CommandNode<S> currentNode = commandNode;
        for (String string : argument) {
            Set<CommandNode<S>> nodeSet = new HashSet<>(currentNode.getChildren());
            if (nodeSet.isEmpty()) return true;
            Map<String, ? extends ArgumentCommandNode<?,?>> argumentCommandNodeMap = ((CommandNodeAccessor) currentNode).getArguments();
            if (currentNode.getChild(string) == null) {
                if (!argumentCommandNodeMap.isEmpty()) continue;
                return false;
            }
            currentNode = currentNode.getChild(string);
        }
        return true;
    }
}
