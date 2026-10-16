package io.github.dragon826307.draconictech.command;

import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.api.command.AbstractCommandRootNode;

public final class DraconicTechCommandRootNode extends AbstractCommandRootNode {
    public String root() {
        return DraconicTech.MOD_ID;
    }
}
