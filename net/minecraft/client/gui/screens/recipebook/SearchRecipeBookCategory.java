/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public final class SearchRecipeBookCategory
extends Enum<SearchRecipeBookCategory>
implements ExtendedRecipeBookCategory {
    public static final /* enum */ SearchRecipeBookCategory CRAFTING = new SearchRecipeBookCategory(RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE);
    public static final /* enum */ SearchRecipeBookCategory FURNACE = new SearchRecipeBookCategory(RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC);
    public static final /* enum */ SearchRecipeBookCategory BLAST_FURNACE = new SearchRecipeBookCategory(RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC);
    public static final /* enum */ SearchRecipeBookCategory SMOKER = new SearchRecipeBookCategory(RecipeBookCategories.SMOKER_FOOD);
    private final List<RecipeBookCategory> includedCategories;
    private static final /* synthetic */ SearchRecipeBookCategory[] $VALUES;

    public static SearchRecipeBookCategory[] values() {
        return (SearchRecipeBookCategory[])$VALUES.clone();
    }

    public static SearchRecipeBookCategory valueOf(String $$0) {
        return Enum.valueOf(SearchRecipeBookCategory.class, $$0);
    }

    private SearchRecipeBookCategory(RecipeBookCategory ... $$0) {
        this.includedCategories = List.of((Object[])$$0);
    }

    public List<RecipeBookCategory> includedCategories() {
        return this.includedCategories;
    }

    private static /* synthetic */ SearchRecipeBookCategory[] b() {
        return new SearchRecipeBookCategory[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
    }

    static {
        $VALUES = SearchRecipeBookCategory.b();
    }
}

