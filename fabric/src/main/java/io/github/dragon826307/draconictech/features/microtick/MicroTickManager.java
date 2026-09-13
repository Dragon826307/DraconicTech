package io.github.dragon826307.draconictech.features.microtick;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.config.ConfigProjectManager;
import io.github.dragon826307.draconictech.config.ConfigProjects;
import io.github.dragon826307.draconictech.mixin.tick.ChunkHolderInvoker;
import io.github.dragon826307.draconictech.mixin.tick.EntityTrackerInvoker;
import io.github.dragon826307.draconictech.mixin.tick.ServerChunkLoadingManagerAccessor;
import io.github.dragon826307.draconictech.mixin.tick.ServerCommonNetworkHandlerAccessor;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
import io.github.dragon826307.draconictech.util.SendMessageHelper;
import io.github.dragon826307.draconictech.util.ServerTranslationUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class MicroTickManager {
    private static final String FAIL_NULL = ServerTranslationUtil.getOrNull("dt.micro_tick.fail_null");
    private static final Text FAIL_NULL_T = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.fail_null"),true);
    private static final String PREPARE_FAIL = ServerTranslationUtil.getOrNull("dt.micro_tick.prepare_fail");
    private static final Text PREPARE_FAIL_T = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.prepare_fail"),true);
    private static final String ALREADY_HALT = ServerTranslationUtil.getOrNull("dt.micro_tick.already_halt");
    private static final Text ALREADY_HALT_T = SendMessageHelper.getMessage(ServerTranslationUtil.getTranslatedWithFallback("dt.micro_tick.already_halt"),true);

    private static MicroTickManager INSTANCE;

    private final MinecraftServer server;
    private final ScheduledExecutorService microTickManagerThread = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    private Consumer<Text> feedback = text -> {};

    private volatile CountDownLatch unfreezeLatch;
    private boolean onTickPostProcessing = false;
    private boolean isFreeze = false;
    private int remainStep = 0;
    private int tick_flags = 0;
    private int frozen_lvl = 0;
    // level:
    // 000 -> normal
    // 001 -> global(before_nu)
    // 010 -> phase
    // 011 -> event
    // 100 -> update
    // 101 -> ???
    // 110 -> global(after_nu)
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_MAIN)
    private static void init() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            INSTANCE.setTickFrozenLevel(0);
            if (INSTANCE.isFreeze()) {
                INSTANCE.unfreeze();
            }
            INSTANCE.microTickManagerThread.close();
        });
    }

    @AutoInitialize(phase = InitializePhase.ON_SERVER_STARTED)
    private static void flagInit() {
        INSTANCE.checkConfig();
    }

    public static MicroTickManager getInstance() {
        return INSTANCE;
    }

    public MicroTickManager(MinecraftServer server) {
        this.server = server;
        CompletableFuture.runAsync(() -> microTickManagerThread.scheduleAtFixedRate(() -> {
            if (INSTANCE.isFreeze()) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    ((ServerCommonNetworkHandlerAccessor)player.networkHandler).runBaseTick();
                }
            }
        },10,10,TimeUnit.SECONDS));
        INSTANCE = this;
    }

    public MinecraftServer getServer() {return this.server;}

    public void onEndTick() {
        onTickPostProcessing = false;
    }

    public void checkConfig() {
        String config = ConfigProjectManager.getConfig(ConfigProjects.Main.GLOBAL_TICK_FREEZE_ORIGIN).asString();
        setMicroTickFlag(MicroTickingFlags.ORIGIN_BEFORE_NU, config.equals(ConfigProjects.Main.GLOBAL_TICK_FREEZE_ORIGIN.getDefaultValue()));
    }

    public boolean isFreeze() {
        return isFreeze;
    }

    public boolean isOnTickPostProcessing() {
        return onTickPostProcessing;
    }

    public void setMicroTickFlag(int flag, boolean bl) {
        tick_flags = (tick_flags | flag) & (bl ? -1 : ~flag);
    }

    public boolean getMicroTickFlag(int flag) {
        return (tick_flags & flag) == flag;
    }

    public void setTickFrozenLevel(int lvl){
        if (lvl < 0 || lvl > 5) {
            throw new IllegalArgumentException("Invalid value for MicroTickManager.frozen_lvl: " + lvl);
        }
        frozen_lvl = lvl;
    }

    public void step(int step) {
        if (step <= 0) {
            throw new IllegalArgumentException("Invalid value for step: " + step);
        }
        if (!isFreeze) {
            return;
        }
        isFreeze = false;
        remainStep = step - 1;
        unfreezeLatch.countDown();
    }

    public int getTickFrozenLevel(){
        return frozen_lvl;
    }
    //游戏逻辑线程-指令
    public void setCommandSource(ServerCommandSource source) {
        feedback = text -> source.sendFeedback(() -> text,true);
    }
    //游戏逻辑线程
    public void tryFreeze(Text t) {
//        MutableText text = (MutableText) t;
//        text.styled(style -> style.withHoverEvent(new HoverEvent.ShowText(text)));
        feedback.accept(t);
        tryFreeze();
    }

    public void tryFreeze() {
        if (remainStep > 0) {
            remainStep--;
            return;
        }
        if (isFreeze) {
            feedback.accept(ALREADY_HALT_T);
            DraconicTech.LOGGER.warn(ALREADY_HALT);
            return;
        }
        unfreezeLatch = new CountDownLatch(1);
        try {
            isFreeze = true;
            onTickPostProcessing = true;
            if (getTickFrozenLevel() > 1) {
                syncToClientImmediately();
            }
            unfreezeLatch.await();
        } catch (InterruptedException e) {
            isFreeze = false;
            Thread.currentThread().interrupt();
        }
    }

    private void syncToClientImmediately() {
        for (ServerWorld world : server.getWorlds()) {
            ServerChunkLoadingManager serverChunkLoadingManager = world.getChunkManager().chunkLoadingManager;
            Iterable<ChunkHolder> chunkHolders = ((ServerChunkLoadingManagerAccessor) serverChunkLoadingManager).getChunkHolders().values();
            for (ChunkHolder chunkHolder : chunkHolders) {
                if (chunkHolder != null) {
                    ((ChunkHolderInvoker) chunkHolder).flushAllUpdates(chunkHolder.getWorldChunk());
                }
            }
            var entityTrackers = ((ServerChunkLoadingManagerAccessor) serverChunkLoadingManager).getEntityTrackers();
            for (Object tracker : entityTrackers.values()) {
                if (tracker != null) {
                    ((EntityTrackerInvoker) tracker).updateAllTrackedStatus(server.getPlayerManager().getPlayerList());
                }
            }
        }
    }

    //Netty Worker 线程
    public void unfreeze() {
        if (!isFreeze) {
            DraconicTech.LOGGER.warn("[MicroTickManager] The game logic thread is not halt");
            return;
        }
        if (unfreezeLatch != null) {
            unfreezeLatch.countDown();
        }
        isFreeze = false;
        setTickFrozenLevel(0);
    }
}

