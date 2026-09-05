package io.github.dragon826307.draconictech;

import net.minecraft.server.network.ServerPlayerEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerRecorder {
    private static final Set<UUID> PLAYERS_WITH_MOD = new HashSet<>();
    public static boolean isPlayerWithMod(UUID uuid) {
        return PLAYERS_WITH_MOD.contains(uuid);
    }
    /**若{@code ServerPlayerEntity}为{@code null}则默认执行者为控制台或命令方块
     * <p>
     * 否则，检索{@link PlayerRecorder#PLAYERS_WITH_MOD }中是否包含当前玩家uuid
     * @return {@code true}如果包含玩家uuid或{@code player}为null
     */
    public static boolean isPlayerWithMod(@Nullable ServerPlayerEntity player) {
        if (player != null) {
            return PLAYERS_WITH_MOD.contains(player.getUuid());
        }else return true;
    }
    public static boolean addPlayer(UUID uuid) {
        if (!PLAYERS_WITH_MOD.contains(uuid)) {
            PLAYERS_WITH_MOD.add(uuid);
            return true;
        }
        return false;
    }
    public static boolean removePlayer(UUID uuid) {
        if (PLAYERS_WITH_MOD.contains(uuid)) {
            PLAYERS_WITH_MOD.remove(uuid);
            return true;
        }
        return false;
    }
}
