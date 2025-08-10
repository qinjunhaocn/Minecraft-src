/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;

public interface WorldGenLevel
extends ServerLevelAccessor {
    public long getSeed();

    default public boolean ensureCanWrite(BlockPos $$0) {
        return true;
    }

    default public void setCurrentlyGenerating(@Nullable Supplier<String> $$0) {
    }
}

