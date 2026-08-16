package dragon826307.dt.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dragon826307.dt.features.microtick.MicroTickManager;
import dragon826307.dt.features.microtick.MicroTickingFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BooleanSupplier;

@Mixin(value = MinecraftServer.class,priority = 999)
public class MinecraftServerMixin {
    //TODO : 堆积数据包处理 & keepAliveC2S包处理
    @WrapOperation(method = "tickWorlds",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickNetworkIo()V"))
    private void onTickNetworkIo(MinecraftServer instance, Operation<Void> original) {
        if (MicroTickManager.INSTANCE.getTickFrozenLevel() == 1) {
            if (MicroTickManager.INSTANCE.getMicroTickFlag(MicroTickingFlags.ORIGIN_BEFORE_NU)) {
                MicroTickManager.INSTANCE.tryFreeze();
            }
        }
        original.call(instance);
    }
    @WrapOperation(method = "tickWorlds",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;updatePlayerLatency()V"))
    private void onUpdatePlayerLatency(PlayerManager instance, Operation<Void> original) {
        original.call(instance);
        if (MicroTickManager.INSTANCE.getTickFrozenLevel() == 1) {
            if (!MicroTickManager.INSTANCE.getMicroTickFlag(MicroTickingFlags.ORIGIN_BEFORE_NU)) {
                MicroTickManager.INSTANCE.tryFreeze();
            }
        }
    }
    @Inject(method = "tick",at = @At("TAIL"))
    private void onTickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        MicroTickManager.INSTANCE.onEndTick();
    }
}
