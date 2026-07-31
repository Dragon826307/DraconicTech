package dragon826307.dt.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.ServerTickManager;

public final class ObjectCreatedEvents {
    private ObjectCreatedEvents() {}
    public static final Event<CreatedEvents> SERVER_TICK_MANAGER = EventFactory.createArrayBacked(CreatedEvents.class, listeners -> serverTickManager -> {for (CreatedEvents listener : listeners) listener.onCreated(serverTickManager);});
    @FunctionalInterface
    public interface CreatedEvents {
        void onCreated(ServerTickManager serverTickManager);
    }
}
