package io.github.dragon826307.draconictech.server.mixin;

import io.github.dragon826307.draconictech.features.microtick.MicroTickManager;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DedicatedServerWatchdog.class)
public class DedicatedServerWatchdogMixin {
    @ModifyVariable(method = "run",at = @At(value = "LOAD", ordinal = 0),ordinal = 0)
    private long onRun(long l){
        if (MicroTickManager.INSTANCE.isOnTickPostProcessing()) return Long.MAX_VALUE;
        return l;
    }
}
