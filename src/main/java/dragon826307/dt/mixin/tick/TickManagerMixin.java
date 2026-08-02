package dragon826307.dt.mixin.tick;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TickManager.class)
public class TickManagerMixin {
    @Unique
    private static int frozenLevel = -1;
    @ModifyReturnValue(method = "shouldSkipTick",at = @At("RETURN"))
    private boolean shouldSkipEntity(boolean original, Entity entity) {
        return original;
    }
    @Inject(method = "step",at = @At("TAIL"))
    private void onStep(CallbackInfo ci) {

    }
}
