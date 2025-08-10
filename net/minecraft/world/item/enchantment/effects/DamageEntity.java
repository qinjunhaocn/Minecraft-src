/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.enchantment.effects;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record DamageEntity(LevelBasedValue minDamage, LevelBasedValue maxDamage, Holder<DamageType> damageType) implements EnchantmentEntityEffect
{
    public static final MapCodec<DamageEntity> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)LevelBasedValue.CODEC.fieldOf("min_damage").forGetter(DamageEntity::minDamage), (App)LevelBasedValue.CODEC.fieldOf("max_damage").forGetter(DamageEntity::maxDamage), (App)DamageType.CODEC.fieldOf("damage_type").forGetter(DamageEntity::damageType)).apply((Applicative)$$0, DamageEntity::new));

    @Override
    public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
        float $$5 = Mth.randomBetween($$3.getRandom(), this.minDamage.calculate($$1), this.maxDamage.calculate($$1));
        $$3.hurtServer($$0, new DamageSource(this.damageType, $$2.owner()), $$5);
    }

    public MapCodec<DamageEntity> codec() {
        return CODEC;
    }
}

