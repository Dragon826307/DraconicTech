package dragon826307.dt.mixin.tick;

import dragon826307.dt.project.microtick.WorldTickManager;
import dragon826307.dt.project.microtick.WorldTickingFlags;
import net.minecraft.server.function.CommandFunctionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandFunctionManager.class)
public class CommandFunctionManagerMixin {
    @Inject(method = "tick",at = @At("HEAD"),cancellable = true)
    private static void onTick(CallbackInfo ci) {
        if(!WorldTickManager.getWorldTickFlag(WorldTickingFlags.COMMAND_FUNCTION)) ci.cancel();
    }
}
