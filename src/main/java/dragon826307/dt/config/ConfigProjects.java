package dragon826307.dt.config;

import io.netty.util.internal.EmptyArrays;

public final class ConfigProjects {
    private static final String[] booleanSuggestions = {
        "true",
        "false"
    };
    //有效范围:
    //字符串:"正则表达式" 数值:"a-b"
    //无限定:null
    //boolean类和char类默认总是有效
    public enum Client implements ConfigProjectsInt{
        ALLOW_ANY_CHAT_CHARACTER("EnhancedChat:allow_illegal_character",ConfigType.BOOLEAN,false,null,false,null);
        private final String name;
        private final ConfigType type;
        private final Object defaultValue;
        private final String validRange;
        private final boolean shouldUpdateCommand;
        private final String[] suggestions;
        Client(String name, ConfigType type, Object defaultValue, String validRange, boolean shouldUpdateCommand,String[] suggestions) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.validRange = validRange;
            this.shouldUpdateCommand = shouldUpdateCommand;
            this.suggestions = suggestions;
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public ConfigType getConfigType() {
            return type;
        }
        @Override
        public ConfigStorageType getStorageType() {
            return ConfigStorageType.CLIENT;
        }
        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
        @Override
        public String getValidRangeAsString() {
            return validRange;
        }
        @Override
        public boolean shouldUpdateCommandTree() {
            return shouldUpdateCommand;
        }
        @Override
        public String[] getSuggestList() {
            if (type == ConfigType.BOOLEAN) return booleanSuggestions;
            else return suggestions;
        }
    }
    public enum Main implements ConfigProjectsInt{
        ALLOW_MODIFY_CONTAINER_SIGNAL("ContainerSignalModifier:allow_modify_container_signal",ConfigType.BOOLEAN,false,null,false,null);
        private final String name;
        private final ConfigType type;
        private final Object defaultValue;
        private final String validRange;
        private final boolean shouldUpdateCommand;
        private final String[] suggestions;
        Main(String name, ConfigType type, Object defaultValue, String validRange, boolean shouldUpdateCommand,String[] suggestions) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.validRange = validRange;
            this.shouldUpdateCommand = shouldUpdateCommand;
            this.suggestions = suggestions;
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public ConfigType getConfigType() {
            return type;
        }
        @Override
        public ConfigStorageType getStorageType() {
            return ConfigStorageType.MAIN;
        }
        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
        @Override
        public String getValidRangeAsString() {
            return validRange;
        }
        @Override
        public boolean shouldUpdateCommandTree() {
            return shouldUpdateCommand;
        }
        @Override
        public String[] getSuggestList() {
            if (type == ConfigType.BOOLEAN) return booleanSuggestions;
            else return suggestions;
        }
    }
    public enum Server implements ConfigProjectsInt{
        STATUS_COMMAND_PERMISSION("status_command_permission_requirement",ConfigType.INT,2,"0-4"),
        ALLOW_PLAYER_WITH_NO_MOD_CHANGE_CONFIG("allow_player_with_no_mod_change_config",ConfigType.BOOLEAN, true,null);
        private final String name;
        private final ConfigType type;
        private final Object defaultValue;
        private final String validRange;
        Server(String name, ConfigType type, Object defaultValue, String validRange) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.validRange = validRange;
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public ConfigType getConfigType() {
            return type;
        }
        @Override
        public ConfigStorageType getStorageType() {
            return ConfigStorageType.SERVER;
        }
        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
        @Override
        public String getValidRangeAsString() {
            return validRange;
        }
        @Override
        public boolean shouldUpdateCommandTree() {
            return false;
        }
        @Override
        public String[] getSuggestList() {
            return EmptyArrays.EMPTY_STRINGS;
        }
    }
    public enum Auto implements ConfigProjectsInt{
        ;
        private final String name;
        private final Object defaultValue;
        Auto(String name,Object defaultValue){
            this.defaultValue = defaultValue;
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public ConfigType getConfigType() {
            return null;
        }
        @Override
        public ConfigStorageType getStorageType() {
            return ConfigStorageType.AUTO;
        }
        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
        @Override
        public String getValidRangeAsString() {
            return null;
        }
        @Override
        public boolean shouldUpdateCommandTree() {
            return false;
        }
        @Override
        public String[] getSuggestList() {
            return EmptyArrays.EMPTY_STRINGS;
        }
    }
}
