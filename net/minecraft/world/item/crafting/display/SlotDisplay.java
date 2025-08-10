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
package net.minecraft.world.item.crafting.display;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.block.entity.FuelValues;

public interface SlotDisplay {
    public static final Codec<SlotDisplay> CODEC = BuiltInRegistries.SLOT_DISPLAY.byNameCodec().dispatch(SlotDisplay::type, Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, SlotDisplay> STREAM_CODEC = ByteBufCodecs.registry(Registries.SLOT_DISPLAY).dispatch(SlotDisplay::type, Type::streamCodec);

    public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2);

    public Type<? extends SlotDisplay> type();

    default public boolean isEnabled(FeatureFlagSet $$0) {
        return true;
    }

    default public List<ItemStack> resolveForStacks(ContextMap $$0) {
        return this.resolve($$0, ItemStackContentsFactory.INSTANCE).toList();
    }

    default public ItemStack resolveForFirstStack(ContextMap $$0) {
        return this.resolve($$0, ItemStackContentsFactory.INSTANCE).findFirst().orElse(ItemStack.EMPTY);
    }

    public static class ItemStackContentsFactory
    implements DisplayContentsFactory.ForStacks<ItemStack> {
        public static final ItemStackContentsFactory INSTANCE = new ItemStackContentsFactory();

        @Override
        public ItemStack forStack(ItemStack $$0) {
            return $$0;
        }

        @Override
        public /* synthetic */ Object forStack(ItemStack itemStack) {
            return this.forStack(itemStack);
        }
    }

    public record WithRemainder(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay
    {
        public static final MapCodec<WithRemainder> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)CODEC.fieldOf("input").forGetter(WithRemainder::input), (App)CODEC.fieldOf("remainder").forGetter(WithRemainder::remainder)).apply((Applicative)$$0, WithRemainder::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, WithRemainder> STREAM_CODEC = StreamCodec.composite(STREAM_CODEC, WithRemainder::input, STREAM_CODEC, WithRemainder::remainder, WithRemainder::new);
        public static final Type<WithRemainder> TYPE = new Type<WithRemainder>(MAP_CODEC, STREAM_CODEC);

        public Type<WithRemainder> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap $$0, DisplayContentsFactory<T> $$1) {
            if ($$1 instanceof DisplayContentsFactory.ForRemainders) {
                DisplayContentsFactory.ForRemainders $$22 = (DisplayContentsFactory.ForRemainders)$$1;
                List $$3 = this.remainder.resolve($$0, $$1).toList();
                return this.input.resolve($$0, $$1).map($$2 -> $$22.addRemainder($$2, $$3));
            }
            return this.input.resolve($$0, $$1);
        }

        @Override
        public boolean isEnabled(FeatureFlagSet $$0) {
            return this.input.isEnabled($$0) && this.remainder.isEnabled($$0);
        }
    }

    public record Composite(List<SlotDisplay> contents) implements SlotDisplay
    {
        public static final MapCodec<Composite> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)CODEC.listOf().fieldOf("contents").forGetter(Composite::contents)).apply((Applicative)$$0, Composite::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Composite> STREAM_CODEC = StreamCodec.composite(STREAM_CODEC.apply(ByteBufCodecs.list()), Composite::contents, Composite::new);
        public static final Type<Composite> TYPE = new Type<Composite>(MAP_CODEC, STREAM_CODEC);

        public Type<Composite> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap $$0, DisplayContentsFactory<T> $$1) {
            return this.contents.stream().flatMap($$2 -> $$2.resolve($$0, $$1));
        }

        @Override
        public boolean isEnabled(FeatureFlagSet $$0) {
            return this.contents.stream().allMatch($$1 -> $$1.isEnabled($$0));
        }
    }

    public record TagSlotDisplay(TagKey<Item> tag) implements SlotDisplay
    {
        public static final MapCodec<TagSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(TagSlotDisplay::tag)).apply((Applicative)$$0, TagSlotDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, TagSlotDisplay> STREAM_CODEC = StreamCodec.composite(TagKey.streamCodec(Registries.ITEM), TagSlotDisplay::tag, TagSlotDisplay::new);
        public static final Type<TagSlotDisplay> TYPE = new Type<TagSlotDisplay>(MAP_CODEC, STREAM_CODEC);

        public Type<TagSlotDisplay> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap $$02, DisplayContentsFactory<T> $$12) {
            if ($$12 instanceof DisplayContentsFactory.ForStacks) {
                DisplayContentsFactory.ForStacks $$2 = (DisplayContentsFactory.ForStacks)$$12;
                HolderLookup.Provider $$3 = $$02.getOptional(SlotDisplayContext.REGISTRIES);
                if ($$3 != null) {
                    return $$3.lookupOrThrow(Registries.ITEM).get(this.tag).map($$1 -> $$1.stream().map($$2::forStack)).stream().flatMap($$0 -> $$0);
                }
            }
            return Stream.empty();
        }
    }

    public record ItemStackSlotDisplay(ItemStack stack) implements SlotDisplay
    {
        public static final MapCodec<ItemStackSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ItemStack.STRICT_CODEC.fieldOf("item").forGetter(ItemStackSlotDisplay::stack)).apply((Applicative)$$0, ItemStackSlotDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackSlotDisplay> STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, ItemStackSlotDisplay::stack, ItemStackSlotDisplay::new);
        public static final Type<ItemStackSlotDisplay> TYPE = new Type<ItemStackSlotDisplay>(MAP_CODEC, STREAM_CODEC);

        public Type<ItemStackSlotDisplay> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap $$0, DisplayContentsFactory<T> $$1) {
            if ($$1 instanceof DisplayContentsFactory.ForStacks) {
                DisplayContentsFactory.ForStacks $$2 = (DisplayContentsFactory.ForStacks)$$1;
                return Stream.of($$2.forStack(this.stack));
            }
            return Stream.empty();
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public boolean equals(Object $$0) {
            if (this == $$0) return true;
            if (!($$0 instanceof ItemStackSlotDisplay)) return false;
            ItemStackSlotDisplay $$1 = (ItemStackSlotDisplay)$$0;
            if (!ItemStack.matches(this.stack, $$1.stack)) return false;
            return true;
        }

        @Override
        public boolean isEnabled(FeatureFlagSet $$0) {
            return this.stack.getItem().isEnabled($$0);
        }
    }

    public record ItemSlotDisplay(Holder<Item> item) implements SlotDisplay
    {
        public static final MapCodec<ItemSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Item.CODEC.fieldOf("item").forGetter(ItemSlotDisplay::item)).apply((Applicative)$$0, ItemSlotDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemSlotDisplay> STREAM_CODEC = StreamCodec.composite(Item.STREAM_CODEC, ItemSlotDisplay::item, ItemSlotDisplay::new);
        public static final Type<ItemSlotDisplay> TYPE = new Type<ItemSlotDisplay>(MAP_CODEC, STREAM_CODEC);

        public ItemSlotDisplay(Item $$0) {
            this($$0.builtInRegistryHolder());
        }

        public Type<ItemSlotDisplay> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap $$0, DisplayContentsFactory<T> $$1) {
            if ($$1 instanceof DisplayContentsFactory.ForStacks) {
                DisplayContentsFactory.ForStacks $$2 = (DisplayContentsFactory.ForStacks)$$1;
                return Stream.of($$2.forStack(this.item));
            }
            return Stream.empty();
        }

        @Override
        public boolean isEnabled(FeatureFlagSet $$0) {
            return this.item.value().isEnabled($$0);
        }
    }

    public record SmithingTrimDemoSlotDisplay(SlotDisplay base, SlotDisplay material, Holder<TrimPattern> pattern) implements SlotDisplay
    {
        public static final MapCodec<SmithingTrimDemoSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)CODEC.fieldOf("base").forGetter(SmithingTrimDemoSlotDisplay::base), (App)CODEC.fieldOf("material").forGetter(SmithingTrimDemoSlotDisplay::material), (App)TrimPattern.CODEC.fieldOf("pattern").forGetter(SmithingTrimDemoSlotDisplay::pattern)).apply((Applicative)$$0, SmithingTrimDemoSlotDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimDemoSlotDisplay> STREAM_CODEC = StreamCodec.composite(STREAM_CODEC, SmithingTrimDemoSlotDisplay::base, STREAM_CODEC, SmithingTrimDemoSlotDisplay::material, TrimPattern.STREAM_CODEC, SmithingTrimDemoSlotDisplay::pattern, SmithingTrimDemoSlotDisplay::new);
        public static final Type<SmithingTrimDemoSlotDisplay> TYPE = new Type<SmithingTrimDemoSlotDisplay>(MAP_CODEC, STREAM_CODEC);

        public Type<SmithingTrimDemoSlotDisplay> type() {
            return TYPE;
        }

        @Override
        public <T> Stream<T> resolve(ContextMap $$02, DisplayContentsFactory<T> $$1) {
            if ($$1 instanceof DisplayContentsFactory.ForStacks) {
                DisplayContentsFactory.ForStacks $$2 = (DisplayContentsFactory.ForStacks)$$1;
                HolderLookup.Provider $$3 = $$02.getOptional(SlotDisplayContext.REGISTRIES);
                if ($$3 != null) {
                    RandomSource $$4 = RandomSource.create(System.identityHashCode(this));
                    List<ItemStack> $$5 = this.base.resolveForStacks($$02);
                    if ($$5.isEmpty()) {
                        return Stream.empty();
                    }
                    List<ItemStack> $$6 = this.material.resolveForStacks($$02);
                    if ($$6.isEmpty()) {
                        return Stream.empty();
                    }
                    return Stream.generate(() -> {
                        ItemStack $$4 = (ItemStack)Util.getRandom($$5, $$4);
                        ItemStack $$5 = (ItemStack)Util.getRandom($$6, $$4);
                        return SmithingTrimRecipe.applyTrim($$3, $$4, $$5, this.pattern);
                    }).limit(256L).filter($$0 -> !$$0.isEmpty()).limit(16L).map($$2::forStack);
                }
            }
            return Stream.empty();
        }
    }

    public static class AnyFuel
    implements SlotDisplay {
        public static final AnyFuel INSTANCE = new AnyFuel();
        public static final MapCodec<AnyFuel> MAP_CODEC = MapCodec.unit((Object)INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, AnyFuel> STREAM_CODEC = StreamCodec.unit(INSTANCE);
        public static final Type<AnyFuel> TYPE = new Type<AnyFuel>(MAP_CODEC, STREAM_CODEC);

        private AnyFuel() {
        }

        public Type<AnyFuel> type() {
            return TYPE;
        }

        public String toString() {
            return "<any fuel>";
        }

        @Override
        public <T> Stream<T> resolve(ContextMap $$0, DisplayContentsFactory<T> $$1) {
            if ($$1 instanceof DisplayContentsFactory.ForStacks) {
                DisplayContentsFactory.ForStacks $$2 = (DisplayContentsFactory.ForStacks)$$1;
                FuelValues $$3 = $$0.getOptional(SlotDisplayContext.FUEL_VALUES);
                if ($$3 != null) {
                    return $$3.fuelItems().stream().map($$2::forStack);
                }
            }
            return Stream.empty();
        }
    }

    public static class Empty
    implements SlotDisplay {
        public static final Empty INSTANCE = new Empty();
        public static final MapCodec<Empty> MAP_CODEC = MapCodec.unit((Object)INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, Empty> STREAM_CODEC = StreamCodec.unit(INSTANCE);
        public static final Type<Empty> TYPE = new Type<Empty>(MAP_CODEC, STREAM_CODEC);

        private Empty() {
        }

        public Type<Empty> type() {
            return TYPE;
        }

        public String toString() {
            return "<empty>";
        }

        @Override
        public <T> Stream<T> resolve(ContextMap $$0, DisplayContentsFactory<T> $$1) {
            return Stream.empty();
        }
    }

    public record Type<T extends SlotDisplay>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
    }
}

