package io.github.dragon826307.draconictech.client.config;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.config.ConfigProject;
import io.github.dragon826307.draconictech.config.ConfigProjectManager;
import io.github.dragon826307.draconictech.util.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.util.auto_init.InitializePhase;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientConfigProjectManager extends ConfigProjectManager {
    private static final Map<ConfigProject.Client<?>, Object> CACHE = new ConcurrentHashMap<>();
    private static final Path CLIENT_CONFIG = ROOT.resolve("client.dat");
    @AutoInitialize(phase = InitializePhase.ON_CLIENT_STARTED,priority = 999)
    private static void init() {
        DraconicTech.LOGGER.info("Initializing ClientConfigProjectManager");
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new IllegalStateException("Cannot initialize ClientConfigManager on a physical Server!");
        }
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        loadALL();
        for (ConfigProject.Client<?> project: ConfigProject.Client.values()) CACHE.putIfAbsent(project, project.getDefaultValue());
        ConfigProjectManager.onSave(ClientConfigProjectManager::saveALL);
    }
    public static void saveALL(){
        atomicWrite(CLIENT_CONFIG,copyALL(CACHE));
    }
    private static void loadALL(){
        loadFormFile(CLIENT_CONFIG, ConfigProject.Client.values(),CACHE);
    }
}
