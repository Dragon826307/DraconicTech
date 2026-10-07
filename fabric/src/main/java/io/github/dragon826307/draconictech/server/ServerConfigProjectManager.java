package io.github.dragon826307.draconictech.server;

import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.config.ConfigProject;
import io.github.dragon826307.draconictech.api.config.ConfigProjectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConfigProjectManager extends ConfigProjectManager {
    private static final Path SERVER_CONFIG = ROOT.resolve("server.dat");
    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING)
    private static void init() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return;
        }
        CACHE.put(ConfigProject.Server.getClazz(), new ConcurrentHashMap<>());
        loadALL();
        ConfigProjectManager.onSave(ServerConfigProjectManager::saveALL);
    }
    @SuppressWarnings("unchecked")
    public static <T> T getConfig(ConfigProject.Server<T> project) {
        if (project == null) return null;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return project.getSinglePlayerValue();
        }
        return (T) CACHE.get(ConfigProject.Server.getClazz()).getOrDefault(project,project.getDefaultValue());
    }
    private static void saveALL(boolean feedback){
        atomicWrite(SERVER_CONFIG,copyALL(CACHE.get(ConfigProject.Server.getClazz())),feedback);
    }
    private static void loadALL(){
        loadFormFile(SERVER_CONFIG, ConfigProject.Server.values(),CACHE.get(ConfigProject.Server.getClazz()));
    }
}
