/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.core.component.predicates;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.CollectionPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.FireworkExplosionPredicate;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;

public record FireworksPredicate(Optional<CollectionPredicate<FireworkExplosion, FireworkExplosionPredicate.FireworkPredicate>> explosions, MinMaxBounds.Ints flightDuration) implements SingleComponentItemPredicate<Fireworks>
{
    public static final Codec<FireworksPredicate> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)CollectionPredicate.codec(FireworkExplosionPredicate.FireworkPredicate.CODEC).optionalFieldOf("explosions").forGetter(FireworksPredicate::explosions), (App)MinMaxBounds.Ints.CODEC.optionalFieldOf("flight_duration", (Object)MinMaxBounds.Ints.ANY).forGetter(FireworksPredicate::flightDuration)).apply((Applicative)$$0, FireworksPredicate::new));

    @Override
    public DataComponentType<Fireworks> componentType() {
        return DataComponents.FIREWORKS;
    }

    @Override
    public boolean matches(Fireworks $$0) {
        if (this.explosions.isPresent() && !this.explosions.get().test($$0.explosions())) {
            return false;
        }
        return this.flightDuration.matches($$0.flightDuration());
    }
}

