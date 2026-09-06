package io.github.dragon826307.draconictech.network;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
import io.github.dragon826307.draconictech.util.PlayerRecorder;
import io.github.dragon826307.draconictech.util.SendMessageHelper;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class ModNetworkHandler {
    public static final Identifier DEBUG_ID = Identifier.of(DraconicTech.MOD_ID, "debug");
    public static final Identifier HELLO_ID = Identifier.of(DraconicTech.MOD_ID, "hello");

    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_MAIN, priority = 999)
    private static void init() {
        DraconicTech.LOGGER.info("Initializing ModNetworkHandler...");
        PayloadTypeRegistry.playS2C().register(Mod$HelloDraconicTechS2CPacket.ID,Mod$HelloDraconicTechS2CPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(Mod$HelloDraconicTechC2SPacket.ID,Mod$HelloDraconicTechC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(Mod$DebugModeToggleC2SPacket.ID,Mod$DebugModeToggleC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Mod$HelloDraconicTechC2SPacket.ID,((mod$HelloDraconicTechC2SPacket, context) -> {
            PlayerRecorder.addPlayer(context.player().getUuid());
            context.server().getPlayerManager().sendCommandTree(context.player());
            DraconicTech.LOGGER.info("Send Command Tree Update Packet to {}", context.player().getStringifiedName());
        }));
        ServerPlayNetworking.registerGlobalReceiver(Mod$DebugModeToggleC2SPacket.ID,((mod$DebugModeToggleC2SPacket, context) -> {
            if (PlayerRecorder.isPlayerWithMod(context.player().getUuid()) && context.player().hasPermissionLevel(4)) {
                DraconicTech.DEBUG = !DraconicTech.DEBUG;
                DraconicTech.LOGGER.warn("Debug mode has been set to **{}** by player: {}", DraconicTech.DEBUG, context.player().getStringifiedName());
                context.player().sendMessage(SendMessageHelper.getMessage("Server-Side DEBUG MODE:" + (DraconicTech.DEBUG ? "§aON" : "§cOFF"),true));
            }else context.player().sendMessage(Text.translatable("command.failed").withColor(Colors.LIGHT_RED));
        }));
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> packetSender.sendPacket(new Mod$HelloDraconicTechS2CPacket()));
        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) -> PlayerRecorder.removePlayer(serverPlayNetworkHandler.getPlayer().getUuid()));
    }
}
