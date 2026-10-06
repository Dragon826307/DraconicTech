package io.github.dragon826307.draconictech.config;

import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;

public final class ConfigProject {
    public static final AbstractConfigType<Integer> NULL_CONFIG = Main.register(new Main<>(ConfigBuilder.CommonConfigBuilder.create("NULL", Integer.class, Integer.MIN_VALUE, ((value, invalidReason) -> true), (string, invalidReason) -> null)));
    public enum UpdateCommandTreeFlags {
        NOTHING,
        IF_SUCCESS,
        ALWAYS_TRUE,
    }
    public static final class Client<T> extends AbstractConfigType<T> {
        private static final List<Client<?>> REGISTRY = new ArrayList<>();
        public Client(ConfigBuilder.CommonConfigBuilder<T> builder) {
            super(builder);
        }
        public static <T> Client<T> register(Client<T> config) {
            REGISTRY.add(config);
            return config;
        }
        @SuppressWarnings("unchecked")
        public static Class<Client<?>> getClazz() {
            return (Class<Client<?>>) (Class<?>) Client.class;
        }
        public static Client<?>[] values() {
            return REGISTRY.toArray(new Client[0]);
        }
    }
    public static final class Main<T> extends AbstractConfigType<T> {
        private static final List<Main<?>> REGISTRY = new ArrayList<>();
        public Main(ConfigBuilder.CommonConfigBuilder<T> builder) {
            super(builder);
        }
        public static <T> Main<T> register(Main<T> config) {
            REGISTRY.add(config);
            return config;
        }
        public static Main<?>[] values() {
            return REGISTRY.toArray(new Main[0]);
        }
        @SuppressWarnings("unchecked")
        public static Class<Main<?>> getClazz() {
            return (Class<Main<?>>) (Object) Main.class;
        }
    }
    public static final class Server<T> extends AbstractConfigType<T> {
        private static final List<Server<?>> REGISTRY = new ArrayList<>();

        private final T singlePlayerValue;

        public Server(ConfigBuilder.ServerConfigBuilder<T> builder) {
            super(builder);
            this.singlePlayerValue = builder.getSinglePlayerValue();
        }

        public static <T> Server<T> register(Server<T> config) {
            REGISTRY.add(config);
            return config;
        }
        public static Server<?>[] values() {
            return REGISTRY.toArray(new Server[0]);
        }

        public static final Server<Integer> STATUS_COMMAND_PERMISSION = register(new Server<>(ConfigBuilder.ServerConfigBuilder.create("status_command_permission_requirement", Integer.class, 2, ConfigBuildHelper.INTEGER_VALIDATOR(0, 4), ((string, invalidReason) -> Ints.tryParse(string)))));

        public T getSinglePlayerValue() {
            return singlePlayerValue;
        }
        @SuppressWarnings("unchecked")
        public static Class<Server<?>> getClazz() {
            return (Class<Server<?>>) (Object) Server.class;
        }
    }
    public static final class Auto<T> extends AbstractConfigType<T> {
        private static final List<Auto<?>> REGISTRY = new ArrayList<>();

        public Auto(ConfigBuilder.CommonConfigBuilder<T> builder) {
            super(builder);
        }

        public static <T> Auto<T> register(Auto<T> config) {
            REGISTRY.add(config);
            return config;
        }
        public static Auto<?>[] values() {
            return REGISTRY.toArray(new Auto[0]);
        }
        @SuppressWarnings("unchecked")
        public static Class<Auto<?>> getClazz() {
            return (Class<Auto<?>>) (Object) Auto.class;
        }

    }
}