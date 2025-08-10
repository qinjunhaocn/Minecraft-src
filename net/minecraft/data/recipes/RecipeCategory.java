/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.recipes;

public final class RecipeCategory
extends Enum<RecipeCategory> {
    public static final /* enum */ RecipeCategory BUILDING_BLOCKS = new RecipeCategory("building_blocks");
    public static final /* enum */ RecipeCategory DECORATIONS = new RecipeCategory("decorations");
    public static final /* enum */ RecipeCategory REDSTONE = new RecipeCategory("redstone");
    public static final /* enum */ RecipeCategory TRANSPORTATION = new RecipeCategory("transportation");
    public static final /* enum */ RecipeCategory TOOLS = new RecipeCategory("tools");
    public static final /* enum */ RecipeCategory COMBAT = new RecipeCategory("combat");
    public static final /* enum */ RecipeCategory FOOD = new RecipeCategory("food");
    public static final /* enum */ RecipeCategory BREWING = new RecipeCategory("brewing");
    public static final /* enum */ RecipeCategory MISC = new RecipeCategory("misc");
    private final String recipeFolderName;
    private static final /* synthetic */ RecipeCategory[] $VALUES;

    public static RecipeCategory[] values() {
        return (RecipeCategory[])$VALUES.clone();
    }

    public static RecipeCategory valueOf(String $$0) {
        return Enum.valueOf(RecipeCategory.class, $$0);
    }

    private RecipeCategory(String $$0) {
        this.recipeFolderName = $$0;
    }

    public String getFolderName() {
        return this.recipeFolderName;
    }

    private static /* synthetic */ RecipeCategory[] b() {
        return new RecipeCategory[]{BUILDING_BLOCKS, DECORATIONS, REDSTONE, TRANSPORTATION, TOOLS, COMBAT, FOOD, BREWING, MISC};
    }

    static {
        $VALUES = RecipeCategory.b();
    }
}

