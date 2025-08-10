/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class AttachedToLogsDecorator
extends TreeDecorator {
    public static final MapCodec<AttachedToLogsDecorator> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").forGetter($$0 -> Float.valueOf($$0.probability)), (App)BlockStateProvider.CODEC.fieldOf("block_provider").forGetter($$0 -> $$0.blockProvider), (App)ExtraCodecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions").forGetter($$0 -> $$0.directions)).apply((Applicative)$$02, AttachedToLogsDecorator::new));
    private final float probability;
    private final BlockStateProvider blockProvider;
    private final List<Direction> directions;

    public AttachedToLogsDecorator(float $$0, BlockStateProvider $$1, List<Direction> $$2) {
        this.probability = $$0;
        this.blockProvider = $$1;
        this.directions = $$2;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        RandomSource $$1 = $$0.random();
        for (BlockPos $$2 : Util.shuffledCopy($$0.logs(), $$1)) {
            Direction $$3 = Util.getRandom(this.directions, $$1);
            BlockPos $$4 = $$2.relative($$3);
            if (!($$1.nextFloat() <= this.probability) || !$$0.isAir($$4)) continue;
            $$0.setBlock($$4, this.blockProvider.getState($$1, $$4));
        }
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.ATTACHED_TO_LOGS;
    }
}

