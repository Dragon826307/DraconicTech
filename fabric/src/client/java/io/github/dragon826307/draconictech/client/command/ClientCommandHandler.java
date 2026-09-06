package io.github.dragon826307.draconictech.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.client.command.argument.EnhancedChatArgumentType;
import io.github.dragon826307.draconictech.command.argument.ConfigValueArgumentType;
import io.github.dragon826307.draconictech.command.argument.serializer.ConfigValueArgumentSerializer;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class ClientCommandHandler {
    public static final LiteralArgumentBuilder<FabricClientCommandSource> commandRoot = ClientCommandManager.literal(DraconicTech.MOD_ID).executes(ClientCommandHandler::executeBaseCommand);
    public static final LiteralArgumentBuilder<FabricClientCommandSource> commandRoot_copy = ClientCommandManager.literal("dt").executes(ClientCommandHandler::executeBaseCommand);
    private static final List<ClientCommandCallback> callbacks = new ArrayList<>();
    private static int executeBaseCommand(CommandContext<FabricClientCommandSource> context){
        DraconicTech.LOGGER.info("test");
        return 1;
    }

    @AutoInitialize(phase = InitializePhase.ON_CLIENT_STARTED)
    private static void init(){
        DraconicTech.LOGGER.info("Initializing ClientCommandHandler");
        for(ClientCommandCallback c : callbacks){
            commandRoot.then(c.addClientCommandBranch(ClientCommandManager.literal(c.setBranchName())));
            commandRoot_copy.then(c.addClientCommandBranch(ClientCommandManager.literal(c.setBranchName())));
        }
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(ClientCommandHandler.commandRoot);
            dispatcher.register(ClientCommandHandler.commandRoot_copy);
        });
    }
    public static void registerCommand(ClientCommandCallback c){
        callbacks.add(c);
    }
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT)
    private static void register() {
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(DraconicTech.MOD_ID,"enhanced_chat"), EnhancedChatArgumentType.class, ConstantArgumentSerializer.of(EnhancedChatArgumentType::eChatArgument));
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(DraconicTech.MOD_ID,"config_value"), ConfigValueArgumentType.class, new ConfigValueArgumentSerializer());
    }
}
