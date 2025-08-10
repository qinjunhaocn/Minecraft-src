/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public interface RecipeBuilder {
    public static final ResourceLocation ROOT_RECIPE_ADVANCEMENT = ResourceLocation.withDefaultNamespace("recipes/root");

    public RecipeBuilder unlockedBy(String var1, Criterion<?> var2);

    public RecipeBuilder group(@Nullable String var1);

    public Item getResult();

    public void save(RecipeOutput var1, ResourceKey<Recipe<?>> var2);

    default public void save(RecipeOutput $$0) {
        this.save($$0, ResourceKey.create(Registries.RECIPE, RecipeBuilder.getDefaultRecipeId(this.getResult())));
    }

    default public void save(RecipeOutput $$0, String $$1) {
        ResourceLocation $$2 = RecipeBuilder.getDefaultRecipeId(this.getResult());
        ResourceLocation $$3 = ResourceLocation.parse($$1);
        if ($$3.equals($$2)) {
            throw new IllegalStateException("Recipe " + $$1 + " should remove its 'save' argument as it is equal to default one");
        }
        this.save($$0, ResourceKey.create(Registries.RECIPE, $$3));
    }

    public static ResourceLocation getDefaultRecipeId(ItemLike $$0) {
        return BuiltInRegistries.ITEM.getKey($$0.asItem());
    }

    public static CraftingBookCategory determineBookCategory(RecipeCategory $$0) {
        return switch ($$0) {
            case RecipeCategory.BUILDING_BLOCKS -> CraftingBookCategory.BUILDING;
            case RecipeCategory.TOOLS, RecipeCategory.COMBAT -> CraftingBookCategory.EQUIPMENT;
            case RecipeCategory.REDSTONE -> CraftingBookCategory.REDSTONE;
            default -> CraftingBookCategory.MISC;
        };
    }
}

