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
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record SummonEntityEffect(HolderSet<EntityType<?>> entityTypes, boolean joinTeam) implements EnchantmentEntityEffect
{
    public static final MapCodec<SummonEntityEffect> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(SummonEntityEffect::entityTypes), (App)Codec.BOOL.optionalFieldOf("join_team", (Object)false).forGetter(SummonEntityEffect::joinTeam)).apply((Applicative)$$0, SummonEntityEffect::new));

    @Override
    public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
        BlockPos $$5 = BlockPos.containing($$4);
        if (!Level.isInSpawnableBounds($$5)) {
            return;
        }
        Optional<Holder<EntityType<?>>> $$6 = this.entityTypes().getRandomElement($$0.getRandom());
        if ($$6.isEmpty()) {
            return;
        }
        Object $$7 = $$6.get().value().spawn($$0, $$5, EntitySpawnReason.TRIGGERED);
        if ($$7 == null) {
            return;
        }
        if ($$7 instanceof LightningBolt) {
            LightningBolt $$8 = (LightningBolt)$$7;
            LivingEntity livingEntity = $$2.owner();
            if (livingEntity instanceof ServerPlayer) {
                ServerPlayer $$9 = (ServerPlayer)livingEntity;
                $$8.setCause($$9);
            }
        }
        if (this.joinTeam && $$3.getTeam() != null) {
            $$0.getScoreboard().addPlayerToTeam(((Entity)$$7).getScoreboardName(), $$3.getTeam());
        }
        ((Entity)$$7).snapTo($$4.x, $$4.y, $$4.z, ((Entity)$$7).getYRot(), ((Entity)$$7).getXRot());
    }

    public MapCodec<SummonEntityEffect> codec() {
        return CODEC;
    }
}

