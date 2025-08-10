/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetAttributesFunction
extends LootItemConditionalFunction {
    public static final MapCodec<SetAttributesFunction> CODEC = RecordCodecBuilder.mapCodec($$02 -> SetAttributesFunction.commonFields($$02).and($$02.group((App)Modifier.CODEC.listOf().fieldOf("modifiers").forGetter($$0 -> $$0.modifiers), (App)Codec.BOOL.optionalFieldOf("replace", (Object)true).forGetter($$0 -> $$0.replace))).apply((Applicative)$$02, SetAttributesFunction::new));
    private final List<Modifier> modifiers;
    private final boolean replace;

    SetAttributesFunction(List<LootItemCondition> $$0, List<Modifier> $$1, boolean $$2) {
        super($$0);
        this.modifiers = List.copyOf($$1);
        this.replace = $$2;
    }

    public LootItemFunctionType<SetAttributesFunction> getType() {
        return LootItemFunctions.SET_ATTRIBUTES;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.modifiers.stream().flatMap($$0 -> $$0.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ItemStack run(ItemStack $$0, LootContext $$12) {
        if (this.replace) {
            $$0.set(DataComponents.ATTRIBUTE_MODIFIERS, this.updateModifiers($$12, ItemAttributeModifiers.EMPTY));
        } else {
            $$0.update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, $$1 -> this.updateModifiers($$12, (ItemAttributeModifiers)((Object)$$1)));
        }
        return $$0;
    }

    private ItemAttributeModifiers updateModifiers(LootContext $$0, ItemAttributeModifiers $$1) {
        RandomSource $$2 = $$0.getRandom();
        for (Modifier $$3 : this.modifiers) {
            EquipmentSlotGroup $$4 = Util.getRandom($$3.slots, $$2);
            $$1 = $$1.withModifierAdded($$3.attribute, new AttributeModifier($$3.id, $$3.amount.getFloat($$0), $$3.operation), $$4);
        }
        return $$1;
    }

    public static ModifierBuilder modifier(ResourceLocation $$0, Holder<Attribute> $$1, AttributeModifier.Operation $$2, NumberProvider $$3) {
        return new ModifierBuilder($$0, $$1, $$2, $$3);
    }

    public static Builder setAttributes() {
        return new Builder();
    }

    static final class Modifier
    extends Record {
        final ResourceLocation id;
        final Holder<Attribute> attribute;
        final AttributeModifier.Operation operation;
        final NumberProvider amount;
        final List<EquipmentSlotGroup> slots;
        private static final Codec<List<EquipmentSlotGroup>> SLOTS_CODEC = ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(EquipmentSlotGroup.CODEC));
        public static final Codec<Modifier> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(Modifier::id), (App)Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute), (App)AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation), (App)NumberProviders.CODEC.fieldOf("amount").forGetter(Modifier::amount), (App)SLOTS_CODEC.fieldOf("slot").forGetter(Modifier::slots)).apply((Applicative)$$0, Modifier::new));

        Modifier(ResourceLocation $$0, Holder<Attribute> $$1, AttributeModifier.Operation $$2, NumberProvider $$3, List<EquipmentSlotGroup> $$4) {
            this.id = $$0;
            this.attribute = $$1;
            this.operation = $$2;
            this.amount = $$3;
            this.slots = $$4;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Modifier.class, "id;attribute;operation;amount;slots", "id", "attribute", "operation", "amount", "slots"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Modifier.class, "id;attribute;operation;amount;slots", "id", "attribute", "operation", "amount", "slots"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Modifier.class, "id;attribute;operation;amount;slots", "id", "attribute", "operation", "amount", "slots"}, this, $$0);
        }

        public ResourceLocation id() {
            return this.id;
        }

        public Holder<Attribute> attribute() {
            return this.attribute;
        }

        public AttributeModifier.Operation operation() {
            return this.operation;
        }

        public NumberProvider amount() {
            return this.amount;
        }

        public List<EquipmentSlotGroup> slots() {
            return this.slots;
        }
    }

    public static class ModifierBuilder {
        private final ResourceLocation id;
        private final Holder<Attribute> attribute;
        private final AttributeModifier.Operation operation;
        private final NumberProvider amount;
        private final Set<EquipmentSlotGroup> slots = EnumSet.noneOf(EquipmentSlotGroup.class);

        public ModifierBuilder(ResourceLocation $$0, Holder<Attribute> $$1, AttributeModifier.Operation $$2, NumberProvider $$3) {
            this.id = $$0;
            this.attribute = $$1;
            this.operation = $$2;
            this.amount = $$3;
        }

        public ModifierBuilder forSlot(EquipmentSlotGroup $$0) {
            this.slots.add($$0);
            return this;
        }

        public Modifier build() {
            return new Modifier(this.id, this.attribute, this.operation, this.amount, List.copyOf(this.slots));
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final boolean replace;
        private final List<Modifier> modifiers = Lists.newArrayList();

        public Builder(boolean $$0) {
            this.replace = $$0;
        }

        public Builder() {
            this(false);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withModifier(ModifierBuilder $$0) {
            this.modifiers.add($$0.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetAttributesFunction(this.getConditions(), this.modifiers, this.replace);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }
}

