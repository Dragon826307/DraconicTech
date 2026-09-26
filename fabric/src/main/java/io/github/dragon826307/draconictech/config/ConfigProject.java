package io.github.dragon826307.draconictech.config;

import com.google.common.primitives.Ints;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ConfigProject {
    public static final AbstractConfigType<Integer> NULL_CONFIG = Main.register(new Main<>("NULL", Integer.class,Integer.MIN_VALUE,null,b -> null,() -> false,ConfigBuildHelper.NULL_SUGGESTIONS_SUPPLIER,ConfigBuildHelper.NULL_RUNNABLE,UpdateCommandTreeFlags.NOTHING));
    public enum UpdateCommandTreeFlags {
        NOTHING,
        IF_SUCCESS,
        ALWAYS_TRUE,
    }
    public static final class Client<T> extends AbstractConfigType<T> {
        private static final List<Client<?>> REGISTRY = new ArrayList<>();

        public static <T> Client<T> register(Client<T> config) {
            REGISTRY.add(config);
            return config;
        }

        /**
         * @param ID 配置项ID，不可重复
         * @param defaultValue 默认值
         * @param configValidator 范围校验器，为{@code null}时则总是通过
         * @param parser 配置项解析器，定义如何将字符串解析为配置项所需类型，可通过返回{@code null}表示解析失败
         * @param shouldBuild 是否构建为指令，为{@code null}时总是构建
         * @param suggestionsSupplier 建议列表提供器，为{@code null}时则表示为空列表
         * @param postProcessor 后续处理器，定义修改完配置项后应该进行的操作，并且总是会执行。为{@code null}则表示不进行任何操作
         */
        public Client(String ID, @NonNull Class<T> type,@NonNull T defaultValue, @Nullable ConfigValidator<T> configValidator, @NonNull StringParser<String, T> parser, @Nullable BuildAsCommand shouldBuild,@Nullable Supplier<List<String>> suggestionsSupplier,@Nullable Runnable postProcessor) {
            super(ID, type, defaultValue, configValidator, parser, shouldBuild);
            super.setSuggestionsSupplier(suggestionsSupplier);
            super.setPostProcessor(postProcessor);
        }

        public static Client<?>[] values() {
            return REGISTRY.toArray(new Client[0]);
        }
    }
    public static final class Main<T> extends AbstractConfigType<T> {
        private static final List<Main<?>> REGISTRY = new ArrayList<>();

        /**
         * @param ID 配置项ID，不可重复
         * @param defaultValue 默认值
         * @param configValidator 范围校验器，为{@code null}时则总是通过
         * @param parser 配置项解析器，定义如何将字符串解析为配置项所需类型，可通过返回{@code null}表示解析失败
         * @param shouldBuild 是否构建为指令，为{@code null}时总是构建
         * @param suggestionsSupplier 建议列表提供器，为{@code null}时则表示为空列表
         * @param postProcessor 后续处理器，定义修改完配置项后应该进行的操作。为{@code null}则表示不进行任何操作
         * @param updateCommandTreeFlag 修改完配置项后是否调用后续处理器
         * @see UpdateCommandTreeFlags
         */
        public Main(String ID,@NonNull Class<T> type ,@NonNull T defaultValue, @Nullable ConfigValidator<T> configValidator, @NonNull StringParser<String, T> parser, @Nullable BuildAsCommand shouldBuild, Supplier<List<String>> suggestionsSupplier, Runnable postProcessor, UpdateCommandTreeFlags updateCommandTreeFlag) {
            super(ID,type , defaultValue, configValidator, parser, shouldBuild);
            super.setSuggestionsSupplier(suggestionsSupplier);
            super.setPostProcessor(postProcessor);
            super.setUpdateCommandTreeFlag(updateCommandTreeFlag);
        }


        public static <T> Main<T> register(Main<T> config) {
            REGISTRY.add(config);
            return config;
        }
        public static Main<?>[] values() {
            return REGISTRY.toArray(new Main[0]);
        }
        public static final Main<Integer> AUTO_SAVE_INTERVAL = register(new Main<>("ConfigProjectManager:auto_save_interval_seconds", Integer.class, 120, integer -> integer >= 30 && integer <= 14400 , Ints::tryParse,null, () -> List.of("60","120","300","1800"),null, UpdateCommandTreeFlags.NOTHING));
    }
    public static final class Server<T> extends AbstractConfigType<T> {
        private static final List<Server<?>> REGISTRY = new ArrayList<>();

        private final T singlePlayerValue;

        /**
         * @param ID 配置项ID，不可重复
         * @param defaultValue 默认值
         * @param configValidator 范围校验器，为{@code null}时则总是通过
         * @param parser 配置项解析器，定义如何将字符串解析为配置项所需类型，可通过返回{@code null}表示解析失败
         * @param shouldBuild 是否构建为指令，为{@code null}时总是构建
         * @param singlePlayerValue 单人游戏时使用的值，硬编码值，不可通过指令更改
         */
        public Server(String ID, @NonNull Class<T> type,@NonNull T defaultValue, @Nullable ConfigValidator<T> configValidator, @NonNull StringParser<String, T> parser, @Nullable BuildAsCommand shouldBuild, T singlePlayerValue) {
            super(ID, type, defaultValue, configValidator, parser, shouldBuild);
            this.singlePlayerValue = singlePlayerValue;
        }

        public static <T> Server<T> register(Server<T> config) {
            REGISTRY.add(config);
            return config;
        }
        public static Server<?>[] values() {
            return REGISTRY.toArray(new Server[0]);
        }
        public static final Server<Integer> STATUS_COMMAND_PERMISSION = register(new Server<>("status_command_permission_requirement", Integer.class, 2, ConfigBuildHelper.INTEGER_VALIDATOR(0,4), Ints::tryParse,null,4));
        public static final Server<Boolean> ALLOW_PLAYER_WITH_NO_MOD_CHANGE_CONFIG = register(new Server<>("allow_player_with_no_mod_change_config", Boolean.class, true, null, ConfigBuildHelper.BOOLEAN_PARSER,null,true));

        public T getSinglePlayerValue() {
            return singlePlayerValue;
        }
    }
    public static final class Auto<T> extends AbstractConfigType<T> {
        private static final List<Auto<?>> REGISTRY = new ArrayList<>();

        public static <T> Auto<T> register(Auto<T> config) {
            REGISTRY.add(config);
            return config;
        }
        public static Auto<?>[] values() {
            return REGISTRY.toArray(new Auto[0]);
        }
        public Auto(String ID, @NonNull Class<T> type, @NonNull T defaultValue, @Nullable ConfigValidator<T> configValidator, @NonNull StringParser<String, T> parser, @Nullable BuildAsCommand shouldBuild) {
            super(ID, type, defaultValue, configValidator, parser,shouldBuild);
        }
    }
}