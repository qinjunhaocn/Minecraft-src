/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public interface SmithingRecipe
extends Recipe<SmithingRecipeInput> {
    @Override
    default public RecipeType<SmithingRecipe> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public RecipeSerializer<? extends SmithingRecipe> getSerializer();

    @Override
    default public boolean matches(SmithingRecipeInput $$0, Level $$1) {
        return Ingredient.testOptionalIngredient(this.templateIngredient(), $$0.template()) && this.baseIngredient().test($$0.base()) && Ingredient.testOptionalIngredient(this.additionIngredient(), $$0.addition());
    }

    public Optional<Ingredient> templateIngredient();

    public Ingredient baseIngredient();

    public Optional<Ingredient> additionIngredient();

    @Override
    default public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.SMITHING;
    }
}

