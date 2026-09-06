package io.github.dragon826307.draconictech.client.config;

import io.github.dragon826307.draconictech.config.ConfigProjects;
import io.github.dragon826307.draconictech.config.ConfigType;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;

public final class ClientConfigProjects {
    public static final ConfigProjects.Client ALLOW_ILLEGAL_CHAT_CHARACTER = ConfigProjects.Client.register(new ConfigProjects.Client("EnhancedChat:allow_illegal_character", ConfigType.BOOLEAN, false, null, false, null, null));

    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT,priority = Integer.MIN_VALUE)
    private static void init(){}
}
