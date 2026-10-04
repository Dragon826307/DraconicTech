package io.github.dragon826307.draconictech.client.config;

import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.auto_init.Location;
import io.github.dragon826307.draconictech.config.ConfigBuildHelper;
import io.github.dragon826307.draconictech.config.ConfigInfo;
import io.github.dragon826307.draconictech.config.ConfigProject;

public final class ClientConfigProjects {
    public static final ConfigProject.Client<Boolean> ALLOW_ILLEGAL_CHAT_CHARACTER = ConfigProject.Client.register(new ConfigProject.Client<>(new ConfigInfo("EnhancedChat:allow_illegal_character"), Boolean.class, false, null, ConfigBuildHelper.BOOLEAN_PARSER, null, () -> ConfigBuildHelper.BOOLEAN_SUGGESTIONS, null));

    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT,priority = @Location(provideTo = ClientConfigProjectManager.class))
    private static void init(){}
}
