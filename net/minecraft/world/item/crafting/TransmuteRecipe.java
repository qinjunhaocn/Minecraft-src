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

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.TransmuteResult;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class TransmuteRecipe
implements CraftingRecipe {
    final String group;
    final CraftingBookCategory category;
    final Ingredient input;
    final Ingredient material;
    final TransmuteResult result;
    @Nullable
    private PlacementInfo placementInfo;

    public TransmuteRecipe(String $$0, CraftingBookCategory $$1, Ingredient $$2, Ingredient $$3, TransmuteResult $$4) {
        this.group = $$0;
        this.category = $$1;
        this.input = $$2;
        this.material = $$3;
        this.result = $$4;
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        if ($$0.ingredientCount() != 2) {
            return false;
        }
        boolean $$2 = false;
        boolean $$3 = false;
        for (int $$4 = 0; $$4 < $$0.size(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if (!$$2 && this.input.test($$5)) {
                if (this.result.isResultUnchanged($$5)) {
                    return false;
                }
                $$2 = true;
                continue;
            }
            if (!$$3 && this.material.test($$5)) {
                $$3 = true;
                continue;
            }
            return false;
        }
        return $$2 && $$3;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
            ItemStack $$3 = $$0.getItem($$2);
            if ($$3.isEmpty() || !this.input.test($$3)) continue;
            return this.result.apply($$3);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of((Object)new ShapelessCraftingRecipeDisplay(List.of((Object)this.input.display(), (Object)this.material.display()), this.result.display(), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    @Override
    public RecipeSerializer<TransmuteRecipe> getSerializer() {
        return RecipeSerializer.TRANSMUTE;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(List.of((Object)this.input, (Object)this.material));
        }
        return this.placementInfo;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    public static class Serializer
    implements RecipeSerializer<TransmuteRecipe> {
        private static final MapCodec<TransmuteRecipe> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Codec.STRING.optionalFieldOf("group", (Object)"").forGetter($$0 -> $$0.group), (App)CraftingBookCategory.CODEC.fieldOf("category").orElse((Object)CraftingBookCategory.MISC).forGetter($$0 -> $$0.category), (App)Ingredient.CODEC.fieldOf("input").forGetter($$0 -> $$0.input), (App)Ingredient.CODEC.fieldOf("material").forGetter($$0 -> $$0.material), (App)TransmuteResult.CODEC.fieldOf("result").forGetter($$0 -> $$0.result)).apply((Applicative)$$02, TransmuteRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, $$0 -> $$0.group, CraftingBookCategory.STREAM_CODEC, $$0 -> $$0.category, Ingredient.CONTENTS_STREAM_CODEC, $$0 -> $$0.input, Ingredient.CONTENTS_STREAM_CODEC, $$0 -> $$0.material, TransmuteResult.STREAM_CODEC, $$0 -> $$0.result, TransmuteRecipe::new);

        @Override
        public MapCodec<TransmuteRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

