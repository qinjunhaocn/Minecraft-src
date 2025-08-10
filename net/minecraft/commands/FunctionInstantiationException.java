/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.commands;

import net.minecraft.network.chat.Component;

public class FunctionInstantiationException
extends Exception {
    private final Component messageComponent;

    public FunctionInstantiationException(Component $$0) {
        super($$0.getString());
        this.messageComponent = $$0;
    }

    public Component messageComponent() {
        return this.messageComponent;
    }
}

