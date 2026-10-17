package io.github.dragon826307.draconictech.server;

import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.config.ConfigProject;
import io.github.dragon826307.draconictech.api.config.ConfigProjectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class ServerConfigProjectManager extends ConfigProjectManager {
    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING)
    private static void init() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return;
        }
        register(ConfigProject.Server.values());
        loadToCacheFormFile(ConfigProject.Server.TYPE);
        ConfigProjectManager.onSave(ServerConfigProjectManager::saveALL);
    }
    public static <T> T getConfig(ConfigProject.Server<T> project) {
        if (project == null) return null;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return project.getSinglePlayerValue();
        }
        return ConfigProjectManager.getConfig(project);
    }
    private static void saveALL(boolean feedback){
        atomicWrite(ConfigProject.Server.TYPE,feedback);
    }
}
