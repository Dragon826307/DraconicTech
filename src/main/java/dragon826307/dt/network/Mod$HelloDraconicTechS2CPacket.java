package dragon826307.dt.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record Mod$HelloDraconicTechS2CPacket() implements CustomPayload {
    public static final CustomPayload.Id<Mod$HelloDraconicTechS2CPacket> ID = new CustomPayload.Id<>(ModNetworkHandler.HELLO_ID);
    public static final PacketCodec<PacketByteBuf,Mod$HelloDraconicTechS2CPacket> CODEC = PacketCodec.unit(new Mod$HelloDraconicTechS2CPacket());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
