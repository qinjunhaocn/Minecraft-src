/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.world.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FireworkStarRecipe
extends CustomRecipe {
    private static final Map<Item, FireworkExplosion.Shape> SHAPE_BY_ITEM = Map.of((Object)Items.FIRE_CHARGE, (Object)FireworkExplosion.Shape.LARGE_BALL, (Object)Items.FEATHER, (Object)FireworkExplosion.Shape.BURST, (Object)Items.GOLD_NUGGET, (Object)FireworkExplosion.Shape.STAR, (Object)Items.SKELETON_SKULL, (Object)FireworkExplosion.Shape.CREEPER, (Object)Items.WITHER_SKELETON_SKULL, (Object)FireworkExplosion.Shape.CREEPER, (Object)Items.CREEPER_HEAD, (Object)FireworkExplosion.Shape.CREEPER, (Object)Items.PLAYER_HEAD, (Object)FireworkExplosion.Shape.CREEPER, (Object)Items.DRAGON_HEAD, (Object)FireworkExplosion.Shape.CREEPER, (Object)Items.ZOMBIE_HEAD, (Object)FireworkExplosion.Shape.CREEPER, (Object)Items.PIGLIN_HEAD, (Object)FireworkExplosion.Shape.CREEPER);
    private static final Ingredient TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
    private static final Ingredient TWINKLE_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);

    public FireworkStarRecipe(CraftingBookCategory $$0) {
        super($$0);
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        if ($$0.ingredientCount() < 2) {
            return false;
        }
        boolean $$2 = false;
        boolean $$3 = false;
        boolean $$4 = false;
        boolean $$5 = false;
        boolean $$6 = false;
        for (int $$7 = 0; $$7 < $$0.size(); ++$$7) {
            ItemStack $$8 = $$0.getItem($$7);
            if ($$8.isEmpty()) continue;
            if (SHAPE_BY_ITEM.containsKey($$8.getItem())) {
                if ($$4) {
                    return false;
                }
                $$4 = true;
                continue;
            }
            if (TWINKLE_INGREDIENT.test($$8)) {
                if ($$6) {
                    return false;
                }
                $$6 = true;
                continue;
            }
            if (TRAIL_INGREDIENT.test($$8)) {
                if ($$5) {
                    return false;
                }
                $$5 = true;
                continue;
            }
            if (GUNPOWDER_INGREDIENT.test($$8)) {
                if ($$2) {
                    return false;
                }
                $$2 = true;
                continue;
            }
            if ($$8.getItem() instanceof DyeItem) {
                $$3 = true;
                continue;
            }
            return false;
        }
        return $$2 && $$3;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        FireworkExplosion.Shape $$2 = FireworkExplosion.Shape.SMALL_BALL;
        boolean $$3 = false;
        boolean $$4 = false;
        IntArrayList $$5 = new IntArrayList();
        for (int $$6 = 0; $$6 < $$0.size(); ++$$6) {
            ItemStack $$7 = $$0.getItem($$6);
            if ($$7.isEmpty()) continue;
            FireworkExplosion.Shape $$8 = SHAPE_BY_ITEM.get($$7.getItem());
            if ($$8 != null) {
                $$2 = $$8;
                continue;
            }
            if (TWINKLE_INGREDIENT.test($$7)) {
                $$3 = true;
                continue;
            }
            if (TRAIL_INGREDIENT.test($$7)) {
                $$4 = true;
                continue;
            }
            Item item = $$7.getItem();
            if (!(item instanceof DyeItem)) continue;
            DyeItem $$9 = (DyeItem)item;
            $$5.add($$9.getDyeColor().getFireworkColor());
        }
        ItemStack $$10 = new ItemStack(Items.FIREWORK_STAR);
        $$10.set(DataComponents.FIREWORK_EXPLOSION, new FireworkExplosion($$2, (IntList)$$5, IntList.of(), $$4, $$3));
        return $$10;
    }

    @Override
    public RecipeSerializer<FireworkStarRecipe> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }
}

