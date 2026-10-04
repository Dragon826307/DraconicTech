package io.github.dragon826307.draconictech.config;

import io.github.dragon826307.draconictech.platform.text.BuiltText;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ConfigInfo {
    private final String ID;
    private BuiltText name;
    private BuiltText description;
    private String category;
    public ConfigInfo(String ID){
        this.ID = ID;
    }
    ConfigInfo setName(BuiltText name){
        this.name = name;
        return this;
    }
    ConfigInfo setDescription(BuiltText description){
        this.description = description;
        return this;
    }

    /**
     * @param category 配置项类别，可通过{@code §}分隔字符串以实现多级分类
     */
    ConfigInfo setCategory(String category){
        this.category = category;
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
}
