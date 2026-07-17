package dragon826307.dt.server;

import dragon826307.dt.DraconicTech;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;

public class DraconicTechServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DraconicTech.LOGGER.info("Initializing DraconicTech Server...");
        ServerConfigProjectManager.init();

    }
}
