package io.github.dragon826307.draconictech.client.config;

import io.github.dragon826307.draconictech.api.auto_init.AutoInitialize;
import io.github.dragon826307.draconictech.api.auto_init.InitializePhase;
import io.github.dragon826307.draconictech.api.auto_init.Location;
import io.github.dragon826307.draconictech.api.config.ConfigBuildHelper;
import io.github.dragon826307.draconictech.api.config.ConfigBuilder;
import io.github.dragon826307.draconictech.api.config.ConfigProject;

public final class ClientConfigProjects {
    @AutoInitialize(phase = InitializePhase.ON_MOD_INIT_CLIENT,priority = @Location(provideTo = ClientConfigProjectManager.class))
    private static void init(){}
    public static final ConfigProject.Client<Boolean> ALLOW_ILLEGAL_CHAT_CHARACTER = ConfigProject.Client.register(new ConfigProject.Client<>(ConfigBuilder.CommonConfigBuilder.create("EnhancedChat:allow_illegal_character", Boolean.class, false, (value, invalidReason) -> true, ConfigBuildHelper.BOOLEAN_PARSER).setSuggestionsSupplier(ConfigBuildHelper.BOOLEAN_SUGGESTIONS)));
}
