package io.github.dragon826307.draconictech.mixin.tick;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TickCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TickCommand.class)
public class TickCommandMixin {
    @Inject(method = "executeFreeze",at = @At("RETURN"))
    private static void onExecuteFreeze(ServerCommandSource source, boolean frozen, CallbackInfoReturnable<Integer> cir) {

    }
}
