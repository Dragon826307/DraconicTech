package io.github.dragon826307.draconictech.mixin.tick;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.command.CommandFlags;
import io.github.dragon826307.draconictech.features.microtick.MicroTickManager;
import io.github.dragon826307.draconictech.features.microtick.mixin_int.InstantCommandNode;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    protected abstract ParseResults<ServerCommandSource> parse(String command);

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onCommandExecution",at = @At("HEAD"),cancellable = true)
    private void onOnCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci){
        MinecraftServer server = MicroTickManager.getInstance().getServer();
        ParseResults<ServerCommandSource> parseResults = this.parse(packet.command());
        List<ParsedCommandNode<ServerCommandSource>> nodes = parseResults.getContext().getNodes();
        if (!nodes.isEmpty()){
            CommandNode<ServerCommandSource> commandNode = nodes.getLast().getNode();
            if ((((InstantCommandNode) commandNode).draconictech$getFlags() & CommandFlags.INSTANT) == CommandFlags.INSTANT) {
                try {
                    server.getCommandManager().getDispatcher().execute(parseResults);
                } catch (Exception e) {
                    player.sendMessage(Text.literal(e.getMessage()).formatted(Formatting.RED));
                    if (!(e instanceof CommandSyntaxException)) {
                        DraconicTech.LOGGER.error("Error while executing command: '{}'",packet.command(),e);
                    }
                }
                ci.cancel();
            }
        }
    }
    @ModifyExpressionValue(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/TickManager;shouldTick()Z"))
    private boolean onOnPlayerMove(boolean original){
        return original && !MicroTickManager.getInstance().isOnTickPostProcessing();
    }
}
