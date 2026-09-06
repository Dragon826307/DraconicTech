package io.github.dragon826307.draconictech.config;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.regex.util.RegexUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class ConfigProjectManager {
    private static final Map<ConfigProjects.Main, ConfigGetterValue> CACHE_MAIN = new ConcurrentHashMap<>();
    private static final Map<ConfigProjects.Auto, ConfigGetterValue> CACHE_AUTO = new ConcurrentHashMap<>();
    protected static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve(DraconicTech.MOD_ID).resolve("config");
    protected static final Path CLIENT_CONFIG = ROOT.resolve("client.dat");
    protected static final Path MAIN_CONFIG = ROOT.resolve("main.dat");
    protected static final Path AUTO_CONFIG = ROOT.resolve("auto.dat");
    protected static final Path SERVER_CONFIG = ROOT.resolve("server.dat");

    private static ScheduledFuture<?> saver_task = null;
    private static final ScheduledExecutorService CONFIG_SAVER_THREAD = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ConfigSaverThread");
        thread.setDaemon(true);
        return thread;
    });

    private static final List<Runnable> ON_SAVING_CONFIG = new ArrayList<>();

    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING, priority = 999)
    private static void init(){
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        loadALL();
        for (ConfigProjects.Main project: ConfigProjects.Main.values()) CACHE_MAIN.putIfAbsent(project, new ConfigGetterValue(project.getDefaultValue()));
        for (ConfigProjects.Auto project: ConfigProjects.Auto.values()) CACHE_AUTO.putIfAbsent(project, new ConfigGetterValue(project.getDefaultValue()));
        if (saver_task == null) {
            saver_task = CONFIG_SAVER_THREAD.scheduleAtFixedRate(() -> {
                saveALL();
                for (Runnable runnable : ON_SAVING_CONFIG) {
                    runnable.run();
                }
            },3,1, TimeUnit.MINUTES);
        }
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (saver_task != null && !saver_task.isCancelled()) {
                saver_task.cancel(false);
            }
        });
    }

    public static void onConfigSave(Runnable runnable){
        ON_SAVING_CONFIG.add(runnable);
    }

    public static ConfigGetterValue getConfig(ConfigProjects.Main project){
        return CACHE_MAIN.get(project);
    }
    public static ConfigGetterValue getConfig(ConfigProjects.Auto project){
        return CACHE_AUTO.get(project);
    }
    public static boolean setConfig(ConfigProjects.Main project, Object value) {
        if (project.getConfigType().getClazz().isInstance(value)) {
            CACHE_MAIN.put(project, new ConfigGetterValue(value));
            return true;
        }
        return false;
    }
    public static boolean setConfig(ConfigProjects.Auto project, Object value) {
        CACHE_AUTO.put(project, new ConfigGetterValue(value));
        return true;
    }
    public static void saveALL(){
        atomicWrite(MAIN_CONFIG,copyALL(CACHE_MAIN));
        atomicWrite(AUTO_CONFIG,copyALL(CACHE_AUTO));
    }
    protected static <T extends ConfigProjectsInt> Map<String,Object> copyALL(Map<T,ConfigGetterValue> cache){
        Map<ConfigProjectsInt, ConfigGetterValue> snapshot = new HashMap<>(cache);
        Map<String,Object> config = new HashMap<>();
        snapshot.forEach((key, value)->{
            Object rawValue = value.value();
            String base64key = Base64.getEncoder().encodeToString(key.getName().getBytes(StandardCharsets.UTF_8));
            config.put(base64key, rawValue);
        });
        return config;
    }
    private static void loadALL(){
        loadFormFile(MAIN_CONFIG,ConfigProjects.Main.values(),CACHE_MAIN);
        loadFormFile(AUTO_CONFIG,ConfigProjects.Auto.values(),CACHE_AUTO);
    }
    @SuppressWarnings("unchecked")
    protected static <T extends ConfigProjectsInt> void loadFormFile(Path path, T[] projects, Map<T, ConfigGetterValue> entry){
        if(!Files.exists(path)) return;
        Map<String,T> projectNames = new HashMap<>();
        for(T project:projects){
            projectNames.put(Base64.getEncoder().encodeToString(project.getName().getBytes(StandardCharsets.UTF_8)), project);
        }
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(path))) {
            Map<String,Object> map = (Map<String,Object>) inputStream.readObject();
            map.forEach((k,v)->{
                if (projectNames.containsKey(k)) {
                    entry.put(projectNames.get(k),new ConfigGetterValue(v));
                }else {
                    String decodedKey = k;
                    try {
                        decodedKey = new String(Base64.getDecoder().decode(k), StandardCharsets.UTF_8);
                    } catch (Exception ignored) {}
                    DraconicTech.LOGGER.warn("Unknown project name {}",decodedKey);
                }
            });
        }catch (Exception e){
            DraconicTech.LOGGER.error("Failed to load config projects from {}",path,e);
        }
    }
    protected static void atomicWrite(Path target, Object object){
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
        try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(tmp))) {
            outputStream.writeObject(object);
            outputStream.flush();
        }catch (IOException e){return;}
        try (FileChannel channel = FileChannel.open(tmp, StandardOpenOption.WRITE)) {
            channel.force(true);
        }catch (IOException ignored){}
        try {
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            DraconicTech.LOGGER.error("Failed to replace config file: {}", target, e);
        }
    }
    @Nullable
    public static Object parseValueFromString(String value, ConfigType configType) {
        return switch (configType){
            case STRING -> value;
            case BOOLEAN -> value.equalsIgnoreCase("true") ? Boolean.TRUE : (value.equalsIgnoreCase("false") ? Boolean.FALSE : null);
            case INT -> Ints.tryParse(value);
            case DOUBLE -> Doubles.tryParse(value);
            case FLOAT -> Floats.tryParse(value);
            case LONG -> Longs.tryParse(value);
            case CHAR -> value.isEmpty() ? ' ' : value.charAt(0);
        };
    }
    public static ConfigParserValue parseValue(Object value, ConfigProjectsInt project) {
        ConfigType configType = project.getConfigType();
        String validRange = project.getValidRangeAsString();
        if (configType == ConfigType.CHAR || configType == ConfigType.BOOLEAN || validRange == null || validRange.isEmpty()) {
            return ConfigParserValue.success();
        }
        if (configType == ConfigType.STRING && value instanceof String strValue) {
            if (RegexUtil.isRegex(validRange)) {
                return Pattern.matches(validRange, strValue) ? ConfigParserValue.success() : ConfigParserValue.failure(strValue);
            }
            else {
                return ConfigParserValue.failure("Regex syntax is invalid for config" + project.getName() + ":'" + validRange + "'");
            }
        } else if (value instanceof Number numValue) {
            Pattern rangePattern = Pattern.compile("^([-+]?\\d+(?:\\.\\d+)?)-([-+]?\\d+(?:\\.\\d+)?)$");
            var matcher = rangePattern.matcher(validRange.trim());
            if (!matcher.matches()) {
                return ConfigParserValue.failure("Not a legal range representation for config " + project.getName() + ": '" + validRange + "'");
            }
            Double num1 = Doubles.tryParse(matcher.group(1));
            Double num2 = Doubles.tryParse(matcher.group(2));
            if (num1 == null || num2 == null) return ConfigParserValue.failure();
            double currentNum = numValue.doubleValue();
            double min = Math.min(num1, num2);
            double max = Math.max(num1, num2);
            return (currentNum >= min && currentNum <= max) ? ConfigParserValue.success() : ConfigParserValue.failure();
        }
        return ConfigParserValue.failure(String.valueOf(value));
    }
}