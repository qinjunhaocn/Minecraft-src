/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.resources;

import net.minecraft.server.packs.resources.ResourceManager;

public interface CloseableResourceManager
extends ResourceManager,
AutoCloseable {
    @Override
    public void close();
}

