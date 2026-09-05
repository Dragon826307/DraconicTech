package io.github.dragon826307.draconictech.client.mixin;

import io.github.dragon826307.draconictech.client.util.ClientChatHudHelper;
import io.github.dragon826307.draconictech.client.util.click_event.CommandBaseClickEvent;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class, priority = 999)
public class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lio/netty/channel/ChannelFutureListener;Z)V",at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
        //TODO : 需要拦截警告窗口
        if (packet instanceof CommandExecutionC2SPacket(String command) && command.startsWith(CommandBaseClickEvent.PREFIX)) {
            String cmd = command.substring(CommandBaseClickEvent.PREFIX.length());
            if (CommandBaseClickEvent.isTaskEvent(cmd)) {
                CommandBaseClickEvent.runWithID(cmd);
            }else {
                ClientChatHudHelper.addMessageInChat(Text.literal("Unknown task id: \n" + cmd).withColor(Colors.LIGHT_RED),true);
            }
            ci.cancel();
        }
    }
}
