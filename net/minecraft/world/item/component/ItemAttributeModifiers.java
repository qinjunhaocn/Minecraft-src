/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  java.lang.MatchException
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.apache.commons.lang3.function.TriConsumer
 */
package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.function.TriConsumer;

public record ItemAttributeModifiers(List<Entry> modifiers) {
    public static final ItemAttributeModifiers EMPTY = new ItemAttributeModifiers(List.of());
    public static final Codec<ItemAttributeModifiers> CODEC = Entry.CODEC.listOf().xmap(ItemAttributeModifiers::new, ItemAttributeModifiers::modifiers);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeModifiers> STREAM_CODEC = StreamCodec.composite(Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemAttributeModifiers::modifiers, ItemAttributeModifiers::new);
    public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = Util.make(new DecimalFormat("#.##"), $$0 -> $$0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));

    public static Builder builder() {
        return new Builder();
    }

    public ItemAttributeModifiers withModifierAdded(Holder<Attribute> $$0, AttributeModifier $$1, EquipmentSlotGroup $$2) {
        ImmutableList.Builder $$3 = ImmutableList.builderWithExpectedSize(this.modifiers.size() + 1);
        for (Entry $$4 : this.modifiers) {
            if ($$4.matches($$0, $$1.id())) continue;
            $$3.add((Object)$$4);
        }
        $$3.add((Object)new Entry($$0, $$1, $$2));
        return new ItemAttributeModifiers((List<Entry>)((Object)$$3.build()));
    }

    public void forEach(EquipmentSlotGroup $$0, TriConsumer<Holder<Attribute>, AttributeModifier, Display> $$1) {
        for (Entry $$2 : this.modifiers) {
            if (!$$2.slot.equals($$0)) continue;
            $$1.accept($$2.attribute, (Object)$$2.modifier, (Object)$$2.display);
        }
    }

    public void forEach(EquipmentSlotGroup $$0, BiConsumer<Holder<Attribute>, AttributeModifier> $$1) {
        for (Entry $$2 : this.modifiers) {
            if (!$$2.slot.equals($$0)) continue;
            $$1.accept($$2.attribute, $$2.modifier);
        }
    }

    public void forEach(EquipmentSlot $$0, BiConsumer<Holder<Attribute>, AttributeModifier> $$1) {
        for (Entry $$2 : this.modifiers) {
            if (!$$2.slot.test($$0)) continue;
            $$1.accept($$2.attribute, $$2.modifier);
        }
    }

    public double compute(double $$0, EquipmentSlot $$1) {
        double $$2 = $$0;
        for (Entry $$3 : this.modifiers) {
            if (!$$3.slot.test($$1)) continue;
            double $$4 = $$3.modifier.amount();
            $$2 += (switch ($$3.modifier.operation()) {
                default -> throw new MatchException(null, null);
                case AttributeModifier.Operation.ADD_VALUE -> $$4;
                case AttributeModifier.Operation.ADD_MULTIPLIED_BASE -> $$4 * $$0;
                case AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL -> $$4 * $$2;
            });
        }
        return $$2;
    }

    public static class Builder {
        private final ImmutableList.Builder<Entry> entries = ImmutableList.builder();

        Builder() {
        }

        public Builder add(Holder<Attribute> $$0, AttributeModifier $$1, EquipmentSlotGroup $$2) {
            this.entries.add((Object)new Entry($$0, $$1, $$2));
            return this;
        }

        public Builder add(Holder<Attribute> $$0, AttributeModifier $$1, EquipmentSlotGroup $$2, Display $$3) {
            this.entries.add((Object)new Entry($$0, $$1, $$2, $$3));
            return this;
        }

        public ItemAttributeModifiers build() {
            return new ItemAttributeModifiers((List<Entry>)((Object)this.entries.build()));
        }
    }

    public static final class Entry
    extends Record {
        final Holder<Attribute> attribute;
        final AttributeModifier modifier;
        final EquipmentSlotGroup slot;
        final Display display;
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)Attribute.CODEC.fieldOf("type").forGetter(Entry::attribute), (App)AttributeModifier.MAP_CODEC.forGetter(Entry::modifier), (App)EquipmentSlotGroup.CODEC.optionalFieldOf("slot", (Object)EquipmentSlotGroup.ANY).forGetter(Entry::slot), (App)Display.CODEC.optionalFieldOf("display", (Object)Display.Default.INSTANCE).forGetter(Entry::display)).apply((Applicative)$$0, Entry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(Attribute.STREAM_CODEC, Entry::attribute, AttributeModifier.STREAM_CODEC, Entry::modifier, EquipmentSlotGroup.STREAM_CODEC, Entry::slot, Display.STREAM_CODEC, Entry::display, Entry::new);

        public Entry(Holder<Attribute> $$0, AttributeModifier $$1, EquipmentSlotGroup $$2) {
            this($$0, $$1, $$2, Display.attributeModifiers());
        }

        public Entry(Holder<Attribute> $$0, AttributeModifier $$1, EquipmentSlotGroup $$2, Display $$3) {
            this.attribute = $$0;
            this.modifier = $$1;
            this.slot = $$2;
            this.display = $$3;
        }

        public boolean matches(Holder<Attribute> $$0, ResourceLocation $$1) {
            return $$0.equals(this.attribute) && this.modifier.is($$1);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "attribute;modifier;slot;display", "attribute", "modifier", "slot", "display"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "attribute;modifier;slot;display", "attribute", "modifier", "slot", "display"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "attribute;modifier;slot;display", "attribute", "modifier", "slot", "display"}, this, $$0);
        }

        public Holder<Attribute> attribute() {
            return this.attribute;
        }

        public AttributeModifier modifier() {
            return this.modifier;
        }

        public EquipmentSlotGroup slot() {
            return this.slot;
        }

        public Display display() {
            return this.display;
        }
    }

    public static interface Display {
        public static final Codec<Display> CODEC = Type.CODEC.dispatch("type", Display::type, $$0 -> $$0.codec);
        public static final StreamCodec<RegistryFriendlyByteBuf, Display> STREAM_CODEC = Type.STREAM_CODEC.cast().dispatch(Display::type, Type::streamCodec);

        public static Display attributeModifiers() {
            return Default.INSTANCE;
        }

        public static Display hidden() {
            return Hidden.INSTANCE;
        }

        public static Display override(Component $$0) {
            return new OverrideText($$0);
        }

        public Type type();

        public void apply(Consumer<Component> var1, @Nullable Player var2, Holder<Attribute> var3, AttributeModifier var4);

        public record Default() implements Display
        {
            static final Default INSTANCE = new Default();
            static final MapCodec<Default> CODEC = MapCodec.unit((Object)INSTANCE);
            static final StreamCodec<RegistryFriendlyByteBuf, Default> STREAM_CODEC = StreamCodec.unit(INSTANCE);

            @Override
            public Type type() {
                return Type.DEFAULT;
            }

            @Override
            public void apply(Consumer<Component> $$0, @Nullable Player $$1, Holder<Attribute> $$2, AttributeModifier $$3) {
                double $$8;
                double $$4 = $$3.amount();
                boolean $$5 = false;
                if ($$1 != null) {
                    if ($$3.is(Item.BASE_ATTACK_DAMAGE_ID)) {
                        $$4 += $$1.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                        $$5 = true;
                    } else if ($$3.is(Item.BASE_ATTACK_SPEED_ID)) {
                        $$4 += $$1.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                        $$5 = true;
                    }
                }
                if ($$3.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || $$3.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    double $$6 = $$4 * 100.0;
                } else if ($$2.is(Attributes.KNOCKBACK_RESISTANCE)) {
                    double $$7 = $$4 * 10.0;
                } else {
                    $$8 = $$4;
                }
                if ($$5) {
                    $$0.accept(CommonComponents.space().append(Component.a("attribute.modifier.equals." + $$3.operation().id(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format($$8), Component.translatable($$2.value().getDescriptionId())})).withStyle(ChatFormatting.DARK_GREEN));
                } else if ($$4 > 0.0) {
                    $$0.accept(Component.a("attribute.modifier.plus." + $$3.operation().id(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format($$8), Component.translatable($$2.value().getDescriptionId())}).withStyle($$2.value().getStyle(true)));
                } else if ($$4 < 0.0) {
                    $$0.accept(Component.a("attribute.modifier.take." + $$3.operation().id(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(-$$8), Component.translatable($$2.value().getDescriptionId())}).withStyle($$2.value().getStyle(false)));
                }
            }
        }

        public record Hidden() implements Display
        {
            static final Hidden INSTANCE = new Hidden();
            static final MapCodec<Hidden> CODEC = MapCodec.unit((Object)INSTANCE);
            static final StreamCodec<RegistryFriendlyByteBuf, Hidden> STREAM_CODEC = StreamCodec.unit(INSTANCE);

            @Override
            public Type type() {
                return Type.HIDDEN;
            }

            @Override
            public void apply(Consumer<Component> $$0, @Nullable Player $$1, Holder<Attribute> $$2, AttributeModifier $$3) {
            }
        }

        public record OverrideText(Component component) implements Display
        {
            static final MapCodec<OverrideText> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ComponentSerialization.CODEC.fieldOf("value").forGetter(OverrideText::component)).apply((Applicative)$$0, OverrideText::new));
            static final StreamCodec<RegistryFriendlyByteBuf, OverrideText> STREAM_CODEC = StreamCodec.composite(ComponentSerialization.STREAM_CODEC, OverrideText::component, OverrideText::new);

            @Override
            public Type type() {
                return Type.OVERRIDE;
            }

            @Override
            public void apply(Consumer<Component> $$0, @Nullable Player $$1, Holder<Attribute> $$2, AttributeModifier $$3) {
                $$0.accept(this.component);
            }
        }

        public static final class Type
        extends Enum<Type>
        implements StringRepresentable {
            public static final /* enum */ Type DEFAULT = new Type("default", 0, Default.CODEC, Default.STREAM_CODEC);
            public static final /* enum */ Type HIDDEN = new Type("hidden", 1, Hidden.CODEC, Hidden.STREAM_CODEC);
            public static final /* enum */ Type OVERRIDE = new Type("override", 2, OverrideText.CODEC, OverrideText.STREAM_CODEC);
            static final Codec<Type> CODEC;
            private static final IntFunction<Type> BY_ID;
            static final StreamCodec<ByteBuf, Type> STREAM_CODEC;
            private final String name;
            private final int id;
            final MapCodec<? extends Display> codec;
            private final StreamCodec<RegistryFriendlyByteBuf, ? extends Display> streamCodec;
            private static final /* synthetic */ Type[] $VALUES;

            public static Type[] values() {
                return (Type[])$VALUES.clone();
            }

            public static Type valueOf(String $$0) {
                return Enum.valueOf(Type.class, $$0);
            }

            private Type(String $$0, int $$1, MapCodec<? extends Display> $$2, StreamCodec<RegistryFriendlyByteBuf, ? extends Display> $$3) {
                this.name = $$0;
                this.id = $$1;
                this.codec = $$2;
                this.streamCodec = $$3;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }

            private int id() {
                return this.id;
            }

            private StreamCodec<RegistryFriendlyByteBuf, ? extends Display> streamCodec() {
                return this.streamCodec;
            }

            private static /* synthetic */ Type[] d() {
                return new Type[]{DEFAULT, HIDDEN, OVERRIDE};
            }

            static {
                $VALUES = Type.d();
                CODEC = StringRepresentable.fromEnum(Type::values);
                BY_ID = ByIdMap.a(Type::id, Type.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
                STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Type::id);
            }
        }
    }
}

