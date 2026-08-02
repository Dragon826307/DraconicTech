package dragon826307.dt.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dragon826307.dt.features.microtick.MicroTickManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
//    @WrapOperation(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerTickManager;step()V"))
//    private void onTick(ServerTickManager instance, Operation<Void> original){
//        if (MicroTickManager.INSTANCE.getTickFrozenLevel() != 0) {
//            MicroTickManager.INSTANCE.tryFreeze();
//        }
//        original.call(instance);
//    }
    @Inject(method = "tick",at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (MicroTickManager.INSTANCE.getTickFrozenLevel() != 0) {
            MicroTickManager.INSTANCE.tryFreeze();
        }
    }
}
