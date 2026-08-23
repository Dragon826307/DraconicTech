package dragon826307.dt.command.argument.serializer;

import com.google.gson.JsonObject;
import dragon826307.dt.command.argument.ConfigValueArgumentType;
import dragon826307.dt.config.ConfigPostProcessing;
import dragon826307.dt.config.ConfigProjectsInt;
import dragon826307.dt.config.ConfigStorageType;
import dragon826307.dt.config.ConfigType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public final class ConfigValueArgumentSerializer implements ArgumentSerializer<ConfigValueArgumentType, ConfigValueArgumentSerializer.Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf buf) {
        ConfigProjectsInt config = properties.config;
        buf.writeString(config.getName());
        String validRange = config.getValidRangeAsString();
        if (validRange != null) {
            buf.writeBoolean(true);
            buf.writeString(validRange);
        }else buf.writeBoolean(false);
        buf.writeEnumConstant(config.getConfigType());
        buf.writeCollection(List.of(config.getSuggestList()), PacketByteBuf::writeString);
    }

    @Override
    public Properties fromPacket(PacketByteBuf buf) {
        String name = buf.readString();
        String validRange = buf.readBoolean() ? buf.readString() : null;
        ConfigType configType = buf.readEnumConstant(ConfigType.class);
        String[] suggests = buf.readList(PacketByteBuf::readString).toArray(new String[0]);
        ConfigProjectsInt clientConfig = new ConfigProjectsInt() {
            @Override public String getName() { return name; }
            @Override public ConfigType getConfigType() { return configType; }
            @Override public ConfigStorageType getStorageType() { return null; }
            @Override public Object getDefaultValue() { return null; }
            @Override public @Nullable String getValidRangeAsString() { return validRange; }
            @Override public boolean shouldUpdateCommandTree() { return false; }
            @Override public String[] getSuggestList() { return suggests; }
            @Override public @Nullable ConfigPostProcessing getPostProcessing() {return null;}
        };
        return new Properties(clientConfig);
    }

    @Override
    public void writeJson(Properties properties, JsonObject json) {
        json.addProperty("config_name", properties.config.getName());
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
