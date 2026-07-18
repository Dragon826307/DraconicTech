package dragon826307.dt.client.network;

import dragon826307.dt.client.DraconicTechClient;
import dragon826307.dt.network.Mod$HelloDraconicTechC2SPacket;
import dragon826307.dt.network.Mod$HelloDraconicTechS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientModNetworkHandler {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(Mod$HelloDraconicTechS2CPacket.ID,((mod$HelloDraconicTechS2CPacket, context) -> {
            DraconicTechClient.serverHadDraconicTech = true;
            context.responseSender().sendPacket(new Mod$HelloDraconicTechC2SPacket());
        }));
    }
}
