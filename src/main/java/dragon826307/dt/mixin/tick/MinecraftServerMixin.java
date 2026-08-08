package dragon826307.dt.mixin.tick;

import dragon826307.dt.features.microtick.MicroTickManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    //TODO : 看门狗重置 & 堆积数据包处理 & keepAliveC2S包处理
    @Inject(method = "tick",at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MicroTickManager.INSTANCE.getTickFrozenLevel() != 0) {
            MicroTickManager.INSTANCE.tryFreeze();
        }
    }
}
