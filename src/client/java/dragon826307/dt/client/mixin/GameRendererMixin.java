package dragon826307.dt.client.mixin;

import dragon826307.dt.client.util.render.RenderManager;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "close",at = @At("RETURN"))
    public void close(CallbackInfo ci) {
        RenderManager.close();
    }
}
