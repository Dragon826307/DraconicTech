package dragon826307.dt.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record Mod$CheckingModS2CPacket() implements CustomPayload {
    public static final CustomPayload.Id<Mod$CheckingModS2CPacket> ID = new CustomPayload.Id<>(ModNetworkHandler.S2C_CHECKING_ID);
    public static final PacketCodec<PacketByteBuf, Mod$CheckingModS2CPacket> CODEC = PacketCodec.unit(new Mod$CheckingModS2CPacket());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
