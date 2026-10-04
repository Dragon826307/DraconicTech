package io.github.dragon826307.draconictech.network;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.auto_init.Location;
import io.github.dragon826307.draconictech.util.PlayerRecorder;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModNetworkHandler {
    @Deprecated
    public static final Identifier DEBUG_ID = Identifier.of(DraconicTech.MOD_ID, "debug");
    public static final Identifier HELLO_ID = Identifier.of(DraconicTech.MOD_ID, "hello");

    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_MAIN, priority = @Location(priority = 999))
    private static void init() {
        DraconicTech.LOGGER.info("Initializing ModNetworkHandler...");
        PayloadTypeRegistry.playS2C().register(Mod$HelloDraconicTechS2CPacket.ID,Mod$HelloDraconicTechS2CPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(Mod$HelloDraconicTechC2SPacket.ID,Mod$HelloDraconicTechC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Mod$HelloDraconicTechC2SPacket.ID,((mod$HelloDraconicTechC2SPacket, context) -> {
            PlayerRecorder.addPlayer(context.player().getUuid());
            context.server().getPlayerManager().sendCommandTree(context.player());
            DraconicTech.LOGGER.info("Send Command Tree Update Packet to {}", context.player().getStringifiedName());
        }));
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> packetSender.sendPacket(new Mod$HelloDraconicTechS2CPacket()));
        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) -> PlayerRecorder.removePlayer(serverPlayNetworkHandler.getPlayer().getUuid()));
    }
}
