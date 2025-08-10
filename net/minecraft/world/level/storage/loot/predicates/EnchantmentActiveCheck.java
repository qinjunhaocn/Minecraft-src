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
package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public record EnchantmentActiveCheck(boolean active) implements LootItemCondition
{
    public static final MapCodec<EnchantmentActiveCheck> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.BOOL.fieldOf("active").forGetter(EnchantmentActiveCheck::active)).apply((Applicative)$$0, EnchantmentActiveCheck::new));

    @Override
    public boolean test(LootContext $$0) {
        return $$0.getParameter(LootContextParams.ENCHANTMENT_ACTIVE) == this.active;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.ENCHANTMENT_ACTIVE_CHECK;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ENCHANTMENT_ACTIVE);
    }

    public static LootItemCondition.Builder enchantmentActiveCheck() {
        return () -> new EnchantmentActiveCheck(true);
    }

    public static LootItemCondition.Builder enchantmentInactiveCheck() {
        return () -> new EnchantmentActiveCheck(false);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }
}

