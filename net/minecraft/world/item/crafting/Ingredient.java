/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;

public final class Ingredient
implements StackedContents.IngredientInfo<Holder<Item>>,
Predicate<ItemStack> {
    public static final StreamCodec<RegistryFriendlyByteBuf, Ingredient> CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM).map(Ingredient::new, $$0 -> $$0.values);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> OPTIONAL_CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM).map($$0 -> $$0.size() == 0 ? Optional.empty() : Optional.of(new Ingredient((HolderSet<Item>)$$0)), $$02 -> $$02.map($$0 -> $$0.values).orElse(HolderSet.a(new Holder[0])));
    public static final Codec<HolderSet<Item>> NON_AIR_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.ITEM, Item.CODEC, false);
    public static final Codec<Ingredient> CODEC = ExtraCodecs.nonEmptyHolderSet(NON_AIR_HOLDER_SET_CODEC).xmap(Ingredient::new, $$0 -> $$0.values);
    private final HolderSet<Item> values;

    private Ingredient(HolderSet<Item> $$02) {
        $$02.unwrap().ifRight($$0 -> {
            if ($$0.isEmpty()) {
                throw new UnsupportedOperationException("Ingredients can't be empty");
            }
            if ($$0.contains(Items.AIR.builtInRegistryHolder())) {
                throw new UnsupportedOperationException("Ingredient can't contain air");
            }
        });
        this.values = $$02;
    }

    public static boolean testOptionalIngredient(Optional<Ingredient> $$0, ItemStack $$12) {
        return $$0.map($$1 -> $$1.test($$12)).orElseGet($$12::isEmpty);
    }

    @Deprecated
    public Stream<Holder<Item>> items() {
        return this.values.stream();
    }

    public boolean isEmpty() {
        return this.values.size() == 0;
    }

    @Override
    public boolean test(ItemStack $$0) {
        return $$0.is(this.values);
    }

    @Override
    public boolean acceptsItem(Holder<Item> $$0) {
        return this.values.contains($$0);
    }

    public boolean equals(Object $$0) {
        if ($$0 instanceof Ingredient) {
            Ingredient $$1 = (Ingredient)$$0;
            return Objects.equals(this.values, $$1.values);
        }
        return false;
    }

    public static Ingredient of(ItemLike $$0) {
        return new Ingredient(HolderSet.a($$0.asItem().builtInRegistryHolder()));
    }

    public static Ingredient a(ItemLike ... $$0) {
        return Ingredient.of(Arrays.stream($$0));
    }

    public static Ingredient of(Stream<? extends ItemLike> $$02) {
        return new Ingredient(HolderSet.direct($$02.map($$0 -> $$0.asItem().builtInRegistryHolder()).toList()));
    }

    public static Ingredient of(HolderSet<Item> $$0) {
        return new Ingredient($$0);
    }

    public SlotDisplay display() {
        return (SlotDisplay)this.values.unwrap().map(SlotDisplay.TagSlotDisplay::new, $$0 -> new SlotDisplay.Composite($$0.stream().map(Ingredient::displayForSingleItem).toList()));
    }

    public static SlotDisplay optionalIngredientToDisplay(Optional<Ingredient> $$0) {
        return $$0.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE);
    }

    private static SlotDisplay displayForSingleItem(Holder<Item> $$0) {
        SlotDisplay.ItemSlotDisplay $$1 = new SlotDisplay.ItemSlotDisplay($$0);
        ItemStack $$2 = $$0.value().getCraftingRemainder();
        if (!$$2.isEmpty()) {
            SlotDisplay.ItemStackSlotDisplay $$3 = new SlotDisplay.ItemStackSlotDisplay($$2);
            return new SlotDisplay.WithRemainder($$1, $$3);
        }
        return $$1;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((ItemStack)object);
    }

    @Override
    public /* synthetic */ boolean acceptsItem(Object object) {
        return this.acceptsItem((Holder)object);
    }
}

