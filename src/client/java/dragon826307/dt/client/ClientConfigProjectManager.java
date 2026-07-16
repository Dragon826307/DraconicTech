package dragon826307.dt.client;

import dragon826307.dt.config.ConfigProjectManager;
import dragon826307.dt.config.ConfigProjects;
import dragon826307.dt.config.ConfigProjectsInt;
import dragon826307.dt.config.ConfigValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientConfigProjectManager extends ConfigProjectManager {
    private static final Map<ConfigProjects.Client,ConfigValue> CACHE = new ConcurrentHashMap<>();
    public static void init() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            throw new IllegalStateException("Cannot initialize ClientConfigManager on a physical Server!");
        }
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        for (ConfigProjects.Client project: ConfigProjects.Client.values()) CACHE.put(project, new ConfigValue(project.getDefaultValue()));
        loadALL();
    }
    public static ConfigValue getConfig(ConfigProjects.Client project) {
        return CACHE.get(project);
    }
    public static boolean setConfig(ConfigProjects.Client project, Object value) {
        if (project.getConfigType().getClazz().isInstance(value)) {
            CACHE.put(project, new ConfigValue(value));
            return true;
        }
        return false;
    }
    public static void saveALL(){
        Map<ConfigProjectsInt, ConfigValue> snapshot = new HashMap<>(CACHE);
        Map<String,Object> client_config = new HashMap<>();
        snapshot.forEach((key, value)->{
            Object rawValue = value.value();
            String base64key = Base64.getEncoder().encodeToString(key.getName().getBytes(StandardCharsets.UTF_8));
            client_config.put(base64key,rawValue);
        });
        atomicWrite(CLIENT_CONFIG,client_config);
    }
    private static void loadALL(){
        loadFormFile(CLIENT_CONFIG,ConfigProjects.Client.values(),CACHE);
    }
}
