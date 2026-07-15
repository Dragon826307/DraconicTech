package dragon826307.dt.config;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Longs;
import dragon826307.dt.DraconicTech;
import net.fabricmc.loader.api.FabricLoader;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.regex.util.RegexUtil;

import java.io.BufferedReader;
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
    private static final Map<ConfigProjectsInt,ConfigValue> CACHE = new ConcurrentHashMap<>();
    private static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve(DraconicTech.MOD_ID).resolve("config");
    private static final Path CLIENT_CONFIG = ROOT.resolve("client.dat");
    private static final Path MAIN_CONFIG = ROOT.resolve("main.dat");
    private static final Path AUTO_CONFIG = ROOT.resolve("auto.dat");
    private static final Path SERVER_CONFIG = ROOT.resolve("server.dat");
    private static final Path SERVER_CONFIG_STRING = ROOT.resolve("server_config.txt");
    public static void init(){
        try {
            Files.createDirectories(ROOT);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        for (ConfigProjectsInt[] projectsInts:new ConfigProjectsInt[][]{ConfigProjects.Client.values(),ConfigProjects.Server.values(),ConfigProjects.Main.values(),ConfigProjects.Auto.values()}) {
            for (ConfigProjectsInt projects:projectsInts) {
                CACHE.put(projects, new ConfigValue(projects.getDefaultValue()));
            }
        }
        loadALL();
    }
    public static ConfigValue getConfig(ConfigProjectsInt project){
        return CACHE.get(project);
    }
    public static boolean setConfig(ConfigProjectsInt project,Object value){
        if(value == null){
            return false;
        }
        if(project.getConfigType() != null && !project.getConfigType().getClazz().isInstance(value)){
            return false;
        }
        CACHE.put(project,new ConfigValue(value));
        return true;
    }
    //TODO : 未设置定时保存
    public static void saveALL(){
        Map<ConfigProjectsInt,ConfigValue> snapshot = new HashMap<>(CACHE);
        Map<String,Object> client_config = new HashMap<>();
        Map<String,Object> main_config = new HashMap<>();
        Map<String,Object> auto_config = new HashMap<>();
        Map<String,Object> server_config = new HashMap<>();
        StringBuilder server_config_string = new StringBuilder();
        server_config_string.append(getNote());
        snapshot.forEach((key, value)->{
            Object rawValue = value.value();
            String base64key = Base64.getEncoder().encodeToString(key.getName().getBytes(StandardCharsets.UTF_8));
            switch(key.getStorageType()){
                case ConfigStorageType.CLIENT -> client_config.put(base64key,rawValue);
                case ConfigStorageType.MAIN -> main_config.put(base64key,rawValue);
                case ConfigStorageType.AUTO -> auto_config.put(base64key,rawValue);
                case ConfigStorageType.SERVER -> {
                    server_config.put(base64key, rawValue);
                    server_config_string.append(key.getName()).append(" = ").append(rawValue).append("\n");
                }
            }
        });
        atomicWrite(CLIENT_CONFIG,client_config);
        atomicWrite(MAIN_CONFIG,main_config);
        atomicWrite(AUTO_CONFIG,auto_config);
        atomicWrite(SERVER_CONFIG,server_config);
        String string = server_config_string.toString();
        if (!string.isEmpty()) atomicWrite(string);
    }
    private static void loadALL(){
        loadFormFile(CLIENT_CONFIG,ConfigProjects.Client.values());
        loadFormFile(MAIN_CONFIG,ConfigProjects.Main.values());
        loadFormFile(AUTO_CONFIG,ConfigProjects.Auto.values());
        loadFormFile(SERVER_CONFIG,ConfigProjects.Server.values());
        if(!Files.exists(SERVER_CONFIG_STRING)) return;
        Map<String,ConfigProjectsInt> map = new HashMap<>();
        for(ConfigProjectsInt projects:ConfigProjects.Server.values()){
            map.put(projects.getName(),projects);
        }
        try (BufferedReader bufferedReader = Files.newBufferedReader(SERVER_CONFIG_STRING, StandardCharsets.UTF_8)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;
                int index = line.indexOf("=");
                if (index == -1) continue;
                String key = line.substring(0, index).trim().replaceAll(" ","_");
                String value = line.substring(index+1);
                if (value.startsWith(" ")) value = value.substring(1);
                if (map.containsKey(key)) {
                    if (!value.isEmpty()) {
                        Object obj = parseValueFromString(value, map.get(key).getConfigType());
                        if (obj != null) CACHE.put(map.get(key), new ConfigValue(obj));
                        else DraconicTech.LOGGER.warn("Failed to parse server config project '{}'", key);
                    } else {
                        CACHE.put(map.get(key), new ConfigValue(map.get(key).getDefaultValue()));
                    }
                } else {
                    DraconicTech.LOGGER.warn("Unknow server config project '{}'", key);
                }
            }
        }catch (Exception e){
            DraconicTech.LOGGER.error("Failed to load config projects from {}",SERVER_CONFIG_STRING,e);
        }
    }
    @SuppressWarnings("unchecked")
    private static void loadFormFile(Path path,ConfigProjectsInt[] projects){
        if(!Files.exists(path)) return;
        Map<String,ConfigProjectsInt> projectNames = new HashMap<>();
        for(ConfigProjectsInt project:projects){
            projectNames.put(Base64.getEncoder().encodeToString(project.getName().getBytes(StandardCharsets.UTF_8)), project);
        }
        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(path))) {
            Map<String,Object> map = (Map<String,Object>) inputStream.readObject();
            map.forEach((k,v)->{
                if (projectNames.containsKey(k)) {
                    CACHE.put(projectNames.get(k),new ConfigValue(v));
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
    private static void atomicWrite(Path target,Object object){
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
    private static void atomicWrite(String value){
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
    public static ParseValue parseValue(Object value, ConfigProjectsInt project) {
        ConfigType configType = project.getConfigType();
        String validRange = project.getValidRangeAsString();
        if (configType == ConfigType.CHAR || configType == ConfigType.BOOLEAN || validRange == null || validRange.isEmpty()) {
            return ParseValue.success();
        }
        if (configType == ConfigType.STRING && value instanceof String strValue) {
            if (RegexUtil.isRegex(validRange))
                return Pattern.matches(validRange, strValue) ? ParseValue.success() : ParseValue.failure();
            else
                return ParseValue.failure("Regex syntax is invalid for config" + project.getName() + ":'" + validRange + "'");
        } else if (value instanceof Number numValue) {
            String[] split = validRange.split("-");
            if (split.length != 2) return ParseValue.failure();
            Double num1 = Doubles.tryParse(split[0]);
            Double num2 = Doubles.tryParse(split[1]);
            double currentNum = numValue.doubleValue();
            if (num1 == null || num2 == null) {
                return ParseValue.failure("Not a legal range representation for config" + project.getName() + ":'" + validRange + "'");
            }
            return ((currentNum >= num1 && currentNum <= num2) || (currentNum >= num2 && currentNum <= num1)) ? ParseValue.success() : ParseValue.failure();
        }
        return ParseValue.failure();
    }
    private static String getNote(){
        return """
                # 在本模组的当前配置文件中:
                // 你可以使用以 "#" 或者 "//" 为开头的行作为注释
                # 在每一行中，只有以这两种格式开头的行才会被标记为注释:
                // 对于其它情况，例如某行的内容为 "text#note"
                // 则不会被视为一行注释
                # 本模组的配置文件遵守 key=value 的格式:
                // 对于每一个合法的行，第一个"="会被视为分隔标记，其余"="则会被读取
                // 第一个"="前方的内容为key值，后方的内容为value值
                # 关于空格处理:
                // 对于key值，尽管原则上key值不能包含空格，但模组读取时会保留所有非空字符之间的空格，并将其替换为"_"(下划线)，其余空格则会被忽略
                // 对于value值，模组读取时会忽略开头第一个空格(如果有)，因此多余的空格可能会导致某一项解析失败
                # 重置与回退:
                // 如果你已经修改了某个配置，但是想回退到上一次的配置，直接将对应配置项的那一行整行删掉即可
                // 如果你想让某个配置重置为默认值，则将配置修改为"key="的格式即可(也就是value值留空)
                
                # In the current configuration file of this mod:
                // You can use lines beginning with "#" or "//" as comments
                # Only lines starting with either of these two formats are treated as comments
                // For other cases, e.g. a line containing "text#note"
                // will NOT be treated as a comment line
                # This mod's configuration file follows the key=value format:
                // For each valid line, the first "=" is treated as the delimiter; all subsequent "=" are read as part of the value
                // The content before the first "=" is the key, and the content after it is the value
                # About whitespace handling:
                // For keys, although keys原则上 should not contain spaces, the mod will preserve spaces between non‑whitespace characters and replace them with "_" (underscore); all other spaces are ignored
                // For values, the mod will ignore the first leading space (if any) when reading; therefore, extra spaces may cause parsing failures for that entry
                # Reset and rollback:
                // If you have modified a setting and want to revert to the previous configuration, simply delete the entire line of that configuration entry
                // If you want to reset a setting to its default value, change it to "key=" (i.e., leave the value blank)
                
                # 本MODの現在の設定ファイルについて:
                // 行頭が "#" または "//" で始まる行をコメントとして使用できます
                # これらの2つの形式のいずれかで始まる行のみがコメントとして扱われます
                // その他の場合、例えば "text#note" を含む行は
                // コメント行とは見なされません
                # 本MODの設定ファイルは key=value 形式に従います:
                // 有効な各行において、最初の "=" が区切り記号と見なされ、以降の "=" は値の一部として読み取られます
                // 最初の "=" より前がキー、後ろが値となります
                # 空白処理について:
                // キーについては、原則としてキーに空白を含めることはできませんが、MODは非空白文字間の空白を保持し、"_"(アンダースコア)に置き換えます。それ以外の空白は無視されます
                // 値については、読み取り時に先頭の1つの空白（存在する場合）は無視されます。そのため、余分な空白があるとその項目の解析が失敗する可能性があります
                # リセットとロールバック:
                // 設定を変更した後、前の設定に戻したい場合は、該当する設定行を丸ごと削除してください
                // 設定をデフォルト値にリセットしたい場合は、"key=" の形式（つまり値を空にする）に変更してください
                
                """;
    }
}