/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.alchemy;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;

public record PotionContents(Optional<Holder<Potion>> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects, Optional<String> customName) implements ConsumableListener,
TooltipProvider
{
    private final List<MobEffectInstance> customEffects;
    public static final PotionContents EMPTY = new PotionContents(Optional.empty(), Optional.empty(), List.of(), Optional.empty());
    private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);
    public static final int BASE_POTION_COLOR = -13083194;
    private static final Codec<PotionContents> FULL_CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Potion.CODEC.optionalFieldOf("potion").forGetter(PotionContents::potion), (App)Codec.INT.optionalFieldOf("custom_color").forGetter(PotionContents::customColor), (App)MobEffectInstance.CODEC.listOf().optionalFieldOf("custom_effects", (Object)List.of()).forGetter(PotionContents::customEffects), (App)Codec.STRING.optionalFieldOf("custom_name").forGetter(PotionContents::customName)).apply((Applicative)$$0, PotionContents::new));
    public static final Codec<PotionContents> CODEC = Codec.withAlternative(FULL_CODEC, Potion.CODEC, PotionContents::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, PotionContents> STREAM_CODEC = StreamCodec.composite(Potion.STREAM_CODEC.apply(ByteBufCodecs::optional), PotionContents::potion, ByteBufCodecs.INT.apply(ByteBufCodecs::optional), PotionContents::customColor, MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), PotionContents::customEffects, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), PotionContents::customName, PotionContents::new);

    public PotionContents(Holder<Potion> $$0) {
        this(Optional.of($$0), Optional.empty(), List.of(), Optional.empty());
    }

    public static ItemStack createItemStack(Item $$0, Holder<Potion> $$1) {
        ItemStack $$2 = new ItemStack($$0);
        $$2.set(DataComponents.POTION_CONTENTS, new PotionContents($$1));
        return $$2;
    }

    public boolean is(Holder<Potion> $$0) {
        return this.potion.isPresent() && this.potion.get().is($$0) && this.customEffects.isEmpty();
    }

    public Iterable<MobEffectInstance> getAllEffects() {
        if (this.potion.isEmpty()) {
            return this.customEffects;
        }
        if (this.customEffects.isEmpty()) {
            return this.potion.get().value().getEffects();
        }
        return Iterables.concat(this.potion.get().value().getEffects(), this.customEffects);
    }

    public void forEachEffect(Consumer<MobEffectInstance> $$0, float $$1) {
        if (this.potion.isPresent()) {
            for (MobEffectInstance $$2 : this.potion.get().value().getEffects()) {
                $$0.accept($$2.withScaledDuration($$1));
            }
        }
        for (MobEffectInstance $$3 : this.customEffects) {
            $$0.accept($$3.withScaledDuration($$1));
        }
    }

    public PotionContents withPotion(Holder<Potion> $$0) {
        return new PotionContents(Optional.of($$0), this.customColor, this.customEffects, this.customName);
    }

    public PotionContents withEffectAdded(MobEffectInstance $$0) {
        return new PotionContents(this.potion, this.customColor, Util.copyAndAdd(this.customEffects, $$0), this.customName);
    }

    public int getColor() {
        return this.getColorOr(-13083194);
    }

    public int getColorOr(int $$0) {
        if (this.customColor.isPresent()) {
            return this.customColor.get();
        }
        return PotionContents.getColorOptional(this.getAllEffects()).orElse($$0);
    }

    public Component getName(String $$0) {
        String $$1 = this.customName.or(() -> this.potion.map($$0 -> ((Potion)$$0.value()).name())).orElse("empty");
        return Component.translatable($$0 + $$1);
    }

    public static OptionalInt getColorOptional(Iterable<MobEffectInstance> $$0) {
        int $$1 = 0;
        int $$2 = 0;
        int $$3 = 0;
        int $$4 = 0;
        for (MobEffectInstance $$5 : $$0) {
            if (!$$5.isVisible()) continue;
            int $$6 = $$5.getEffect().value().getColor();
            int $$7 = $$5.getAmplifier() + 1;
            $$1 += $$7 * ARGB.red($$6);
            $$2 += $$7 * ARGB.green($$6);
            $$3 += $$7 * ARGB.blue($$6);
            $$4 += $$7;
        }
        if ($$4 == 0) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(ARGB.color($$1 / $$4, $$2 / $$4, $$3 / $$4));
    }

    public boolean hasEffects() {
        if (!this.customEffects.isEmpty()) {
            return true;
        }
        return this.potion.isPresent() && !this.potion.get().value().getEffects().isEmpty();
    }

    public List<MobEffectInstance> customEffects() {
        return Lists.transform(this.customEffects, MobEffectInstance::new);
    }

    /*
     * WARNING - void declaration
     */
    public void applyToLivingEntity(LivingEntity $$0, float $$1) {
        void $$3;
        Player $$4;
        Level level = $$0.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel $$2 = (ServerLevel)level;
        Player $$5 = $$0 instanceof Player ? ($$4 = (Player)$$0) : null;
        this.forEachEffect(arg_0 -> PotionContents.lambda$applyToLivingEntity$3((ServerLevel)$$3, $$5, $$0, arg_0), $$1);
    }

    public static void addPotionTooltip(Iterable<MobEffectInstance> $$0, Consumer<Component> $$12, float $$22, float $$3) {
        ArrayList<Pair> $$4 = Lists.newArrayList();
        boolean $$5 = true;
        for (MobEffectInstance $$6 : $$0) {
            $$5 = false;
            Holder<MobEffect> $$7 = $$6.getEffect();
            int $$8 = $$6.getAmplifier();
            $$7.value().createModifiers($$8, ($$1, $$2) -> $$4.add(new Pair($$1, (Object)$$2)));
            MutableComponent $$9 = PotionContents.getPotionDescription($$7, $$8);
            if (!$$6.endsWithin(20)) {
                $$9 = Component.a("potion.withDuration", $$9, MobEffectUtil.formatDuration($$6, $$22, $$3));
            }
            $$12.accept($$9.withStyle($$7.value().getCategory().getTooltipFormatting()));
        }
        if ($$5) {
            $$12.accept(NO_EFFECT);
        }
        if (!$$4.isEmpty()) {
            $$12.accept(CommonComponents.EMPTY);
            $$12.accept(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            for (Pair $$10 : $$4) {
                double $$14;
                AttributeModifier $$11 = (AttributeModifier)((Object)$$10.getSecond());
                double $$122 = $$11.amount();
                if ($$11.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || $$11.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    double $$13 = $$11.amount() * 100.0;
                } else {
                    $$14 = $$11.amount();
                }
                if ($$122 > 0.0) {
                    $$12.accept(Component.a("attribute.modifier.plus." + $$11.operation().id(), new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format($$14), Component.translatable(((Attribute)((Holder)$$10.getFirst()).value()).getDescriptionId())}).withStyle(ChatFormatting.BLUE));
                    continue;
                }
                if (!($$122 < 0.0)) continue;
                $$12.accept(Component.a("attribute.modifier.take." + $$11.operation().id(), new Object[]{ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format($$14 *= -1.0), Component.translatable(((Attribute)((Holder)$$10.getFirst()).value()).getDescriptionId())}).withStyle(ChatFormatting.RED));
            }
        }
    }

    public static MutableComponent getPotionDescription(Holder<MobEffect> $$0, int $$1) {
        MutableComponent $$2 = Component.translatable($$0.value().getDescriptionId());
        if ($$1 > 0) {
            return Component.a("potion.withAmplifier", $$2, Component.translatable("potion.potency." + $$1));
        }
        return $$2;
    }

    @Override
    public void onConsume(Level $$0, LivingEntity $$1, ItemStack $$2, Consumable $$3) {
        this.applyToLivingEntity($$1, $$2.getOrDefault(DataComponents.POTION_DURATION_SCALE, Float.valueOf(1.0f)).floatValue());
    }

    @Override
    public void addToTooltip(Item.TooltipContext $$0, Consumer<Component> $$1, TooltipFlag $$2, DataComponentGetter $$3) {
        PotionContents.addPotionTooltip(this.getAllEffects(), $$1, $$3.getOrDefault(DataComponents.POTION_DURATION_SCALE, Float.valueOf(1.0f)).floatValue(), $$0.tickRate());
    }

    private static /* synthetic */ void lambda$applyToLivingEntity$3(ServerLevel $$0, Player $$1, LivingEntity $$2, MobEffectInstance $$3) {
        if ($$3.getEffect().value().isInstantenous()) {
            $$3.getEffect().value().applyInstantenousEffect($$0, $$1, $$1, $$2, $$3.getAmplifier(), 1.0);
        } else {
            $$2.addEffect($$3);
        }
    }
}

