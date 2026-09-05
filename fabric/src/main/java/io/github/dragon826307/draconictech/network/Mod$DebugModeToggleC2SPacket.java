package io.github.dragon826307.draconictech.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record Mod$DebugModeToggleC2SPacket() implements CustomPayload {
    public static final CustomPayload.Id<Mod$DebugModeToggleC2SPacket> ID = new CustomPayload.Id<>(ModNetworkHandler.DEBUG_ID);
    public static final PacketCodec<PacketByteBuf, Mod$DebugModeToggleC2SPacket> CODEC = PacketCodec.unit(new Mod$DebugModeToggleC2SPacket());
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
