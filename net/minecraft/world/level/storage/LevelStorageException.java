/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.storage;

import net.minecraft.network.chat.Component;

public class LevelStorageException
extends RuntimeException {
    private final Component messageComponent;

    public LevelStorageException(Component $$0) {
        super($$0.getString());
        this.messageComponent = $$0;
    }

    public Component getMessageComponent() {
        return this.messageComponent;
    }
}

