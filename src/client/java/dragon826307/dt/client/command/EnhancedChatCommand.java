package dragon826307.dt.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.client.command.argument.EnhancedChatArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

public class EnhancedChatCommand implements ClientCommandCallback{
    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> addClientCommandBranch(LiteralArgumentBuilder<FabricClientCommandSource> thisCommandBranch) {
        return thisCommandBranch.then(ClientCommandManager.argument("chat message", EnhancedChatArgumentType.eChatArgument()).executes(context -> {
            ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
            if(handler != null){
                String message = EnhancedChatArgumentType.getMessage(context, "chat message");
                if (message != null) handler.sendChatMessage(message);
                else context.getSource().sendFeedback(Text.of("err"));
            }
            return 1;
        }));
    }
    @Override
    public String setBranchName() {
        return "chat";
    }
}
