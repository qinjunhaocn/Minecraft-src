/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.advancements.critereon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public record LightningBoltPredicate(MinMaxBounds.Ints blocksSetOnFire, Optional<EntityPredicate> entityStruck) implements EntitySubPredicate
{
    public static final MapCodec<LightningBoltPredicate> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)MinMaxBounds.Ints.CODEC.optionalFieldOf("blocks_set_on_fire", (Object)MinMaxBounds.Ints.ANY).forGetter(LightningBoltPredicate::blocksSetOnFire), (App)EntityPredicate.CODEC.optionalFieldOf("entity_struck").forGetter(LightningBoltPredicate::entityStruck)).apply((Applicative)$$0, LightningBoltPredicate::new));

    public static LightningBoltPredicate blockSetOnFire(MinMaxBounds.Ints $$0) {
        return new LightningBoltPredicate($$0, Optional.empty());
    }

    public MapCodec<LightningBoltPredicate> codec() {
        return EntitySubPredicates.LIGHTNING;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean matches(Entity $$0, ServerLevel $$1, @Nullable Vec3 $$22) {
        void $$4;
        if (!($$0 instanceof LightningBolt)) {
            return false;
        }
        LightningBolt $$3 = (LightningBolt)$$0;
        return this.blocksSetOnFire.matches($$4.getBlocksSetOnFire()) && (this.entityStruck.isEmpty() || $$4.getHitEntities().anyMatch($$2 -> this.entityStruck.get().matches($$1, $$22, (Entity)$$2)));
    }
}

