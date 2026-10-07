package io.github.dragon826307.draconictech.api.config;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class ConfigBuilder {
    public static final class CommonConfigBuilder<T> extends AbstractConfigBuilder<T, CommonConfigBuilder<T>> {
        private CommonConfigBuilder(String ID, Class<T> type, T defaultValue, AbstractConfigType.ConfigValidator<T> configValidator, AbstractConfigType.StringParser<String, T> stringParser) {
            super(ID, type, defaultValue, configValidator, stringParser);
        }
        public static <T> CommonConfigBuilder<T> create(String ID, Class<T> type, T defaultValue, AbstractConfigType.ConfigValidator<T> configValidator, AbstractConfigType.StringParser<String,@Nullable T> stringParser) {
            return new CommonConfigBuilder<>(ID, type, defaultValue, configValidator, stringParser);
        }
    }
    public static final class ServerConfigBuilder<T> extends AbstractConfigBuilder<T, ServerConfigBuilder<T>> {
        private T singlePlayerValue;
        private ServerConfigBuilder(String ID, Class<T> type, T defaultValue, AbstractConfigType.ConfigValidator<T> configValidator, AbstractConfigType.StringParser<String, T> stringParser) {
            super(ID, type, defaultValue, configValidator, stringParser);
            this.singlePlayerValue = defaultValue;
        }
        public static <T> ServerConfigBuilder<T> create(String ID, Class<T> type, T defaultValue, AbstractConfigType.ConfigValidator<T> configValidator, AbstractConfigType.StringParser<String, T> stringParser) {
            return new ServerConfigBuilder<>(ID, type, defaultValue, configValidator, stringParser);
        }
        public ServerConfigBuilder<T> setSinglePlayerValue(T singlePlayerValue) {
            if(singlePlayerValue == null) return this;
            this.singlePlayerValue = singlePlayerValue;
            return this;
        }
        public T getSinglePlayerValue() {
            return singlePlayerValue;
        }
    }
}
