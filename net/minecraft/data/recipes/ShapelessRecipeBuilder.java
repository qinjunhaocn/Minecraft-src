/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.recipes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

public class ShapelessRecipeBuilder
implements RecipeBuilder {
    private final HolderGetter<Item> items;
    private final RecipeCategory category;
    private final ItemStack result;
    private final List<Ingredient> ingredients = new ArrayList<Ingredient>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
    @Nullable
    private String group;

    private ShapelessRecipeBuilder(HolderGetter<Item> $$0, RecipeCategory $$1, ItemStack $$2) {
        this.items = $$0;
        this.category = $$1;
        this.result = $$2;
    }

    public static ShapelessRecipeBuilder shapeless(HolderGetter<Item> $$0, RecipeCategory $$1, ItemStack $$2) {
        return new ShapelessRecipeBuilder($$0, $$1, $$2);
    }

    public static ShapelessRecipeBuilder shapeless(HolderGetter<Item> $$0, RecipeCategory $$1, ItemLike $$2) {
        return ShapelessRecipeBuilder.shapeless($$0, $$1, $$2, 1);
    }

    public static ShapelessRecipeBuilder shapeless(HolderGetter<Item> $$0, RecipeCategory $$1, ItemLike $$2, int $$3) {
        return new ShapelessRecipeBuilder($$0, $$1, $$2.asItem().getDefaultInstance().copyWithCount($$3));
    }

    public ShapelessRecipeBuilder requires(TagKey<Item> $$0) {
        return this.requires(Ingredient.of(this.items.getOrThrow($$0)));
    }

    public ShapelessRecipeBuilder requires(ItemLike $$0) {
        return this.requires($$0, 1);
    }

    public ShapelessRecipeBuilder requires(ItemLike $$0, int $$1) {
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            this.requires(Ingredient.of($$0));
        }
        return this;
    }

    public ShapelessRecipeBuilder requires(Ingredient $$0) {
        return this.requires($$0, 1);
    }

    public ShapelessRecipeBuilder requires(Ingredient $$0, int $$1) {
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            this.ingredients.add($$0);
        }
        return this;
    }

    @Override
    public ShapelessRecipeBuilder unlockedBy(String $$0, Criterion<?> $$1) {
        this.criteria.put($$0, $$1);
        return this;
    }

    @Override
    public ShapelessRecipeBuilder group(@Nullable String $$0) {
        this.group = $$0;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput $$0, ResourceKey<Recipe<?>> $$1) {
        this.ensureValid($$1);
        Advancement.Builder $$2 = $$0.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked($$1)).rewards(AdvancementRewards.Builder.recipe($$1)).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach($$2::addCriterion);
        ShapelessRecipe $$3 = new ShapelessRecipe((String)Objects.requireNonNullElse((Object)this.group, (Object)""), RecipeBuilder.determineBookCategory(this.category), this.result, this.ingredients);
        $$0.accept($$1, $$3, $$2.build($$1.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceKey<Recipe<?>> $$0) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + String.valueOf($$0.location()));
        }
    }

    @Override
    public /* synthetic */ RecipeBuilder group(@Nullable String string) {
        return this.group(string);
    }

    public /* synthetic */ RecipeBuilder unlockedBy(String string, Criterion criterion) {
        return this.unlockedBy(string, criterion);
    }
}

