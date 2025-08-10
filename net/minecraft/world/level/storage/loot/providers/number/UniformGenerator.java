/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record UniformGenerator(NumberProvider min, NumberProvider max) implements NumberProvider
{
    public static final MapCodec<UniformGenerator> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)NumberProviders.CODEC.fieldOf("min").forGetter(UniformGenerator::min), (App)NumberProviders.CODEC.fieldOf("max").forGetter(UniformGenerator::max)).apply((Applicative)$$0, UniformGenerator::new));

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.UNIFORM;
    }

    public static UniformGenerator between(float $$0, float $$1) {
        return new UniformGenerator(ConstantValue.exactly($$0), ConstantValue.exactly($$1));
    }

    @Override
    public int getInt(LootContext $$0) {
        return Mth.nextInt($$0.getRandom(), this.min.getInt($$0), this.max.getInt($$0));
    }

    @Override
    public float getFloat(LootContext $$0) {
        return Mth.nextFloat($$0.getRandom(), this.min.getFloat($$0), this.max.getFloat($$0));
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Sets.union(this.min.getReferencedContextParams(), this.max.getReferencedContextParams());
    }
}

