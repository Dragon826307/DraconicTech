package dragon826307.dt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当该模组作为库使用时，如要使用{@code @AutoInitialize}注解，
 * 则必须在{@code fabric.mod.json}里声明需要进行初始化的包名
 * <p>
 *     示例：
 *     <pre>
 *     "custom": {
 *         "draconictech:auto_init": ["(需要进行扫描的包名)"]
 *     }
 *     </pre>
 * </p>
 * <p>
 *     例如这段代码表示需要扫描该模组下的{@code dragon826307.dt}包
 *     <pre>
 *     "custom": {
 *         "draconictech:auto_init": ["dragon826307.dt"]
 *     }
 *     </pre>
 * </p>
 * 需要通过{@code phase}指定初始化阶段，类型为{@link InitializePhase}，可以在初始化方法内获取指定的上下文参数，具体可看{@link InitializePhase}，
 * {@code priority}为优先级（选填），在同一触发阶段内，值越低的越优先执行，默认值为{@code 1000}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoInitialize{
    /**
     * 需要提供注册阶段的枚举值
     * @see InitializePhase
     */
    InitializePhase phase();

    /**
     * 默认值{@code 1000}
     */
    int priority() default 1000;
}