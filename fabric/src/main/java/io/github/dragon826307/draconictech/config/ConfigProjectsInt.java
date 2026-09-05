package io.github.dragon826307.draconictech.config;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ConfigProjectsInt {
    String getName();
    ConfigType getConfigType();
    ConfigStorageType getStorageType();
    Object getDefaultValue();
    @Nullable
    String getValidRangeAsString();
    boolean shouldUpdateCommandTree();
    String[] getSuggestList();
    @Nullable
    Runnable getPostProcessing();
}
