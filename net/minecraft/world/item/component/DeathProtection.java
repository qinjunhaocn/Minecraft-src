/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

public record DeathProtection(List<ConsumeEffect> deathEffects) {
    public static final Codec<DeathProtection> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ConsumeEffect.CODEC.listOf().optionalFieldOf("death_effects", (Object)List.of()).forGetter(DeathProtection::deathEffects)).apply((Applicative)$$0, DeathProtection::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DeathProtection> STREAM_CODEC = StreamCodec.composite(ConsumeEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), DeathProtection::deathEffects, DeathProtection::new);
    public static final DeathProtection TOTEM_OF_UNDYING = new DeathProtection(List.of((Object)new ClearAllStatusEffectsConsumeEffect(), (Object)new ApplyStatusEffectsConsumeEffect(List.of((Object)new MobEffectInstance(MobEffects.REGENERATION, 900, 1), (Object)new MobEffectInstance(MobEffects.ABSORPTION, 100, 1), (Object)new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0)))));

    public void applyEffects(ItemStack $$0, LivingEntity $$1) {
        for (ConsumeEffect $$2 : this.deathEffects) {
            $$2.apply($$1.level(), $$0, $$1);
        }
    }
}

