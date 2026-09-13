package io.github.dragon826307.draconictech.config;

import io.github.dragon826307.draconictech.features.microtick.MicroTickManager;
import io.netty.util.internal.EmptyArrays;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ConfigProjects {
    private static final String[] BOOLEAN_SUGGESTIONS = {
        "true",
        "false"
    };
    //有效范围:
    //字符串:"正则表达式" 数值:"a-b"
    //无限定:null
    //boolean类和char类默认总是有效
    @SuppressWarnings("ClassCanBeRecord")
    public static final class Client implements ConfigProjectsInt {
        private static final List<Client> REGISTRY = new ArrayList<>();
        public static Client register(Client config) {
            REGISTRY.add(config);
            return config;
        }
        public static Client[] values() {
            return REGISTRY.toArray(new Client[0]);
        }
        private final String name;
        private final ConfigType type;
        private final Object defaultValue;
        private final String validRange;
        private final boolean shouldUpdateCommand;
        private final String[] suggestions;
        private final Runnable postProcessing;
        public Client(String name, ConfigType type, Object defaultValue, String validRange, boolean shouldUpdateCommand, String[] suggestions, Runnable configPostProcessing) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.validRange = validRange;
            this.shouldUpdateCommand = shouldUpdateCommand;
            this.suggestions = suggestions == null ? EmptyArrays.EMPTY_STRINGS : suggestions;
            this.postProcessing = configPostProcessing;
        }
        @Override
        public String getName() { return name; }
        @Override
        public ConfigType getConfigType() { return type; }
        @Override
        public ConfigStorageType getStorageType() { return ConfigStorageType.CLIENT; }
        @Override
        public Object getDefaultValue() { return defaultValue; }
        @Override
        public @Nullable String getValidRangeAsString() { return validRange; }
        @Override
        public boolean shouldUpdateCommandTree() { return shouldUpdateCommand; }
        @Override
        public String[] getSuggestList() {
            if (type == ConfigType.BOOLEAN) return BOOLEAN_SUGGESTIONS;
            else return suggestions;
        }
        @Override
        public @Nullable Runnable getPostProcessing() {return postProcessing;}
    }
    @SuppressWarnings("ClassCanBeRecord")
    public static final class Main implements ConfigProjectsInt {
        private static final List<Main> REGISTRY = new ArrayList<>();
        public static Main register(Main config) {
            REGISTRY.add(config);
            return config;
        }
        public static Main[] values() {
            return REGISTRY.toArray(new Main[0]);
        }
        public static final Main ALLOW_MODIFY_CONTAINER_SIGNAL = register(new Main("ContainerSignalModifier:allow_modify_container_signal", ConfigType.BOOLEAN, false, null, false, null, null));
        public static final Main GLOBAL_TICK_FREEZE_ORIGIN = register(new Main("MicroTickManager:default_global_tick_freeze_origin", ConfigType.STRING, "before_network_update", "^(?:before|after)_network_update$", false, new String[]{"before_network_update", "after_network_update"}, () -> MicroTickManager.getInstance().checkConfig()));
        private final String name;
        private final ConfigType type;
        private final Object defaultValue;
        private final String validRange;
        private final boolean shouldUpdateCommand;
        private final String[] suggestions;
        private final Runnable postProcessing;
        public Main(String name, ConfigType type, Object defaultValue, String validRange, boolean shouldUpdateCommand, String[] suggestions, Runnable configPostProcessing) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.validRange = validRange;
            this.shouldUpdateCommand = shouldUpdateCommand;
            this.suggestions = suggestions == null ? EmptyArrays.EMPTY_STRINGS : suggestions;
            this.postProcessing = configPostProcessing;
        }
        @Override
        public String getName() { return name; }
        @Override
        public ConfigType getConfigType() { return type; }
        @Override
        public ConfigStorageType getStorageType() { return ConfigStorageType.MAIN; }
        @Override
        public Object getDefaultValue() { return defaultValue; }
        @Override
        public @Nullable String getValidRangeAsString() { return validRange; }
        @Override
        public boolean shouldUpdateCommandTree() { return shouldUpdateCommand; }
        @Override
        public String[] getSuggestList() {
            if (type == ConfigType.BOOLEAN) return BOOLEAN_SUGGESTIONS;
            else return suggestions;
        }
        @Override
        public @Nullable Runnable getPostProcessing() {return postProcessing;}
    }
    @SuppressWarnings("ClassCanBeRecord")
    public static final class Server implements ConfigProjectsInt {
        private static final List<Server> REGISTRY = new ArrayList<>();
        public static Server register(Server config) {
            REGISTRY.add(config);
            return config;
        }
        public static Server[] values() {
            return REGISTRY.toArray(new Server[0]);
        }
        public static final Server STATUS_COMMAND_PERMISSION = register(new Server("status_command_permission_requirement", ConfigType.INT, 2, "0-4",null,4));
        public static final Server ALLOW_PLAYER_WITH_NO_MOD_CHANGE_CONFIG = register(new Server("allow_player_with_no_mod_change_config", ConfigType.BOOLEAN, false, null,null,true));
        private final String name;
        private final ConfigType type;
        private final Object defaultValue;
        private final String validRange;
        private final Runnable postProcessing;
        private final Object singlePlayerValue;
        public Server(String name, ConfigType type, Object defaultValue, String validRange,Runnable configPostProcessing,Object singlePlayerValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.validRange = validRange;
            this.postProcessing = configPostProcessing;
            this.singlePlayerValue = singlePlayerValue;
        }
        @Override
        public String getName() { return name; }
        @Override
        public ConfigType getConfigType() { return type; }
        @Override
        public ConfigStorageType getStorageType() { return ConfigStorageType.SERVER; }
        @Override
        public Object getDefaultValue() { return defaultValue; }
        @Override
        public @Nullable String getValidRangeAsString() { return validRange; }
        @Override
        public boolean shouldUpdateCommandTree() { return false; }
        @Override
        public String[] getSuggestList() { return EmptyArrays.EMPTY_STRINGS; }
        @Override
        public @Nullable Runnable getPostProcessing() {return postProcessing; }
        public Object getSinglePlayerValue() { return singlePlayerValue; }
    }
    @SuppressWarnings("ClassCanBeRecord")
    public static final class Auto implements ConfigProjectsInt {
        private static final List<Auto> REGISTRY = new ArrayList<>();
        public static Auto register(Auto config) {
            REGISTRY.add(config);
            return config;
        }
        public static Auto[] values() {
            return REGISTRY.toArray(new Auto[0]);
        }
        private final String name;
        private final Object defaultValue;
        public Auto(String name, Object defaultValue) {
            this.defaultValue = defaultValue;
            this.name = name;
        }
        @Override
        public String getName() { return name; }
        @Override
        public ConfigType getConfigType() { return null; }
        @Override
        public ConfigStorageType getStorageType() { return ConfigStorageType.AUTO; }
        @Override
        public Object getDefaultValue() { return defaultValue; }
        @Override
        public @Nullable String getValidRangeAsString() { return null; }
        @Override
        public boolean shouldUpdateCommandTree() { return false; }
        @Override
        public String[] getSuggestList() { return EmptyArrays.EMPTY_STRINGS; }
        @Override
        public @Nullable Runnable getPostProcessing() {return null;}
    }
}