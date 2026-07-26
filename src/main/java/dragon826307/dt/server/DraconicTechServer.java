package dragon826307.dt.server;

import dragon826307.dt.DraconicTech;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class DraconicTechServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DraconicTech.LOGGER.info("Initializing DraconicTech Server...");
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ServerConfigProjectManager.init();
        });
    }
}
