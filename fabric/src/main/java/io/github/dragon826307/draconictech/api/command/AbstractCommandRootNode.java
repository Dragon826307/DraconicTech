package io.github.dragon826307.draconictech.api.command;

public abstract class AbstractCommandRootNode<A extends AbstractCommandRootNode<A>> {
    @SuppressWarnings("unchecked")
    public A getSelf() {
        return (A) this;
    }
}
