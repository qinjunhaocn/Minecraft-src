/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class AlwaysTrueTest
extends RuleTest {
    public static final MapCodec<AlwaysTrueTest> CODEC = MapCodec.unit(() -> INSTANCE);
    public static final AlwaysTrueTest INSTANCE = new AlwaysTrueTest();

    private AlwaysTrueTest() {
    }

    @Override
    public boolean test(BlockState $$0, RandomSource $$1) {
        return true;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.ALWAYS_TRUE_TEST;
    }
}

