package io.github.dragon826307.draconictech.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import io.github.dragon826307.draconictech.DraconicTech;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Inject(method = "execute",at = @At(value = "INVOKE", target = "Ljava/lang/Exception;getMessage()Ljava/lang/String;", ordinal = 0))
    private void onExecute(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfo ci, @Local Exception exception) {
        DraconicTech.LOGGER.error(exception.getMessage());
    }
}
