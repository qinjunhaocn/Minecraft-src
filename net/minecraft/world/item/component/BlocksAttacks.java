/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record BlocksAttacks(float blockDelaySeconds, float disableCooldownScale, List<DamageReduction> damageReductions, ItemDamageFunction itemDamage, Optional<TagKey<DamageType>> bypassedBy, Optional<Holder<SoundEvent>> blockSound, Optional<Holder<SoundEvent>> disableSound) {
    public static final Codec<BlocksAttacks> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("block_delay_seconds", (Object)Float.valueOf(0.0f)).forGetter(BlocksAttacks::blockDelaySeconds), (App)ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("disable_cooldown_scale", (Object)Float.valueOf(1.0f)).forGetter(BlocksAttacks::disableCooldownScale), (App)DamageReduction.CODEC.listOf().optionalFieldOf("damage_reductions", (Object)List.of((Object)((Object)new DamageReduction(90.0f, Optional.empty(), 0.0f, 1.0f)))).forGetter(BlocksAttacks::damageReductions), (App)ItemDamageFunction.CODEC.optionalFieldOf("item_damage", (Object)ItemDamageFunction.DEFAULT).forGetter(BlocksAttacks::itemDamage), (App)TagKey.hashedCodec(Registries.DAMAGE_TYPE).optionalFieldOf("bypassed_by").forGetter(BlocksAttacks::bypassedBy), (App)SoundEvent.CODEC.optionalFieldOf("block_sound").forGetter(BlocksAttacks::blockSound), (App)SoundEvent.CODEC.optionalFieldOf("disabled_sound").forGetter(BlocksAttacks::disableSound)).apply((Applicative)$$0, BlocksAttacks::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlocksAttacks> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, BlocksAttacks::blockDelaySeconds, ByteBufCodecs.FLOAT, BlocksAttacks::disableCooldownScale, DamageReduction.STREAM_CODEC.apply(ByteBufCodecs.list()), BlocksAttacks::damageReductions, ItemDamageFunction.STREAM_CODEC, BlocksAttacks::itemDamage, TagKey.streamCodec(Registries.DAMAGE_TYPE).apply(ByteBufCodecs::optional), BlocksAttacks::bypassedBy, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), BlocksAttacks::blockSound, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), BlocksAttacks::disableSound, BlocksAttacks::new);

    public void onBlocked(ServerLevel $$0, LivingEntity $$1) {
        this.blockSound.ifPresent($$2 -> $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), (Holder<SoundEvent>)$$2, $$1.getSoundSource(), 1.0f, 0.8f + $$0.random.nextFloat() * 0.4f));
    }

    public void disable(ServerLevel $$0, LivingEntity $$1, float $$22, ItemStack $$3) {
        int $$4 = this.disableBlockingForTicks($$22);
        if ($$4 > 0) {
            if ($$1 instanceof Player) {
                Player $$5 = (Player)$$1;
                $$5.getCooldowns().addCooldown($$3, $$4);
            }
            $$1.stopUsingItem();
            this.disableSound.ifPresent($$2 -> $$0.playSound(null, $$1.getX(), $$1.getY(), $$1.getZ(), (Holder<SoundEvent>)$$2, $$1.getSoundSource(), 0.8f, 0.8f + $$0.random.nextFloat() * 0.4f));
        }
    }

    /*
     * WARNING - void declaration
     */
    public void hurtBlockingItem(Level $$0, ItemStack $$1, LivingEntity $$2, InteractionHand $$3, float $$4) {
        int $$7;
        if (!($$2 instanceof Player)) {
            return;
        }
        Player $$5 = (Player)$$2;
        if (!$$0.isClientSide) {
            void $$6;
            $$6.awardStat(Stats.ITEM_USED.get($$1.getItem()));
        }
        if (($$7 = this.itemDamage.apply($$4)) > 0) {
            $$1.hurtAndBreak($$7, $$2, LivingEntity.getSlotForHand($$3));
        }
    }

    private int disableBlockingForTicks(float $$0) {
        float $$1 = $$0 * this.disableCooldownScale;
        if ($$1 > 0.0f) {
            return Math.round($$1 * 20.0f);
        }
        return 0;
    }

    public int blockDelayTicks() {
        return Math.round(this.blockDelaySeconds * 20.0f);
    }

    public float resolveBlockedDamage(DamageSource $$0, float $$1, double $$2) {
        float $$3 = 0.0f;
        for (DamageReduction $$4 : this.damageReductions) {
            $$3 += $$4.resolve($$0, $$1, $$2);
        }
        return Mth.clamp($$3, 0.0f, $$1);
    }

    public record ItemDamageFunction(float threshold, float base, float factor) {
        public static final Codec<ItemDamageFunction> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("threshold").forGetter(ItemDamageFunction::threshold), (App)Codec.FLOAT.fieldOf("base").forGetter(ItemDamageFunction::base), (App)Codec.FLOAT.fieldOf("factor").forGetter(ItemDamageFunction::factor)).apply((Applicative)$$0, ItemDamageFunction::new));
        public static final StreamCodec<ByteBuf, ItemDamageFunction> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, ItemDamageFunction::threshold, ByteBufCodecs.FLOAT, ItemDamageFunction::base, ByteBufCodecs.FLOAT, ItemDamageFunction::factor, ItemDamageFunction::new);
        public static final ItemDamageFunction DEFAULT = new ItemDamageFunction(1.0f, 0.0f, 1.0f);

        public int apply(float $$0) {
            if ($$0 < this.threshold) {
                return 0;
            }
            return Mth.floor(this.base + this.factor * $$0);
        }
    }

    public record DamageReduction(float horizontalBlockingAngle, Optional<HolderSet<DamageType>> type, float base, float factor) {
        public static final Codec<DamageReduction> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("horizontal_blocking_angle", (Object)Float.valueOf(90.0f)).forGetter(DamageReduction::horizontalBlockingAngle), (App)RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).optionalFieldOf("type").forGetter(DamageReduction::type), (App)Codec.FLOAT.fieldOf("base").forGetter(DamageReduction::base), (App)Codec.FLOAT.fieldOf("factor").forGetter(DamageReduction::factor)).apply((Applicative)$$0, DamageReduction::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, DamageReduction> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, DamageReduction::horizontalBlockingAngle, ByteBufCodecs.holderSet(Registries.DAMAGE_TYPE).apply(ByteBufCodecs::optional), DamageReduction::type, ByteBufCodecs.FLOAT, DamageReduction::base, ByteBufCodecs.FLOAT, DamageReduction::factor, DamageReduction::new);

        public float resolve(DamageSource $$0, float $$1, double $$2) {
            if ($$2 > (double)((float)Math.PI / 180 * this.horizontalBlockingAngle)) {
                return 0.0f;
            }
            if (this.type.isPresent() && !this.type.get().contains($$0.typeHolder())) {
                return 0.0f;
            }
            return Mth.clamp(this.base + this.factor * $$1, 0.0f, $$1);
        }
    }
}

