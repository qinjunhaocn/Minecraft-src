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
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public record ExplodeEffect(boolean attributeToUser, Optional<Holder<DamageType>> damageType, Optional<LevelBasedValue> knockbackMultiplier, Optional<HolderSet<Block>> immuneBlocks, Vec3 offset, LevelBasedValue radius, boolean createFire, Level.ExplosionInteraction blockInteraction, ParticleOptions smallParticle, ParticleOptions largeParticle, Holder<SoundEvent> sound) implements EnchantmentEntityEffect
{
    public static final MapCodec<ExplodeEffect> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.BOOL.optionalFieldOf("attribute_to_user", (Object)false).forGetter(ExplodeEffect::attributeToUser), (App)DamageType.CODEC.optionalFieldOf("damage_type").forGetter(ExplodeEffect::damageType), (App)LevelBasedValue.CODEC.optionalFieldOf("knockback_multiplier").forGetter(ExplodeEffect::knockbackMultiplier), (App)RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("immune_blocks").forGetter(ExplodeEffect::immuneBlocks), (App)Vec3.CODEC.optionalFieldOf("offset", (Object)Vec3.ZERO).forGetter(ExplodeEffect::offset), (App)LevelBasedValue.CODEC.fieldOf("radius").forGetter(ExplodeEffect::radius), (App)Codec.BOOL.optionalFieldOf("create_fire", (Object)false).forGetter(ExplodeEffect::createFire), (App)Level.ExplosionInteraction.CODEC.fieldOf("block_interaction").forGetter(ExplodeEffect::blockInteraction), (App)ParticleTypes.CODEC.fieldOf("small_particle").forGetter(ExplodeEffect::smallParticle), (App)ParticleTypes.CODEC.fieldOf("large_particle").forGetter(ExplodeEffect::largeParticle), (App)SoundEvent.CODEC.fieldOf("sound").forGetter(ExplodeEffect::sound)).apply((Applicative)$$0, ExplodeEffect::new));

    @Override
    public void apply(ServerLevel $$0, int $$12, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
        Vec3 $$5 = $$4.add(this.offset);
        $$0.explode(this.attributeToUser ? $$3 : null, this.getDamageSource($$3, $$5), new SimpleExplosionDamageCalculator(this.blockInteraction != Level.ExplosionInteraction.NONE, this.damageType.isPresent(), this.knockbackMultiplier.map($$1 -> Float.valueOf($$1.calculate($$12))), this.immuneBlocks), $$5.x(), $$5.y(), $$5.z(), Math.max(this.radius.calculate($$12), 0.0f), this.createFire, this.blockInteraction, this.smallParticle, this.largeParticle, this.sound);
    }

    @Nullable
    private DamageSource getDamageSource(Entity $$0, Vec3 $$1) {
        if (this.damageType.isEmpty()) {
            return null;
        }
        if (this.attributeToUser) {
            return new DamageSource(this.damageType.get(), $$0);
        }
        return new DamageSource(this.damageType.get(), $$1);
    }

    public MapCodec<ExplodeEffect> codec() {
        return CODEC;
    }
}

