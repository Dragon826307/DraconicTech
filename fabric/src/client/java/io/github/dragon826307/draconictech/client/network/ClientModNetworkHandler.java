package io.github.dragon826307.draconictech.client.network;

import io.github.dragon826307.draconictech.AutoInitialize;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.InitializePhase;
import io.github.dragon826307.draconictech.client.DraconicTechClient;
import io.github.dragon826307.draconictech.network.Mod$HelloDraconicTechC2SPacket;
import io.github.dragon826307.draconictech.network.Mod$HelloDraconicTechS2CPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientModNetworkHandler {
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT)
    private static void init() {
        DraconicTech.LOGGER.info("Initializing Client-Side ModNetworkHandler...");
        ClientPlayNetworking.registerGlobalReceiver(Mod$HelloDraconicTechS2CPacket.ID,((mod$HelloDraconicTechS2CPacket, context) -> {
            DraconicTechClient.serverHadDraconicTech = true;
            context.responseSender().sendPacket(new Mod$HelloDraconicTechC2SPacket());
        }));
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((minecraftClient, clientWorld) -> DraconicTechClient.serverHadDraconicTech = false);
    }
}
