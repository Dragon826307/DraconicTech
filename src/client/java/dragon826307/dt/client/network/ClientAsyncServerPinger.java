package dragon826307.dt.client.network;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.DraconicTech;
import dragon826307.dt.InitializePhase;
import dragon826307.dt.client.util.ChatHudTracker;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ClientAsyncServerPinger {
    private static final Text INVALID = Text.translatable("draconictech.client_async_server_pinger.invalid");
    private static final Text INFO_PING = Text.translatable("draconictech.client_async_server_pinger.info_ping");
    private static final Text INFO_VERSION = Text.translatable("draconictech.client_async_server_pinger.info_version");
    private static final Text INFO_PLAYER = Text.translatable("draconictech.client_async_server_pinger.info_player");
    private static final Text DNS_LATENCY = Text.translatable("draconictech.client_async_server_pinger.dns_latency");
    private static final Text TCP_LATENCY = Text.translatable("draconictech.client_async_server_pinger.tcp_latency");
    private static final Text UDP_LATENCY = Text.translatable("draconictech.client_async_server_pinger.udp_latency");
    private static final int TIMEOUT_MILLIS = 30000;
    private static final Set<ServerAddress> RUNNING_TASK = ConcurrentHashMap.newKeySet();
    private static ExecutorService executor = createExecutor();
    private static ExecutorService createExecutor(){
        return Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "ServerPinger Thread");
            t.setDaemon(true);
            return t;
        });
    }
    public static void ping(String address) {
        if (RUNNING_TASK.size() > 4){
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("draconictech.client_async_server_pinger.too_many_task").withColor(Colors.YELLOW));
            return;
        }
        if (!ServerAddress.isValid(address)) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.empty().append(INVALID).withColor(Colors.LIGHT_RED));
            DraconicTech.LOGGER.warn("Invalid address: {}", address);
            return;
        }
        ServerAddress serverAddress = ServerAddress.parse(address);
        if (RUNNING_TASK.contains(serverAddress)) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("draconictech.client_async_server_pinger.already_ping",address).withColor(Colors.YELLOW));
            DraconicTech.LOGGER.warn("Server address is already pinging: {}", address);
            return;
        }
        DraconicTech.LOGGER.info("Async Server Pinger Ping:{}", address);
        RUNNING_TASK.add(serverAddress);
        synchronized (ClientAsyncServerPinger.class) {
            if (executor.isShutdown()) return;
            executor.submit(() -> {
                ChatHudTracker tracker = ChatHudTracker.AddChatTracker(Text.empty().append(DraconicTech.MOD_PREFIX).append(Text.translatable("draconictech.client_async_server_pinger.prepare",address).withColor(Colors.GREEN)));
                AtomicBoolean isFinished = new AtomicBoolean(false);
                AtomicReference<ClientConnection> connectionRef = new AtomicReference<>(null);
                final long taskStartTime = System.currentTimeMillis();
                CompletableFuture.runAsync(() -> {
                    try {
                        final long[] DNS_Latency = {Util.getMeasuringTimeMs()};
                        Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(serverAddress).map(Address::getInetSocketAddress);
                        DNS_Latency[0] = Util.getMeasuringTimeMs()-DNS_Latency[0];
                        if (optional.isEmpty()){
                            isFinished.set(true);
                            tracker.modify(Text.empty().append(DraconicTech.MOD_PREFIX).append(Text.translatable("draconictech.client_async_server_pinger.unknow_host",address).withColor(Colors.RED)));
                            return;
                        }
                        InetSocketAddress inetSocketAddress = optional.get();
                        final long[] TCP_Latency = {Util.getMeasuringTimeMs()};
                        ClientConnection clientConnection = ClientConnection.connect(inetSocketAddress,false, (MultiValueDebugSampleLogImpl) null);
                        TCP_Latency[0] = Util.getMeasuringTimeMs()- TCP_Latency[0];
                        clientConnection.connect(serverAddress.getAddress(), serverAddress.getPort(), new ClientQueryPacketListener() {
                            private QueryResponseS2CPacket queryResponseS2CPacket;
                            final long[] UDP_Latency = {-1};
                            @Override
                            public void onResponse(QueryResponseS2CPacket packet) {
                                this.queryResponseS2CPacket = packet;
                                UDP_Latency[0] = Util.getMeasuringTimeMs();
                                clientConnection.send(new QueryPingC2SPacket(UDP_Latency[0]));
                            }
                            @Override
                            public void onPingResult(PingResultS2CPacket packet) {
                                UDP_Latency[0] = Util.getMeasuringTimeMs() - UDP_Latency[0];
                                var version = queryResponseS2CPacket.metadata().version();
                                var player = queryResponseS2CPacket.metadata().players();
                                Text hoverInfo_ping = Text.empty().append(TCP_LATENCY).append(TCP_Latency[0] + "ms\n").append(UDP_LATENCY).append(UDP_Latency[0] + "ms\n").append(DNS_LATENCY).append(DNS_Latency[0] + "ms");
                                Text hoverInfo_version = Text.of("Version: " + (version.map(ServerMetadata.Version::gameVersion).orElse("Unknow")) + "\nProtocol: " + (version.map(ServerMetadata.Version::protocolVersion).orElse(0)));
                                MutableText hoverInfo_player = Text.empty();
                                if (player.isPresent()) for (PlayerConfigEntry entry : player.get().sample()) hoverInfo_player.append(entry.name() + "\n");
                                tracker.modify(Text.empty().append(DraconicTech.MOD_PREFIX).append(Text.translatable("draconictech.client_async_server_pinger.done",address).withColor(Colors.GREEN))
                                        .append("\n").append(this.queryResponseS2CPacket.metadata().description()).append("\n")
                                        .append(Text.empty().append(INFO_PING).append(String.valueOf(UDP_Latency[0])).append("ms").styled(style -> style.withHoverEvent(new HoverEvent.ShowText(hoverInfo_ping))).withColor(0x8CB3FF)).append("  ")
                                        .append(Text.empty().append(INFO_VERSION).styled(style -> style.withHoverEvent(new HoverEvent.ShowText(hoverInfo_version))).withColor(0x2CBAA8)).append("  ")
                                        .append(Text.empty().append(INFO_PLAYER).append(player.map(players -> players.online() + "/" + players.max()).orElse("-1")).styled(style -> style.withHoverEvent(new HoverEvent.ShowText(hoverInfo_player))).withColor(0x5555FF)));
                                clientConnection.disconnect(Text.empty());
                                isFinished.set(true);
                            }
                            @Override
                            public void onDisconnected(DisconnectionInfo info) {
                                if (!info.reason().getString().equals("multiplayer.status.finished")){
                                    tracker.modify(Text.empty().append(DraconicTech.MOD_PREFIX).append(Text.translatable("draconictech.client_async_server_pinger.disconnected",address).withColor(Colors.RED)));
                                }
                                isFinished.set(true);
                            }
                            @Override
                            public boolean isConnectionOpen() {
                                return clientConnection.isOpen();
                            }
                        });
                        clientConnection.send(QueryRequestC2SPacket.INSTANCE);
                        connectionRef.set(clientConnection);
                    } catch (Exception e) {
                        tracker.modify(Text.empty().append(DraconicTech.MOD_PREFIX).append(Text.translatable("draconictech.client_async_server_pinger.fails",address)).withColor(0xAA0000).append("\n").append(e.getMessage()));
                        DraconicTech.LOGGER.error("Unexpected error while pinging: {}", address, e);
                        isFinished.set(true);
                    }
                }, executor);
                char[] animFrames = {'|', '/', '-', '\\'};
                int frameIndex = 0;
                try {
                    while (!isFinished.get()) {
                        ClientConnection conn = connectionRef.get();
                        StringBuilder stringBuilder = new StringBuilder();
                        if (System.currentTimeMillis() - taskStartTime > TIMEOUT_MILLIS) {
                            tracker.modify(Text.empty().append(DraconicTech.MOD_PREFIX).append(Text.translatable("draconictech.client_async_server_pinger.timeout",address).append("(" + (System.currentTimeMillis()-taskStartTime) + "ms)").withColor(Colors.RED)));
                            if (conn != null && conn.isOpen()) {
                                conn.disconnect(Text.empty());
                            }
                            break;
                        }else {
                            stringBuilder.append("Ping:[").append(address).append("]\n").repeat(".",Math.min(24,frameIndex/4)).append(animFrames[frameIndex++%4]);
                            tracker.modify(Text.empty().append(DraconicTech.MOD_PREFIX).append(stringBuilder.toString()));
                            if (conn != null && conn.isOpen()) {
                                conn.tick();
                            }
                            //noinspection BusyWait
                            Thread.sleep(256);
                        }
                    }
                } catch (InterruptedException e) {
                    tracker.modify(Text.translatable("draconictech.client_async_server_pinger.interrupted",address).withColor(0xAA0000).append("\n").append(e.getMessage()));
                    DraconicTech.LOGGER.warn("Interrupted ping: {}", address);
                } finally {
                    tracker.done();
                    RUNNING_TASK.remove(serverAddress);
                }
            });
        }
    }
    public static synchronized void shutdownAndClear(){
        if (executor != null && executor.isShutdown()) {
            executor.shutdownNow();
        }
        executor = createExecutor();
        RUNNING_TASK.clear();
        DraconicTech.LOGGER.info("ServerPinger Thread Pool Shutdown And Clear All Task");
    }
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT)
    private static void auto(){
        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> shutdownAndClear());
    }
}