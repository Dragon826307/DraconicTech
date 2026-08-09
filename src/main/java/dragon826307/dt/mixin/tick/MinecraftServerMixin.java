package dragon826307.dt.mixin.tick;

import dragon826307.dt.features.microtick.MicroTickManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(value = MinecraftServer.class,priority = 999)
public class MinecraftServerMixin {
    //TODO : 堆积数据包处理 & keepAliveC2S包处理
    @Inject(method = "tick",at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MicroTickManager.INSTANCE.getTickFrozenLevel() != 0) {
            MicroTickManager.INSTANCE.tryFreeze();
        }
    }
    @Inject(method = "tick",at = @At("TAIL"))
    private void onTickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        MicroTickManager.INSTANCE.onEndTick();
    }
}
