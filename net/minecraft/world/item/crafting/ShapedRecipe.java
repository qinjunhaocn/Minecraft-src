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
package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class ShapedRecipe
implements CraftingRecipe {
    final ShapedRecipePattern pattern;
    final ItemStack result;
    final String group;
    final CraftingBookCategory category;
    final boolean showNotification;
    @Nullable
    private PlacementInfo placementInfo;

    public ShapedRecipe(String $$0, CraftingBookCategory $$1, ShapedRecipePattern $$2, ItemStack $$3, boolean $$4) {
        this.group = $$0;
        this.category = $$1;
        this.pattern = $$2;
        this.result = $$3;
        this.showNotification = $$4;
    }

    public ShapedRecipe(String $$0, CraftingBookCategory $$1, ShapedRecipePattern $$2, ItemStack $$3) {
        this($$0, $$1, $$2, $$3, true);
    }

    @Override
    public RecipeSerializer<? extends ShapedRecipe> getSerializer() {
        return RecipeSerializer.SHAPED_RECIPE;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @VisibleForTesting
    public List<Optional<Ingredient>> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(this.pattern.ingredients());
        }
        return this.placementInfo;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        return this.pattern.matches($$0);
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        return this.result.copy();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of((Object)new ShapedCraftingRecipeDisplay(this.pattern.width(), this.pattern.height(), this.pattern.ingredients().stream().map($$0 -> $$0.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(), new SlotDisplay.ItemStackSlotDisplay(this.result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    public static class Serializer
    implements RecipeSerializer<ShapedRecipe> {
        public static final MapCodec<ShapedRecipe> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.STRING.optionalFieldOf("group", (Object)"").forGetter($$0 -> $$0.group), (App)CraftingBookCategory.CODEC.fieldOf("category").orElse((Object)CraftingBookCategory.MISC).forGetter($$0 -> $$0.category), (App)ShapedRecipePattern.MAP_CODEC.forGetter($$0 -> $$0.pattern), (App)ItemStack.STRICT_CODEC.fieldOf("result").forGetter($$0 -> $$0.result), (App)Codec.BOOL.optionalFieldOf("show_notification", (Object)true).forGetter($$0 -> $$0.showNotification)).apply((Applicative)$$02, ShapedRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ShapedRecipe fromNetwork(RegistryFriendlyByteBuf $$0) {
            String $$1 = $$0.readUtf();
            CraftingBookCategory $$2 = $$0.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern $$3 = (ShapedRecipePattern)ShapedRecipePattern.STREAM_CODEC.decode($$0);
            ItemStack $$4 = (ItemStack)ItemStack.STREAM_CODEC.decode($$0);
            boolean $$5 = $$0.readBoolean();
            return new ShapedRecipe($$1, $$2, $$3, $$4, $$5);
        }

        private static void toNetwork(RegistryFriendlyByteBuf $$0, ShapedRecipe $$1) {
            $$0.writeUtf($$1.group);
            $$0.writeEnum($$1.category);
            ShapedRecipePattern.STREAM_CODEC.encode($$0, $$1.pattern);
            ItemStack.STREAM_CODEC.encode($$0, $$1.result);
            $$0.writeBoolean($$1.showNotification);
        }
    }
}

