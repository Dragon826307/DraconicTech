package dragon826307.dt.client.config;

import dragon826307.dt.AutoInitialize;
import dragon826307.dt.InitializePhase;
import dragon826307.dt.config.ConfigProjects;
import dragon826307.dt.config.ConfigProjectsInt;
import dragon826307.dt.config.ConfigStorageType;
import dragon826307.dt.config.ConfigType;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ClientConfigProjects {
    public static final ConfigProjects.Client ALLOW_ILLEGAL_CHAT_CHARACTER = ConfigProjects.Client.register(new ConfigProjects.Client("EnhancedChat:allow_illegal_character", ConfigType.BOOLEAN, false, null, false, null, null));
}
