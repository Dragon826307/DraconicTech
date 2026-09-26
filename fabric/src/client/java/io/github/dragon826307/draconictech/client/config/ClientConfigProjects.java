package io.github.dragon826307.draconictech.client.config;

import io.github.dragon826307.draconictech.config.ConfigBuildHelper;
import io.github.dragon826307.draconictech.config.ConfigProject;
import io.github.dragon826307.draconictech.util.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.util.auto_init.InitializePhase;

public final class ClientConfigProjects {
    public static final ConfigProject.Client<Boolean> ALLOW_ILLEGAL_CHAT_CHARACTER = ConfigProject.Client.register(new ConfigProject.Client<>("EnhancedChat:allow_illegal_character", Boolean.class, false, null, ConfigBuildHelper.BOOLEAN_PARSER, null, () -> ConfigBuildHelper.BOOLEAN_SUGGESTIONS, null));

    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT,priority = Integer.MIN_VALUE)
    private static void init(){}
}
