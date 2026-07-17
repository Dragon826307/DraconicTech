package dragon826307.dt.mixin.server;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerCommonNetworkHandler.class,priority = 999)
public class ServerCommonNetworkHandlerMixin {
    @Inject(method = "send",at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, CallbackInfo ci) {
        if (packet instanceof CustomPayloadS2CPacket(CustomPayload payload)) {
            Identifier identifier = payload.getId().id();
        }
    }
}
