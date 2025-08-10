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
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HangingMossBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.apache.commons.lang3.mutable.MutableObject;

public class PaleMossDecorator
extends TreeDecorator {
    public static final MapCodec<PaleMossDecorator> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("leaves_probability").forGetter($$0 -> Float.valueOf($$0.leavesProbability)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("trunk_probability").forGetter($$0 -> Float.valueOf($$0.trunkProbability)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("ground_probability").forGetter($$0 -> Float.valueOf($$0.groundProbability))).apply((Applicative)$$02, PaleMossDecorator::new));
    private final float leavesProbability;
    private final float trunkProbability;
    private final float groundProbability;

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.PALE_MOSS;
    }

    public PaleMossDecorator(float $$0, float $$1, float $$2) {
        this.leavesProbability = $$0;
        this.trunkProbability = $$1;
        this.groundProbability = $$2;
    }

    @Override
    public void place(TreeDecorator.Context $$02) {
        RandomSource $$12 = $$02.random();
        WorldGenLevel $$22 = (WorldGenLevel)$$02.level();
        List<BlockPos> $$32 = Util.shuffledCopy($$02.logs(), $$12);
        if ($$32.isEmpty()) {
            return;
        }
        MutableObject<BlockPos> $$4 = new MutableObject<BlockPos>((BlockPos)$$32.getFirst());
        $$32.forEach($$1 -> {
            if ($$1.getY() < ((BlockPos)$$4.getValue()).getY()) {
                $$4.setValue((BlockPos)$$1);
            }
        });
        BlockPos $$5 = (BlockPos)$$4.getValue();
        if ($$12.nextFloat() < this.groundProbability) {
            $$22.registryAccess().lookup(Registries.CONFIGURED_FEATURE).flatMap($$0 -> $$0.get(VegetationFeatures.PALE_MOSS_PATCH)).ifPresent($$3 -> ((ConfiguredFeature)((Object)((Object)$$3.value()))).place($$22, $$22.getLevel().getChunkSource().getGenerator(), $$12, $$5.above()));
        }
        $$02.logs().forEach($$2 -> {
            BlockPos $$3;
            if ($$12.nextFloat() < this.trunkProbability && $$02.isAir($$3 = $$2.below())) {
                PaleMossDecorator.addMossHanger($$3, $$02);
            }
        });
        $$02.leaves().forEach($$2 -> {
            BlockPos $$3;
            if ($$12.nextFloat() < this.leavesProbability && $$02.isAir($$3 = $$2.below())) {
                PaleMossDecorator.addMossHanger($$3, $$02);
            }
        });
    }

    private static void addMossHanger(BlockPos $$0, TreeDecorator.Context $$1) {
        while ($$1.isAir($$0.below()) && !((double)$$1.random().nextFloat() < 0.5)) {
            $$1.setBlock($$0, (BlockState)Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, false));
            $$0 = $$0.below();
        }
        $$1.setBlock($$0, (BlockState)Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, true));
    }
}

