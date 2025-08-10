/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.server.packs.repository;

import java.util.function.Consumer;
import net.minecraft.server.packs.repository.Pack;

@FunctionalInterface
public interface RepositorySource {
    public void loadPacks(Consumer<Pack> var1);
}

