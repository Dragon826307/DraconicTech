package io.github.dragon826307.draconictech.client.mixin;

import io.github.dragon826307.draconictech.client.render.RenderManager;
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
