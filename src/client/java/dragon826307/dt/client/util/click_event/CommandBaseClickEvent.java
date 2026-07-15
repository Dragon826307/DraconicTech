package dragon826307.dt.client.util.click_event;

import dragon826307.dt.DraconicTech;
import dragon826307.dt.client.DraconicTechClient;
import dragon826307.dt.client.util.ClientChatHudHelper;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;

public final class CommandBaseClickEvent {
    public static final String PREFIX = RandomStringUtils.secure().next(16,true,true);
    private static final Map<String, ClickEvent> TASK_EVENTS = new HashMap<>();
    public static net.minecraft.text.ClickEvent run(ClickEvent event) {
        String id = RandomStringUtils.secure().next(48,true,true);
        TASK_EVENTS.put(id, event);
        return new net.minecraft.text.ClickEvent.RunCommand(PREFIX + id);
    }
    public static void runWithID(String id) {
        if (DraconicTechClient.DEBUG) ClientChatHudHelper.sendDebugMessageInChat("Running CommandBaseClickEvent for ID: " + id);
        ClickEvent event = TASK_EVENTS.get(id);
        if (event != null) {
            event.onClick();
            TASK_EVENTS.remove(id);
        }else {
            DraconicTech.LOGGER.error("Unknown click event ID: {}", id);
        }
    }
    public static boolean isTaskEvent(String id) {
        return TASK_EVENTS.containsKey(id);
    }
}
