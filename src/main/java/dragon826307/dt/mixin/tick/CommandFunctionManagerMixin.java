package dragon826307.dt.mixin.tick;

import net.minecraft.server.function.CommandFunctionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandFunctionManager.class)
public class CommandFunctionManagerMixin {
    @Inject(method = "tick",at = @At("HEAD"),cancellable = true)
    private void onTick(CallbackInfo ci) {

    }
}
