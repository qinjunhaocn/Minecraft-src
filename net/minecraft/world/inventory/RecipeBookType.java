/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

public final class RecipeBookType
extends Enum<RecipeBookType> {
    public static final /* enum */ RecipeBookType CRAFTING = new RecipeBookType();
    public static final /* enum */ RecipeBookType FURNACE = new RecipeBookType();
    public static final /* enum */ RecipeBookType BLAST_FURNACE = new RecipeBookType();
    public static final /* enum */ RecipeBookType SMOKER = new RecipeBookType();
    private static final /* synthetic */ RecipeBookType[] $VALUES;

    public static RecipeBookType[] values() {
        return (RecipeBookType[])$VALUES.clone();
    }

    public static RecipeBookType valueOf(String $$0) {
        return Enum.valueOf(RecipeBookType.class, $$0);
    }

    private static /* synthetic */ RecipeBookType[] a() {
        return new RecipeBookType[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
    }

    static {
        $VALUES = RecipeBookType.a();
    }
}

