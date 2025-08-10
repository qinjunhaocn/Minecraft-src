/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestInstance;

@FunctionalInterface
public interface TestInstanceFinder {
    public Stream<Holder.Reference<GameTestInstance>> findTests();
}

