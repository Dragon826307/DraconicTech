package io.github.dragon826307.draconictech.util.auto_init;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示该初始化方法应当在目标类指定的阶段之前执行。
 * 优先级将被自动推断为：目标类同阶段所有带有 {@link AutoInitialize} 的方法的最小 {@code  priority} 减 1。
 * 如果目标类没有同阶段的方法，推断基于默认值 (1000 - 1 = 999)。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProviderTo {
    Class<?> value();
}
