package io.github.dragon826307.draconictech.client.config;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.auto_init.Location;
import io.github.dragon826307.draconictech.api.config.ConfigProject;
import io.github.dragon826307.draconictech.api.config.ConfigProjectManager;
import io.github.dragon826307.draconictech.client.command.ClientCommandHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;

public final class ClientConfigProjectManager extends ConfigProjectManager {
    @AutoInitialize(phase = InitializePhase.ON_CLIENT_STARTED, priority = @Location(provideTo = ClientCommandHandler.class))
    private static void init() {
        DraconicTech.LOGGER.info("Initializing ClientConfigProjectManager");
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new IllegalStateException("Cannot initialize ClientConfigManager on a physical Server!");
        }
        register(ConfigProject.Client.values());
        loadToCacheFormFile(ConfigProject.Client.TYPE);
        try {
            Files.createDirectories(ROOT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ConfigProjectManager.onSave(ClientConfigProjectManager::saveALL);
    }
    private static void saveALL(boolean feedback) {
        atomicWrite(ConfigProject.Client.TYPE, feedback);
    }
}
