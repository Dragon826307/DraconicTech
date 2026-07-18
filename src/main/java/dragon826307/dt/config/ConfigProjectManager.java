package dragon826307.dt.config;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Longs;
import dragon826307.dt.DraconicTech;
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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ConfigProjectManager {
    private static final Map<ConfigProjects.Main, ConfigGetterValue> CACHE_MAIN = new ConcurrentHashMap<>();
    private static final Map<ConfigProjects.Auto, ConfigGetterValue> CACHE_AUTO = new ConcurrentHashMap<>();
    protected static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve(DraconicTech.MOD_ID).resolve("config");
    protected static final Path CLIENT_CONFIG = ROOT.resolve("client.dat");
    protected static final Path MAIN_CONFIG = ROOT.resolve("main.dat");
    protected static final Path AUTO_CONFIG = ROOT.resolve("auto.dat");
    protected static final Path SERVER_CONFIG = ROOT.resolve("server.dat");
    protected static final Path SERVER_CONFIG_STRING = ROOT.resolve("server_config.txt");
    public static void init(){
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        for (ConfigProjects.Main project: ConfigProjects.Main.values()) CACHE_MAIN.put(project, new ConfigGetterValue(project.getDefaultValue()));
        for (ConfigProjects.Auto project: ConfigProjects.Auto.values()) CACHE_AUTO.put(project, new ConfigGetterValue(project.getDefaultValue()));
        loadALL();
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
    //TODO : 未设置定时保存
    public static void saveALL(){
        Map<ConfigProjectsInt, ConfigGetterValue> snapshot = new HashMap<>(CACHE_MAIN);
        snapshot.putAll(CACHE_AUTO);
        Map<String,Object> main_config = new HashMap<>();
        Map<String,Object> auto_config = new HashMap<>();
        snapshot.forEach((key, value)->{
            Object rawValue = value.value();
            String base64key = Base64.getEncoder().encodeToString(key.getName().getBytes(StandardCharsets.UTF_8));
            if (key.getStorageType() == ConfigStorageType.MAIN) main_config.put(base64key, rawValue);
            else auto_config.put(base64key, rawValue);
        });
        atomicWrite(MAIN_CONFIG,main_config);
        atomicWrite(AUTO_CONFIG,auto_config);
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
            Files.move(tmp,target, StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
        }catch (IOException ignored){}
    }
    protected static void atomicWrite(String value){
        Path tmp = SERVER_CONFIG_STRING.resolveSibling(SERVER_CONFIG_STRING.getFileName() + ".tmp");
        try {
            Files.writeString(tmp,value);
            try (FileChannel channel = FileChannel.open(tmp,StandardOpenOption.WRITE)) {
                channel.force(true);
            }
            Files.move(tmp, SERVER_CONFIG_STRING,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
        }catch (IOException ignored){}
    }
    @Nullable
    public static Object parseValueFromString(String value, ConfigType configType) {
        return switch (configType){
            case STRING -> value;
            case BOOLEAN -> value.equals("true") || value.equals("false") ? value.equals("true") : null;
            case INT -> value.matches("^[-+]?[0-9]+$")?Integer.parseInt(value):null;
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
            if (RegexUtil.isRegex(validRange))
                return Pattern.matches(validRange, strValue) ? ConfigParserValue.success() : ConfigParserValue.failure(strValue);
            else
                return ConfigParserValue.failure("Regex syntax is invalid for config" + project.getName() + ":'" + validRange + "'");
        } else if (value instanceof Number numValue) {
            String[] split = validRange.split("-");
            if (split.length != 2) return ConfigParserValue.failure();
            Double num1 = Doubles.tryParse(split[0]);
            Double num2 = Doubles.tryParse(split[1]);
            double currentNum = numValue.doubleValue();
            if (num1 == null || num2 == null) {
                return ConfigParserValue.failure("Not a legal range representation for config" + project.getName() + ":'" + validRange + "'");
            }
            return ((currentNum >= num1 && currentNum <= num2) || (currentNum >= num2 && currentNum <= num1)) ? ConfigParserValue.success() : ConfigParserValue.failure();
        }
        return ConfigParserValue.failure(String.valueOf(value));
    }
}