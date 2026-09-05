package dragon826307.dt;

import dragon826307.dt.server.DraconicTechServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public enum InitializePhase {
    /**
     * 在{@code  DraconicTechClient}初始化时触发，无上下文
     * <p>
     *     备注：只应在{@code client}包中声明
     * </p>
     */
    ON_MOD_INIT_CLIENT,
    /**
     * 在{@link DraconicTechServer}初始化时触发，无上下文
     */
    ON_MOD_INIT_DEDICATED_SERVER,
    /**
     * 在{@link DraconicTech}初始化时触发，无上下文
     * <p>
     *     备注：只应在{@code main}包中声明
     * </p>
     */
    ON_MOD_INIT_MAIN,
    /**
     * 在{@link ServerLifecycleEvents#SERVER_STARTING}被调用时触发，上下文：{@link MinecraftServer}
     */
    ON_SERVER_STARTING,
    /**
     * 在{@link ServerLifecycleEvents#SERVER_STARTED}被调用时触发，上下文：{@link MinecraftServer}
     */
    ON_SERVER_STARTED,
    /**
     * 在{@code ClientLifecycleEvents.CLIENT_STARTED}被调用时触发，上下文：{@code MinecraftClient}
     */
    ON_CLIENT_STARTED,
}