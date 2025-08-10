/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.item.crafting;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.TransmuteResult;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;

public class SmithingTransformRecipe
implements SmithingRecipe {
    final Optional<Ingredient> template;
    final Ingredient base;
    final Optional<Ingredient> addition;
    final TransmuteResult result;
    @Nullable
    private PlacementInfo placementInfo;

    public SmithingTransformRecipe(Optional<Ingredient> $$0, Ingredient $$1, Optional<Ingredient> $$2, TransmuteResult $$3) {
        this.template = $$0;
        this.base = $$1;
        this.addition = $$2;
        this.result = $$3;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput $$0, HolderLookup.Provider $$1) {
        return this.result.apply($$0.base());
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return this.template;
    }

    @Override
    public Ingredient baseIngredient() {
        return this.base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return this.addition;
    }

    @Override
    public RecipeSerializer<SmithingTransformRecipe> getSerializer() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.template, Optional.of(this.base), this.addition));
        }
        return this.placementInfo;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of((Object)new SmithingRecipeDisplay(Ingredient.optionalIngredientToDisplay(this.template), this.base.display(), Ingredient.optionalIngredientToDisplay(this.addition), this.result.display(), new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
    }

    public static class Serializer
    implements RecipeSerializer<SmithingTransformRecipe> {
        private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Ingredient.CODEC.optionalFieldOf("template").forGetter($$0 -> $$0.template), (App)Ingredient.CODEC.fieldOf("base").forGetter($$0 -> $$0.base), (App)Ingredient.CODEC.optionalFieldOf("addition").forGetter($$0 -> $$0.addition), (App)TransmuteResult.CODEC.fieldOf("result").forGetter($$0 -> $$0.result)).apply((Applicative)$$02, SmithingTransformRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> STREAM_CODEC = StreamCodec.composite(Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, $$0 -> $$0.template, Ingredient.CONTENTS_STREAM_CODEC, $$0 -> $$0.base, Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, $$0 -> $$0.addition, TransmuteResult.STREAM_CODEC, $$0 -> $$0.result, SmithingTransformRecipe::new);

        @Override
        public MapCodec<SmithingTransformRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

