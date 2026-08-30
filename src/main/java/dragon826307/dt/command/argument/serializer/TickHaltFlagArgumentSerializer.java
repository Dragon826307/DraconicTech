package dragon826307.dt.command.argument.serializer;

import com.google.gson.JsonObject;
import dragon826307.dt.command.argument.TickHaltFlagArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public final class TickHaltFlagArgumentSerializer implements ArgumentSerializer<TickHaltFlagArgumentType, TickHaltFlagArgumentSerializer.Properties>{
    @Override
    public void writePacket(Properties properties, PacketByteBuf buf) {
        buf.writeString(properties.string);
    }

    @Override
    public Properties fromPacket(PacketByteBuf buf) {
        return new Properties(buf.readString());
    }

    @Override
    public void writeJson(Properties properties, JsonObject json) {
        json.addProperty("micro_tick_flag", properties.string);
    }

    @Override
    public Properties getArgumentTypeProperties(TickHaltFlagArgumentType argumentType) {
        return new Properties(argumentType.flagName());
    }

    public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<TickHaltFlagArgumentType> {
        final String string;
        public Properties(String string) {
            this.string = string;
        }
        @Override
        public TickHaltFlagArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new TickHaltFlagArgumentType(string);
        }

        @Override
        public ArgumentSerializer<TickHaltFlagArgumentType, ?> getSerializer() {
            return TickHaltFlagArgumentSerializer.this;
        }
    }
}
