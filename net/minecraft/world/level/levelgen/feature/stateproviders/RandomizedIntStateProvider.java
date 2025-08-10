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
package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

public class RandomizedIntStateProvider
extends BlockStateProvider {
    public static final MapCodec<RandomizedIntStateProvider> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BlockStateProvider.CODEC.fieldOf("source").forGetter($$0 -> $$0.source), (App)Codec.STRING.fieldOf("property").forGetter($$0 -> $$0.propertyName), (App)IntProvider.CODEC.fieldOf("values").forGetter($$0 -> $$0.values)).apply((Applicative)$$02, RandomizedIntStateProvider::new));
    private final BlockStateProvider source;
    private final String propertyName;
    @Nullable
    private IntegerProperty property;
    private final IntProvider values;

    public RandomizedIntStateProvider(BlockStateProvider $$0, IntegerProperty $$1, IntProvider $$2) {
        this.source = $$0;
        this.property = $$1;
        this.propertyName = $$1.getName();
        this.values = $$2;
        List<Integer> $$3 = $$1.getPossibleValues();
        for (int $$4 = $$2.getMinValue(); $$4 <= $$2.getMaxValue(); ++$$4) {
            if ($$3.contains($$4)) continue;
            throw new IllegalArgumentException("Property value out of range: " + $$1.getName() + ": " + $$4);
        }
    }

    public RandomizedIntStateProvider(BlockStateProvider $$0, String $$1, IntProvider $$2) {
        this.source = $$0;
        this.propertyName = $$1;
        this.values = $$2;
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return BlockStateProviderType.RANDOMIZED_INT_STATE_PROVIDER;
    }

    @Override
    public BlockState getState(RandomSource $$0, BlockPos $$1) {
        BlockState $$2 = this.source.getState($$0, $$1);
        if (this.property == null || !$$2.hasProperty(this.property)) {
            IntegerProperty $$3 = RandomizedIntStateProvider.findProperty($$2, this.propertyName);
            if ($$3 == null) {
                return $$2;
            }
            this.property = $$3;
        }
        return (BlockState)$$2.setValue(this.property, this.values.sample($$0));
    }

    @Nullable
    private static IntegerProperty findProperty(BlockState $$02, String $$12) {
        Collection<Property<?>> $$2 = $$02.getProperties();
        Optional<IntegerProperty> $$3 = $$2.stream().filter($$1 -> $$1.getName().equals($$12)).filter($$0 -> $$0 instanceof IntegerProperty).map($$0 -> (IntegerProperty)$$0).findAny();
        return $$3.orElse(null);
    }
}

