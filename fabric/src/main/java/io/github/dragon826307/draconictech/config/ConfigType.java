package io.github.dragon826307.draconictech.config;

public enum ConfigType{
    STRING(String.class),
    INT(Integer.class),
    BOOLEAN(Boolean.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    LONG(Long.class),
    CHAR(Character.class);
    private final Class<?> clazz;
    ConfigType(Class<?> clazz){
        this.clazz = clazz;
    }
    public Class<?> getClazz(){
        return this.clazz;
    }
}
