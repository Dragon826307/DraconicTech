package dragon826307.dt.mixin.tick;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.ServerNetworkIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerNetworkIo.class)
public class ServerNetworkIoMixin {
    @WrapOperation(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;tick()V"))
    public void tickNetworkIo(ClientConnection instance, Operation<Void> original) {
        original.call(instance);
    }
}
