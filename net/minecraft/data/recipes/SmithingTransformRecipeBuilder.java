/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.recipes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;

public class SmithingTransformRecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final Item result;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap();

    public SmithingTransformRecipeBuilder(Ingredient $$0, Ingredient $$1, Ingredient $$2, RecipeCategory $$3, Item $$4) {
        this.category = $$3;
        this.template = $$0;
        this.base = $$1;
        this.addition = $$2;
        this.result = $$4;
    }

    public static SmithingTransformRecipeBuilder smithing(Ingredient $$0, Ingredient $$1, Ingredient $$2, RecipeCategory $$3, Item $$4) {
        return new SmithingTransformRecipeBuilder($$0, $$1, $$2, $$3, $$4);
    }

    public SmithingTransformRecipeBuilder unlocks(String $$0, Criterion<?> $$1) {
        this.criteria.put($$0, $$1);
        return this;
    }

    public void save(RecipeOutput $$0, String $$1) {
        this.save($$0, ResourceKey.create(Registries.RECIPE, ResourceLocation.parse($$1)));
    }

    public void save(RecipeOutput $$0, ResourceKey<Recipe<?>> $$1) {
        this.ensureValid($$1);
        Advancement.Builder $$2 = $$0.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked($$1)).rewards(AdvancementRewards.Builder.recipe($$1)).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach($$2::addCriterion);
        SmithingTransformRecipe $$3 = new SmithingTransformRecipe(Optional.of(this.template), this.base, Optional.of(this.addition), new TransmuteResult(this.result));
        $$0.accept($$1, $$3, $$2.build($$1.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceKey<Recipe<?>> $$0) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + String.valueOf($$0.location()));
        }
    }
}

