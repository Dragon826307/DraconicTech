package io.github.dragon826307.draconictech.api.auto_init;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示该初始化方法应当在目标类指定的同阶段初始化方法执行之前执行或定义一个整数类型的优先级值。
 * 优先级将被自动推断为：目标类同阶段所有带有 {@link AutoInitialize} 的方法的最小 {@code  priority} 减 1。
 * 如果目标类没有同阶段的方法，推断基于默认值 (1000 - 1 = 999)。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Location {
    /**
     * 声明当前初始化方法需要提供给的目标类（即当前方法将在目标类的所有初始化方法之前执行）。
     * <p>默认值为 {@code Void.class} 表示未设置。</p>
     */
    Class<?> provideTo() default Void.class;
    /**
     * 显式指定的优先级数值（数值越小越先执行）。
     * <p>默认值为 {@link Integer#MIN_VALUE} 表示未设置。</p>
     */
    int priority() default Integer.MAX_VALUE;
}
