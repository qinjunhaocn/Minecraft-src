/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe
extends CustomRecipe {
    private static final Ingredient PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
    private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

    public FireworkRocketRecipe(CraftingBookCategory $$0) {
        super($$0);
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        if ($$0.ingredientCount() < 2) {
            return false;
        }
        boolean $$2 = false;
        int $$3 = 0;
        for (int $$4 = 0; $$4 < $$0.size(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if (PAPER_INGREDIENT.test($$5)) {
                if ($$2) {
                    return false;
                }
                $$2 = true;
                continue;
            }
            if (!(GUNPOWDER_INGREDIENT.test($$5) ? ++$$3 > 3 : !STAR_INGREDIENT.test($$5))) continue;
            return false;
        }
        return $$2 && $$3 >= 1;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        ArrayList<FireworkExplosion> $$2 = new ArrayList<FireworkExplosion>();
        int $$3 = 0;
        for (int $$4 = 0; $$4 < $$0.size(); ++$$4) {
            FireworkExplosion $$6;
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if (GUNPOWDER_INGREDIENT.test($$5)) {
                ++$$3;
                continue;
            }
            if (!STAR_INGREDIENT.test($$5) || ($$6 = $$5.get(DataComponents.FIREWORK_EXPLOSION)) == null) continue;
            $$2.add($$6);
        }
        ItemStack $$7 = new ItemStack(Items.FIREWORK_ROCKET, 3);
        $$7.set(DataComponents.FIREWORKS, new Fireworks($$3, $$2));
        return $$7;
    }

    @Override
    public RecipeSerializer<FireworkRocketRecipe> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
}

