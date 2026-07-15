package dragon826307.dt.command.argument.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dragon826307.dt.command.argument.ConfigValueArgumentType;
import dragon826307.dt.config.ConfigProjectsInt;
import dragon826307.dt.config.ConfigStorageType;
import dragon826307.dt.config.ConfigType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

public final class ConfigValueArgumentSerializer implements ArgumentSerializer<ConfigValueArgumentType, ConfigValueArgumentSerializer.Properties> {
    private static final Gson GSON = new Gson();
    @Override
    public void writePacket(Properties properties, PacketByteBuf buf) {
        ConfigProjectsInt config = properties.config;
        buf.writeString(config.getName());
        buf.writeEnumConstant(config.getConfigType());
        buf.writeString(config.getValidRangeAsString() != null ? config.getValidRangeAsString() : "");
        buf.writeCollection(List.of(config.getSuggestList()), PacketByteBuf::writeString);
    }

    @Override
    public Properties fromPacket(PacketByteBuf buf) {
        String name = buf.readString();
        ConfigType configType = buf.readEnumConstant(ConfigType.class);
        String validRangeStr = buf.readString();
        String validRange = validRangeStr.isEmpty() ? null : validRangeStr;
        List<String> suggestList = buf.readList(PacketByteBuf::readString);
        String[] suggests = suggestList.toArray(new String[0]);
        ConfigProjectsInt clientConfig = new ConfigProjectsInt() {
            @Override public String getName() { return name; }
            @Override public ConfigType getConfigType() { return configType; }
            @Override public ConfigStorageType getStorageType() { return null; } // 客户端解析不需要用到存储类型
            @Override public Object getDefaultValue() { return null; }           // 客户端解析不需要用到默认值
            @Override public String getValidRangeAsString() { return validRange; }
            @Override public boolean shouldUpdateCommandTree() { return false; }
            @Override public String[] getSuggestList() { return suggests; }
        };

        return new Properties(clientConfig);
    }

    @Override
    public void writeJson(Properties properties, JsonObject json) {
        ConfigProjectsInt config = properties.config;
        json.addProperty("config_name", config.getName());
        json.addProperty("config_type", config.getConfigType() != null ? config.getConfigType().name() : "UNKNOWN");
        json.addProperty("valid_range", config.getValidRangeAsString());
    }

    @Override
    public Properties getArgumentTypeProperties(ConfigValueArgumentType argumentType) {
        return new Properties(argumentType.config());
    }

    public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<ConfigValueArgumentType> {
        final ConfigProjectsInt config;
        public Properties(ConfigProjectsInt config) {
            this.config = config;
        }
        @Override
        public ConfigValueArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new ConfigValueArgumentType(config);
        }

        @Override
        public ArgumentSerializer<ConfigValueArgumentType,?> getSerializer() {
            return ConfigValueArgumentSerializer.this;
        }
    }
}
