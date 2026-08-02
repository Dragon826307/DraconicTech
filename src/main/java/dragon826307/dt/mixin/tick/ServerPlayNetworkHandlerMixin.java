package dragon826307.dt.mixin.tick;

import dragon826307.dt.features.microtick.MicroTickManager;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method = "onCommandExecution",at = @At("HEAD"),cancellable = true)
    private void onOnCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci){
        //TODO : 应使用更合理的解析
        if (packet.command().equals("draconictech features tickhalt 0") || packet.command().equals("dt features tickhalt 0")) {
            MicroTickManager.INSTANCE.unfreeze();
            ci.cancel();
        }
    }
}
