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
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record ChangeItemDamage(LevelBasedValue amount) implements EnchantmentEntityEffect
{
    public static final MapCodec<ChangeItemDamage> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)LevelBasedValue.CODEC.fieldOf("amount").forGetter($$0 -> $$0.amount)).apply((Applicative)$$02, ChangeItemDamage::new));

    @Override
    public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
        ItemStack $$5 = $$2.itemStack();
        if ($$5.has(DataComponents.MAX_DAMAGE) && $$5.has(DataComponents.DAMAGE)) {
            ServerPlayer $$6;
            LivingEntity livingEntity = $$2.owner();
            ServerPlayer $$7 = livingEntity instanceof ServerPlayer ? ($$6 = (ServerPlayer)livingEntity) : null;
            int $$8 = (int)this.amount.calculate($$1);
            $$5.hurtAndBreak($$8, $$0, $$7, $$2.onBreak());
        }
    }

    public MapCodec<ChangeItemDamage> codec() {
        return CODEC;
    }
}

