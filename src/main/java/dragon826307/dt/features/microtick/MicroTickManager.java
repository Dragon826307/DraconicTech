package dragon826307.dt.features.microtick;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.InitializePhase;
import dragon826307.dt.mixin.tick.ServerCommonNetworkHandlerAccessor;
import dragon826307.dt.util.SendMessageHelper;
import dragon826307.dt.util.ServerTranslationUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.profiler.Profilers;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.*;

public class MicroTickManager {
    private static final String FAIL_NULL = ServerTranslationUtil.getOrNull("dt.micro_tick.fail_null");
    private static final Text FAIL_NULL_T = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.fail_null"),true);
    private static final String PREPARE_FAIL = ServerTranslationUtil.getOrNull("dt.micro_tick.prepare_fail");
    private static final Text PREPARE_FAIL_T = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.prepare_fail"),true);
    private static final String ALREADY_HALT = ServerTranslationUtil.getOrNull("dt.micro_tick.already_halt");
    private static final Text ALREADY_HALT_T = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.already_halt"),true);

    public static @NonNull MicroTickManager INSTANCE = new MicroTickManager();

    private final ScheduledExecutorService MicroTickManagerThread = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "MicroTick-Manager-Thread");
        thread.setDaemon(true);
        return thread;
    });
    private @Nullable ServerCommandSource source;
    private @Nullable MinecraftServer server;
    private volatile CountDownLatch unfreezeLatch;
    private ScheduledFuture<?> keepAliveTask;
    private boolean isFreeze = false;
    private int TICK_FLAGS = 0b1111_1111_1111_1111_1111_1111_1111_1000;
    private int tickFrozenLevel = 0;
    // level:
    // 0 -> normal
    // 1 -> global
    // 2 -> phase
    // 3 -> event
    // 4 -> update
    // 5 -> ???
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_MAIN)
    private static void init() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> DraconicTech.getMicroTickManager().tickFrozenLevel = 0);
    }
    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTING)
    private static void getServer(MinecraftServer server) {
        INSTANCE.server = server;
    }
    public boolean isFreeze() {
        return isFreeze;
    }
    public void setMicroTickFlag(int flag, boolean bl) {
        TICK_FLAGS = (TICK_FLAGS | flag) & (bl ? -1 : ~flag);
    }
    public boolean getMicroTickFlag(int flag) {
        return ((TICK_FLAGS & flag) ^ flag) == 0;
    }
    public int getMicroTickFlags() {
        return TICK_FLAGS;
    }
    public void setTickFrozenLevel(int lvl){
        if (lvl < 0 || lvl > 5) {
            DraconicTech.LOGGER.error("Invalid value for MicroTickManager.tickFrozenLevel: {}", lvl);
            return;
        }
        tickFrozenLevel = lvl;
        if (lvl == 0) TICK_FLAGS |= 4194048;
        else if (lvl == 1) TICK_FLAGS &= -4194304;
    }
    public int getTickFrozenLevel(){
        return tickFrozenLevel;
    }
    //游戏逻辑线程-指令
    public void setCommandSource(ServerCommandSource source) {
        this.source = source;
    }
    //游戏逻辑线程
    public void tryFreeze() {
        if (isFreeze) {
            sendFeedback(ALREADY_HALT_T);
            DraconicTech.LOGGER.warn(ALREADY_HALT);
            return;
        }
        if (server == null) {
            sendFeedback(FAIL_NULL_T);
            DraconicTech.LOGGER.warn(FAIL_NULL);
            return;
        }
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            Profilers.get().push("prepare");
            //MicroTickManagerThread 线程
            keepAliveTask = MicroTickManagerThread.scheduleAtFixedRate(() -> {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    ((ServerCommonNetworkHandlerAccessor)player.networkHandler).runBaseTick();
                }
            }, 5, 5, TimeUnit.SECONDS);
            Profilers.get().pop();
        }, MicroTickManagerThread);
        try {
            future.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            DraconicTech.LOGGER.warn(PREPARE_FAIL);
            sendFeedback(PREPARE_FAIL_T);
            return;
        } catch (Exception e) {
            DraconicTech.LOGGER.error(e.getMessage());
            sendFeedback(Text.literal(e.getMessage()).withColor(Colors.RED));
            return;
        }
        unfreezeLatch = new CountDownLatch(1);
        try {
            isFreeze = true;
            unfreezeLatch.await();
        } catch (InterruptedException e) {
            isFreeze = false;
            Thread.currentThread().interrupt();
        }
    }
    private void sendFeedback(Text text) {
        if (source != null) {
            source.sendFeedback(() -> text,true);
            source = null;
        }
    }

    //Netty Worker 线程
    public void unfreeze() {
        if (!isFreeze) {
            DraconicTech.LOGGER.warn("[MicroTickManager] The game logic thread is not halt");
            return;
        }
        if (keepAliveTask != null && !keepAliveTask.isCancelled()) {
            keepAliveTask.cancel(true);
        }
        if (unfreezeLatch != null) {
            unfreezeLatch.countDown();
        }
        setTickFrozenLevel(0);
        isFreeze = false;
    }
}

