/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import net.minecraft.gametest.framework.GameTestException;
import net.minecraft.network.chat.Component;

public class GameTestAssertException
extends GameTestException {
    protected final Component message;
    protected final int tick;

    public GameTestAssertException(Component $$0, int $$1) {
        super($$0.getString());
        this.message = $$0;
        this.tick = $$1;
    }

    @Override
    public Component getDescription() {
        return Component.a("test.error.tick", this.message, this.tick);
    }

    @Override
    public String getMessage() {
        return this.getDescription().getString();
    }
}

