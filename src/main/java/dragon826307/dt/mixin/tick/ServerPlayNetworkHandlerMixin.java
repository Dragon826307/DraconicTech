package dragon826307.dt.mixin.tick;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.command.TickHaltCommand;
import dragon826307.dt.features.microtick.MicroTickManager;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    protected abstract ParseResults<ServerCommandSource> parse(String command);

    @Inject(method = "onCommandExecution",at = @At("HEAD"),cancellable = true)
    private void onOnCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci){
        String[] args = packet.command().split(" ");
        if (args.length > 2 && (args[0].equals("dt") || args[0].equals(DraconicTech.MOD_ID)) && args[1].equals("features") && args[2].equals("tickhalt")) {
            try {
                int execute_value = ((ServerCommonNetworkHandlerAccessor) this).getServer().getCommandManager().getDispatcher().execute(parse(packet.command()));
                if (execute_value == Integer.MIN_VALUE) ci.cancel();
            } catch (CommandSyntaxException ignored) {}
        }
    }
}
