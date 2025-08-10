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
import java.util.function.Predicate;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.FireworkExplosion;

public record FireworkExplosionPredicate(FireworkPredicate predicate) implements SingleComponentItemPredicate<FireworkExplosion>
{
    public static final Codec<FireworkExplosionPredicate> CODEC = FireworkPredicate.CODEC.xmap(FireworkExplosionPredicate::new, FireworkExplosionPredicate::predicate);

    @Override
    public DataComponentType<FireworkExplosion> componentType() {
        return DataComponents.FIREWORK_EXPLOSION;
    }

    @Override
    public boolean matches(FireworkExplosion $$0) {
        return this.predicate.test($$0);
    }

    public record FireworkPredicate(Optional<FireworkExplosion.Shape> shape, Optional<Boolean> twinkle, Optional<Boolean> trail) implements Predicate<FireworkExplosion>
    {
        public static final Codec<FireworkPredicate> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)FireworkExplosion.Shape.CODEC.optionalFieldOf("shape").forGetter(FireworkPredicate::shape), (App)Codec.BOOL.optionalFieldOf("has_twinkle").forGetter(FireworkPredicate::twinkle), (App)Codec.BOOL.optionalFieldOf("has_trail").forGetter(FireworkPredicate::trail)).apply((Applicative)$$0, FireworkPredicate::new));

        @Override
        public boolean test(FireworkExplosion $$0) {
            if (this.shape.isPresent() && this.shape.get() != $$0.shape()) {
                return false;
            }
            if (this.twinkle.isPresent() && this.twinkle.get().booleanValue() != $$0.hasTwinkle()) {
                return false;
            }
            return !this.trail.isPresent() || this.trail.get().booleanValue() == $$0.hasTrail();
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((FireworkExplosion)object);
        }
    }
}

