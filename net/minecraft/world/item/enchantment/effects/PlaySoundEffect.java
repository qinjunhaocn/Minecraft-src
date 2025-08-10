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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record PlaySoundEffect(Holder<SoundEvent> soundEvent, FloatProvider volume, FloatProvider pitch) implements EnchantmentEntityEffect
{
    public static final MapCodec<PlaySoundEffect> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)SoundEvent.CODEC.fieldOf("sound").forGetter(PlaySoundEffect::soundEvent), (App)FloatProvider.codec(1.0E-5f, 10.0f).fieldOf("volume").forGetter(PlaySoundEffect::volume), (App)FloatProvider.codec(1.0E-5f, 2.0f).fieldOf("pitch").forGetter(PlaySoundEffect::pitch)).apply((Applicative)$$0, PlaySoundEffect::new));

    @Override
    public void apply(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4) {
        RandomSource $$5 = $$3.getRandom();
        if (!$$3.isSilent()) {
            $$0.playSound(null, $$4.x(), $$4.y(), $$4.z(), this.soundEvent, $$3.getSoundSource(), this.volume.sample($$5), this.pitch.sample($$5));
        }
    }

    public MapCodec<PlaySoundEffect> codec() {
        return CODEC;
    }
}

