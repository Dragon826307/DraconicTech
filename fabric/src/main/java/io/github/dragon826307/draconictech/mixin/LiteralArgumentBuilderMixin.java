package io.github.dragon826307.draconictech.mixin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandBuilder;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LiteralArgumentBuilder.class)
public class LiteralArgumentBuilderMixin {
    @Inject(method = "build()Lcom/mojang/brigadier/tree/LiteralCommandNode;", at = @At(value = "RETURN"))
    private void onBuild(CallbackInfoReturnable<LiteralCommandNode<ServerCommandSource>> cir) {
        ((InstantCommandNode) cir.getReturnValue()).draconictech$setFlags(((InstantCommandBuilder) this).draconictech$getFlags());
    }
}
