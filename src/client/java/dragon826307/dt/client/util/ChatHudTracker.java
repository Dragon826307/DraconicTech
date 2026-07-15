package dragon826307.dt.client.util;

import dragon826307.dt.client.mixin.ChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatHudTracker {
    public static final ChatHudAccessor CHAT_HUB = ((ChatHudAccessor)MinecraftClient.getInstance().inGameHud.getChatHud());
    private static final List<ChatHudTracker> SET = new CopyOnWriteArrayList<>();
    private int chat_index = -1;
    private boolean is_active;
    private ChatHudTracker(){
        this.is_active = true;
        SET.add(this);
    }
    public static void add(){
        for(ChatHudTracker chatHudTracker : SET){
            chatHudTracker.chat_index++;
        }
    }
    public static ChatHudTracker AddChatTracker(MutableText text){
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text));
        return new ChatHudTracker();
    }
    public void done(){
        this.is_active = false;
        SET.remove(this);
    }
    public void modify(Text text){
        if (!this.is_active) return;
        MinecraftClient.getInstance().execute(() ->{
            if (chat_index >= 0 && chat_index < CHAT_HUB.getMessages().size()){
                CHAT_HUB.getMessages().set(chat_index, new ChatHudLine(MinecraftClient.getInstance().inGameHud.getTicks(), text, null, MessageIndicator.system()));
                CHAT_HUB.chatRefresh();
            }else {
                this.done();
            }
        });
    }
}
