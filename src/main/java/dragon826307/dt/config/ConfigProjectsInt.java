package dragon826307.dt.config;

public interface ConfigProjectsInt {
    String getName();
    ConfigType getConfigType();
    ConfigStorageType getStorageType();
    Object getDefaultValue();
    String getValidRangeAsString();
    boolean shouldUpdateCommandTree();
    String[] getSuggestList();
}
