package io.github.dragon826307.draconictech.command.argument.serializer;

import com.google.gson.JsonObject;
import io.github.dragon826307.draconictech.command.argument.ConfigValueArgumentType;
import io.github.dragon826307.draconictech.config.AbstractConfigType;
import io.github.dragon826307.draconictech.config.ConfigProject;
import io.netty.util.internal.UnstableApi;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

//TODO
@UnstableApi
public final class ConfigValueArgumentSerializer implements ArgumentSerializer<ConfigValueArgumentType<?>, ConfigValueArgumentSerializer.Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf buf) {
        buf.writeString(properties.config.getID());
    }

    @Override
    public Properties fromPacket(PacketByteBuf buf) {
        return new Properties(ConfigProject.NULL_CONFIG);
    }

    @Override
    public void writeJson(Properties properties, JsonObject json) {
        json.addProperty("config_name", properties.config.getID());
    }

    @Override
    public Properties getArgumentTypeProperties(ConfigValueArgumentType<?> argumentType) {
        return new Properties(argumentType.config());
    }

    public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<ConfigValueArgumentType<?>> {
        final AbstractConfigType<?> config;
        public Properties(AbstractConfigType<?> config) {
            this.config = config;
        }
        @Override
        public ConfigValueArgumentType<?> createType(CommandRegistryAccess commandRegistryAccess) {
            return new ConfigValueArgumentType<>(config);
        }

        @Override
        public ArgumentSerializer<ConfigValueArgumentType<?>,?> getSerializer() {
            return ConfigValueArgumentSerializer.this;
        }
    }
}
