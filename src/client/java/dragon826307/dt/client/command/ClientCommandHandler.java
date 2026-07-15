package dragon826307.dt.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dragon826307.dt.DraconicTech;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.ArrayList;
import java.util.List;

public class ClientCommandHandler {
    public static final LiteralArgumentBuilder<FabricClientCommandSource> commandRoot = ClientCommandManager.literal(DraconicTech.MOD_ID).executes(context -> {
        DraconicTech.LOGGER.info("test");
        return 1;
    });
    private static final List<ClientCommandCallback> callbacks = new ArrayList<>();
    private static boolean initialized = false;

    /**
     * @param c {@link ClientCommandCallback}
     */
    private static void addCommandBranch(ClientCommandCallback c){
        callbacks.add(c);
    }
    public static void init(){
        if (!initialized){
            addCommandBranch(new PingCommand());
            addCommandBranch(new EnhancedChatCommand());
            addCommandBranch(new ClientConfigCommand());
            initialized = true;
        }
        for(ClientCommandCallback c : callbacks){
            commandRoot.then(c.addClientCommandBranch(ClientCommandManager.literal(c.setBranchName())));
        }
    }
}
