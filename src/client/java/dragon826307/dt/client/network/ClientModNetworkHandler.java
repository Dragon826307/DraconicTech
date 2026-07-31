package dragon826307.dt.client.network;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.InitializePhase;
import dragon826307.dt.client.DraconicTechClient;
import dragon826307.dt.network.Mod$HelloDraconicTechC2SPacket;
import dragon826307.dt.network.Mod$HelloDraconicTechS2CPacket;
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
