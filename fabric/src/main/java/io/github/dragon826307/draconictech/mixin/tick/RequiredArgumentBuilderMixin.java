package io.github.dragon826307.draconictech.mixin.tick;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandBuilder;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RequiredArgumentBuilder.class)
public class RequiredArgumentBuilderMixin {
    @Inject(method = "build()Lcom/mojang/brigadier/tree/ArgumentCommandNode;",at = @At(value = "RETURN"))
    private void onBuild(CallbackInfoReturnable<ArgumentCommandNode<ServerCommandSource, ?>> cir) {
        if (((InstantCommandBuilder) this).draconictech$isInstant()) {
            ((InstantCommandNode) cir.getReturnValue()).draconictech$setInstant(true);
        }
    }
}
