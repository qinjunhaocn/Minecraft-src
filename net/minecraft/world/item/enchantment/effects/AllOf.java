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
package net.minecraft.world.item.enchantment.effects;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.phys.Vec3;

public interface AllOf {
    public static <T, A extends T> MapCodec<A> codec(Codec<T> $$0, Function<List<T>, A> $$1, Function<A, List<T>> $$2) {
        return RecordCodecBuilder.mapCodec($$3 -> $$3.group((App)$$0.listOf().fieldOf("effects").forGetter($$2)).apply((Applicative)$$3, $$1));
    }

    public static EntityEffects a(EnchantmentEntityEffect ... $$0) {
        return new EntityEffects(List.of((Object[])$$0));
    }

    public static LocationBasedEffects a(EnchantmentLocationBasedEffect ... $$0) {
        return new LocationBasedEffects(List.of((Object[])$$0));
    }

    public static ValueEffects a(EnchantmentValueEffect ... $$0) {
        return new ValueEffects(List.of((Object[])$$0));
    }

    public record EntityEffects(List<EnchantmentEntityEffect> effects) implements EnchantmentEntityEffect
    {
        public static final MapCodec<EntityEffects> CODEC = AllOf.codec(EnchantmentEntityEffect.CODEC, EntityEffects::new, EntityEffects::effects);

        @Override
        public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
            for (EnchantmentEntityEffect $$5 : this.effects) {
                $$5.apply($$0, $$1, $$2, $$3, $$4);
            }
        }

        public MapCodec<EntityEffects> codec() {
            return CODEC;
        }
    }

    public record LocationBasedEffects(List<EnchantmentLocationBasedEffect> effects) implements EnchantmentLocationBasedEffect
    {
        public static final MapCodec<LocationBasedEffects> CODEC = AllOf.codec(EnchantmentLocationBasedEffect.CODEC, LocationBasedEffects::new, LocationBasedEffects::effects);

        @Override
        public void onChangedBlock(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4, boolean $$5) {
            for (EnchantmentLocationBasedEffect $$6 : this.effects) {
                $$6.onChangedBlock($$0, $$1, $$2, $$3, $$4, $$5);
            }
        }

        @Override
        public void onDeactivated(EnchantedItemInUse $$0, Entity $$1, Vec3 $$2, int $$3) {
            for (EnchantmentLocationBasedEffect $$4 : this.effects) {
                $$4.onDeactivated($$0, $$1, $$2, $$3);
            }
        }

        public MapCodec<LocationBasedEffects> codec() {
            return CODEC;
        }
    }

    public record ValueEffects(List<EnchantmentValueEffect> effects) implements EnchantmentValueEffect
    {
        public static final MapCodec<ValueEffects> CODEC = AllOf.codec(EnchantmentValueEffect.CODEC, ValueEffects::new, ValueEffects::effects);

        @Override
        public float process(int $$0, RandomSource $$1, float $$2) {
            for (EnchantmentValueEffect $$3 : this.effects) {
                $$2 = $$3.process($$0, $$1, $$2);
            }
            return $$2;
        }

        public MapCodec<ValueEffects> codec() {
            return CODEC;
        }
    }
}

