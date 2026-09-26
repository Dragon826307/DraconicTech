package io.github.dragon826307.draconictech.server;

import io.github.dragon826307.draconictech.config.ConfigProject;
import io.github.dragon826307.draconictech.config.ConfigProjectManager;
import io.github.dragon826307.draconictech.util.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.util.auto_init.InitializePhase;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConfigProjectManager extends ConfigProjectManager {
    private static final Map<ConfigProject.Server<?>, Object> CACHE = new ConcurrentHashMap<>();
    private static final Path SERVER_CONFIG = ROOT.resolve("server.dat");
    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING)
    private static void init() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return;
        }
        loadALL();
        ConfigProjectManager.onSave(ServerConfigProjectManager::saveALL);
    }
    @SuppressWarnings("unchecked")
    public static <T> T getConfig(ConfigProject.Server<T> project) {
        if (project == null) return null;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return project.getSinglePlayerValue();
        }
        return (T) CACHE.getOrDefault(project,project.getDefaultValue());
    }
    public static void saveALL(){
        atomicWrite(SERVER_CONFIG,copyALL(CACHE));
    }
    private static void loadALL(){
        loadFormFile(SERVER_CONFIG, ConfigProject.Server.values(),CACHE);
    }
}
