package io.github.dragon826307.draconictech.api.command;

import java.lang.annotation.*;
import java.util.function.Supplier;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Requirement {
    Class<? extends Supplier<Boolean>> predicate();
}
