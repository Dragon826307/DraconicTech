package dragon826307.dt.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record Mod$HelloDraconicTechC2SPacket() implements CustomPayload {
    public static final CustomPayload.Id<Mod$HelloDraconicTechC2SPacket> ID = new CustomPayload.Id<>(ModNetworkHandler.HELLO_ID);
    public static final PacketCodec<PacketByteBuf,Mod$HelloDraconicTechC2SPacket> CODEC = PacketCodec.unit(new Mod$HelloDraconicTechC2SPacket());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
