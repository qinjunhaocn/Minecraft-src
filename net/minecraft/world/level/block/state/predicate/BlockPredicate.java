/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate
implements Predicate<BlockState> {
    private final Block block;

    public BlockPredicate(Block $$0) {
        this.block = $$0;
    }

    public static BlockPredicate forBlock(Block $$0) {
        return new BlockPredicate($$0);
    }

    @Override
    public boolean test(@Nullable BlockState $$0) {
        return $$0 != null && $$0.is(this.block);
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return this.test((BlockState)object);
    }
}

