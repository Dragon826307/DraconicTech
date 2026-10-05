package io.github.dragon826307.draconictech.config;

import io.github.dragon826307.draconictech.platform.text.BuiltText;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ConfigInfo<T> {
    private final String ID;
    private BuiltText name;
    private BuiltText description;
    private String category;
    private ConfigParser<T> parser;
    @FunctionalInterface
    private interface ConfigParser<T> {
        String parseConfigToString(@Nullable T value);
    }
    public ConfigInfo(String ID){
        this.ID = ID;
    }
    ConfigInfo<T> setName(BuiltText name){
        this.name = name;
        return this;
    }
    ConfigInfo<T> setDescription(BuiltText description){
        this.description = description;
        return this;
    }
    /**
     * @param category 配置项类别，可通过{@code §}分隔字符串以实现多级分类
     */
    ConfigInfo<T> setCategory(String category){
        this.category = category;
        return this;
    }
    ConfigInfo<T> setConfigParser(ConfigParser<T> parser) {
        this.parser = parser;
        return this;
    }
    @Nullable
    BuiltText getName(){
        return this.name;
    }
    @Nullable
    BuiltText getDescription(){
        return this.description;
    }
    public String[] getCategory(){
        if (this.category == null) {
            return new String[0];
        } else if (!this.category.contains("§")) {
            return new String[]{this.category};
        } else {
            return this.category.split("§");
        }
    }
    public String getID(){
        return this.ID;
    }
    public String parseConfigToString(@NonNull AbstractConfigType<T> config){
        if (parser == null) return String.valueOf(ConfigProjectManager.getConfig(config));
        return parser.parseConfigToString(ConfigProjectManager.getConfig(config));
    }
}
