package dragon826307.dt.features.microtick;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.InitializePhase;
import dragon826307.dt.events.ObjectCreatedEvents;
import dragon826307.dt.util.SendMessageHelper;
import dragon826307.dt.util.ServerTranslationUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.*;

public class MicroTickManager {
    private static final String FAIL_NULL = ServerTranslationUtil.getOrNull("dt.micro_tick.fail_null");
    private static final Text FAIL_NULL_T = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.fail_null"),true);
    private static final String PREPARE_FAIL = ServerTranslationUtil.getOrNull("dt.micro_tick.prepare_fail");
    private static final Text PREPARE_FAIL_T = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.prepare_fail"),true);

    public static MicroTickManager INSTANCE;

    private final ScheduledExecutorService MicroTickManagerThread = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "MicroTick-Manager-Thread");
        thread.setDaemon(true);
        return thread;
    });
    private ServerCommandSource source = null;
    private final MinecraftServer server;
    private volatile CountDownLatch unfreezeLatch;
    private ScheduledFuture<?> keepAliveTask;
    private int TICK_FLAGS = -1;
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
        INSTANCE = new MicroTickManager(server);
    }
    public MicroTickManager(MinecraftServer server) {
        this.server = server;
    }
    public void setMicroTickFlag(int flag, boolean bl) {
        TICK_FLAGS = (TICK_FLAGS | flag) & (bl ? -1 : ~flag);
    }
    public boolean getMicroTickFlag(int flag) {
        return (TICK_FLAGS & flag) != 0;
    }
    public int getMicroTickFlags() {
        return TICK_FLAGS;
    }
    public void setTickFrozenLevel(int lvl){
        if (lvl < 0 || lvl > 5) {
            throw new IllegalArgumentException("Invalid value for MicroTickManager.tickFrozenLevel: " + lvl);
        }
        tickFrozenLevel = lvl;
        if (lvl == 0) TICK_FLAGS |= 16383;
        else if (lvl == 1) TICK_FLAGS &= -16384;
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
        if (server == null) {
            sendFeedback(FAIL_NULL_T);
            DraconicTech.LOGGER.warn(FAIL_NULL);
            return;
        }
        CompletableFuture<Void> prepareFuture = CompletableFuture.runAsync(() -> {
            //MicroTickManagerThread 线程
            keepAliveTask = MicroTickManagerThread.scheduleAtFixedRate(() -> {
                long currentTime = System.currentTimeMillis();
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    player.networkHandler.sendPacket(new KeepAliveS2CPacket(currentTime));
                }
            }, 0, 5, TimeUnit.SECONDS);
        }, MicroTickManagerThread);
        try {
            prepareFuture.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            prepareFuture.cancel(true);
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
            unfreezeLatch.await();
        } catch (InterruptedException e) {
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
        if (keepAliveTask != null && !keepAliveTask.isCancelled()) {
            keepAliveTask.cancel(true);
        }
        if (unfreezeLatch != null) {
            unfreezeLatch.countDown();
        }
    }
}

