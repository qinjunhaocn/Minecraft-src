/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;

public record GameTestBatch(int index, Collection<GameTestInfo> gameTestInfos, Holder<TestEnvironmentDefinition> environment) {
    public GameTestBatch {
        if ($$1.isEmpty()) {
            throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
        }
    }
}

