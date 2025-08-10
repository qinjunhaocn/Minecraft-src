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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public record Consumable(float consumeSeconds, ItemUseAnimation animation, Holder<SoundEvent> sound, boolean hasConsumeParticles, List<ConsumeEffect> onConsumeEffects) {
    public static final float DEFAULT_CONSUME_SECONDS = 1.6f;
    private static final int CONSUME_EFFECTS_INTERVAL = 4;
    private static final float CONSUME_EFFECTS_START_FRACTION = 0.21875f;
    public static final Codec<Consumable> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("consume_seconds", (Object)Float.valueOf(1.6f)).forGetter(Consumable::consumeSeconds), (App)ItemUseAnimation.CODEC.optionalFieldOf("animation", (Object)ItemUseAnimation.EAT).forGetter(Consumable::animation), (App)SoundEvent.CODEC.optionalFieldOf("sound", SoundEvents.GENERIC_EAT).forGetter(Consumable::sound), (App)Codec.BOOL.optionalFieldOf("has_consume_particles", (Object)true).forGetter(Consumable::hasConsumeParticles), (App)ConsumeEffect.CODEC.listOf().optionalFieldOf("on_consume_effects", (Object)List.of()).forGetter(Consumable::onConsumeEffects)).apply((Applicative)$$0, Consumable::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Consumable> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, Consumable::consumeSeconds, ItemUseAnimation.STREAM_CODEC, Consumable::animation, SoundEvent.STREAM_CODEC, Consumable::sound, ByteBufCodecs.BOOL, Consumable::hasConsumeParticles, ConsumeEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), Consumable::onConsumeEffects, Consumable::new);

    public InteractionResult startConsuming(LivingEntity $$0, ItemStack $$1, InteractionHand $$2) {
        boolean $$3;
        if (!this.canConsume($$0, $$1)) {
            return InteractionResult.FAIL;
        }
        boolean bl = $$3 = this.consumeTicks() > 0;
        if ($$3) {
            $$0.startUsingItem($$2);
            return InteractionResult.CONSUME;
        }
        ItemStack $$4 = this.onConsume($$0.level(), $$0, $$1);
        return InteractionResult.CONSUME.heldItemTransformedTo($$4);
    }

    public ItemStack onConsume(Level $$0, LivingEntity $$1, ItemStack $$2) {
        RandomSource $$32 = $$1.getRandom();
        this.emitParticlesAndSounds($$32, $$1, $$2, 16);
        if ($$1 instanceof ServerPlayer) {
            ServerPlayer $$4 = (ServerPlayer)$$1;
            $$4.awardStat(Stats.ITEM_USED.get($$2.getItem()));
            CriteriaTriggers.CONSUME_ITEM.trigger($$4, $$2);
        }
        $$2.getAllOfType(ConsumableListener.class).forEach($$3 -> $$3.onConsume($$0, $$1, $$2, this));
        if (!$$0.isClientSide) {
            this.onConsumeEffects.forEach($$3 -> $$3.apply($$0, $$2, $$1));
        }
        $$1.gameEvent(this.animation == ItemUseAnimation.DRINK ? GameEvent.DRINK : GameEvent.EAT);
        $$2.consume(1, $$1);
        return $$2;
    }

    public boolean canConsume(LivingEntity $$0, ItemStack $$1) {
        FoodProperties $$2 = $$1.get(DataComponents.FOOD);
        if ($$2 != null && $$0 instanceof Player) {
            Player $$3 = (Player)$$0;
            return $$3.canEat($$2.canAlwaysEat());
        }
        return true;
    }

    public int consumeTicks() {
        return (int)(this.consumeSeconds * 20.0f);
    }

    public void emitParticlesAndSounds(RandomSource $$0, LivingEntity $$1, ItemStack $$2, int $$3) {
        SoundEvent soundEvent;
        float $$9;
        float $$4 = $$0.nextBoolean() ? 0.5f : 1.0f;
        float $$5 = $$0.triangle(1.0f, 0.2f);
        float $$6 = 0.5f;
        float $$7 = Mth.randomBetween($$0, 0.9f, 1.0f);
        float $$8 = this.animation == ItemUseAnimation.DRINK ? 0.5f : $$4;
        float f = $$9 = this.animation == ItemUseAnimation.DRINK ? $$7 : $$5;
        if (this.hasConsumeParticles) {
            $$1.spawnItemParticles($$2, $$3);
        }
        if ($$1 instanceof OverrideConsumeSound) {
            OverrideConsumeSound $$10 = (OverrideConsumeSound)((Object)$$1);
            soundEvent = $$10.getConsumeSound($$2);
        } else {
            soundEvent = this.sound.value();
        }
        SoundEvent $$11 = soundEvent;
        $$1.playSound($$11, $$8, $$9);
    }

    public boolean shouldEmitParticlesAndSounds(int $$0) {
        int $$2;
        int $$1 = this.consumeTicks() - $$0;
        boolean $$3 = $$1 > ($$2 = (int)((float)this.consumeTicks() * 0.21875f));
        return $$3 && $$0 % 4 == 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static interface OverrideConsumeSound {
        public SoundEvent getConsumeSound(ItemStack var1);
    }

    public static class Builder {
        private float consumeSeconds = 1.6f;
        private ItemUseAnimation animation = ItemUseAnimation.EAT;
        private Holder<SoundEvent> sound = SoundEvents.GENERIC_EAT;
        private boolean hasConsumeParticles = true;
        private final List<ConsumeEffect> onConsumeEffects = new ArrayList<ConsumeEffect>();

        Builder() {
        }

        public Builder consumeSeconds(float $$0) {
            this.consumeSeconds = $$0;
            return this;
        }

        public Builder animation(ItemUseAnimation $$0) {
            this.animation = $$0;
            return this;
        }

        public Builder sound(Holder<SoundEvent> $$0) {
            this.sound = $$0;
            return this;
        }

        public Builder soundAfterConsume(Holder<SoundEvent> $$0) {
            return this.onConsume(new PlaySoundConsumeEffect($$0));
        }

        public Builder hasConsumeParticles(boolean $$0) {
            this.hasConsumeParticles = $$0;
            return this;
        }

        public Builder onConsume(ConsumeEffect $$0) {
            this.onConsumeEffects.add($$0);
            return this;
        }

        public Consumable build() {
            return new Consumable(this.consumeSeconds, this.animation, this.sound, this.hasConsumeParticles, this.onConsumeEffects);
        }
    }
}

