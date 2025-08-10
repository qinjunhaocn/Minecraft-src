/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record ComponentMatches(DataComponentPredicate.Single<?> predicate) implements ConditionalItemModelProperty
{
    public static final MapCodec<ComponentMatches> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DataComponentPredicate.singleCodec("predicate").forGetter(ComponentMatches::predicate)).apply((Applicative)$$0, ComponentMatches::new));

    @Override
    public boolean get(ItemStack $$0, @Nullable ClientLevel $$1, @Nullable LivingEntity $$2, int $$3, ItemDisplayContext $$4) {
        return this.predicate.predicate().matches($$0);
    }

    public MapCodec<ComponentMatches> type() {
        return MAP_CODEC;
    }
}

