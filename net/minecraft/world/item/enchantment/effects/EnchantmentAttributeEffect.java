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

import com.google.common.collect.HashMultimap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.Vec3;

public record EnchantmentAttributeEffect(ResourceLocation id, Holder<Attribute> attribute, LevelBasedValue amount, AttributeModifier.Operation operation) implements EnchantmentLocationBasedEffect
{
    public static final MapCodec<EnchantmentAttributeEffect> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(EnchantmentAttributeEffect::id), (App)Attribute.CODEC.fieldOf("attribute").forGetter(EnchantmentAttributeEffect::attribute), (App)LevelBasedValue.CODEC.fieldOf("amount").forGetter(EnchantmentAttributeEffect::amount), (App)AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(EnchantmentAttributeEffect::operation)).apply((Applicative)$$0, EnchantmentAttributeEffect::new));

    private ResourceLocation idForSlot(StringRepresentable $$0) {
        return this.id.withSuffix("/" + $$0.getSerializedName());
    }

    public AttributeModifier getModifier(int $$0, StringRepresentable $$1) {
        return new AttributeModifier(this.idForSlot($$1), this.amount().calculate($$0), this.operation());
    }

    @Override
    public void onChangedBlock(ServerLevel $$0, int $$1, EnchantedItemInUse $$2, Entity $$3, Vec3 $$4, boolean $$5) {
        if ($$5 && $$3 instanceof LivingEntity) {
            LivingEntity $$6 = (LivingEntity)$$3;
            $$6.getAttributes().addTransientAttributeModifiers(this.makeAttributeMap($$1, $$2.inSlot()));
        }
    }

    @Override
    public void onDeactivated(EnchantedItemInUse $$0, Entity $$1, Vec3 $$2, int $$3) {
        if ($$1 instanceof LivingEntity) {
            LivingEntity $$4 = (LivingEntity)$$1;
            $$4.getAttributes().removeAttributeModifiers(this.makeAttributeMap($$3, $$0.inSlot()));
        }
    }

    private HashMultimap<Holder<Attribute>, AttributeModifier> makeAttributeMap(int $$0, EquipmentSlot $$1) {
        HashMultimap<Holder<Attribute>, AttributeModifier> $$2 = HashMultimap.create();
        $$2.put(this.attribute, (Object)this.getModifier($$0, $$1));
        return $$2;
    }

    public MapCodec<EnchantmentAttributeEffect> codec() {
        return CODEC;
    }
}

