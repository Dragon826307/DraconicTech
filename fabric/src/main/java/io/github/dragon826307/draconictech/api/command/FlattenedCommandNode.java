package io.github.dragon826307.draconictech.api.command;

import java.util.HashMap;
import java.util.function.Supplier;

public record FlattenedCommandNode(
        String flattenedLiteralCommand,
        HashMap<String, AbstractCommandArgument<?>> argumentMap,
        Supplier<Boolean> requirement,
        Runnable executor
) {

}
