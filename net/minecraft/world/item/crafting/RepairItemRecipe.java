/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.world.item.crafting;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RepairItemRecipe
extends CustomRecipe {
    public RepairItemRecipe(CraftingBookCategory $$0) {
        super($$0);
    }

    @Nullable
    private static Pair<ItemStack, ItemStack> getItemsToCombine(CraftingInput $$0) {
        if ($$0.ingredientCount() != 2) {
            return null;
        }
        ItemStack $$1 = null;
        for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
            ItemStack $$3 = $$0.getItem($$2);
            if ($$3.isEmpty()) continue;
            if ($$1 == null) {
                $$1 = $$3;
                continue;
            }
            return RepairItemRecipe.canCombine($$1, $$3) ? Pair.of((Object)$$1, (Object)$$3) : null;
        }
        return null;
    }

    private static boolean canCombine(ItemStack $$0, ItemStack $$1) {
        return $$1.is($$0.getItem()) && $$0.getCount() == 1 && $$1.getCount() == 1 && $$0.has(DataComponents.MAX_DAMAGE) && $$1.has(DataComponents.MAX_DAMAGE) && $$0.has(DataComponents.DAMAGE) && $$1.has(DataComponents.DAMAGE);
    }

    @Override
    public boolean matches(CraftingInput $$0, Level $$1) {
        return RepairItemRecipe.getItemsToCombine($$0) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput $$0, HolderLookup.Provider $$1) {
        Pair<ItemStack, ItemStack> $$2 = RepairItemRecipe.getItemsToCombine($$0);
        if ($$2 == null) {
            return ItemStack.EMPTY;
        }
        ItemStack $$3 = (ItemStack)$$2.getFirst();
        ItemStack $$4 = (ItemStack)$$2.getSecond();
        int $$5 = Math.max($$3.getMaxDamage(), $$4.getMaxDamage());
        int $$6 = $$3.getMaxDamage() - $$3.getDamageValue();
        int $$7 = $$4.getMaxDamage() - $$4.getDamageValue();
        int $$8 = $$6 + $$7 + $$5 * 5 / 100;
        ItemStack $$9 = new ItemStack($$3.getItem());
        $$9.set(DataComponents.MAX_DAMAGE, $$5);
        $$9.setDamageValue(Math.max($$5 - $$8, 0));
        ItemEnchantments $$10 = EnchantmentHelper.getEnchantmentsForCrafting($$3);
        ItemEnchantments $$11 = EnchantmentHelper.getEnchantmentsForCrafting($$4);
        EnchantmentHelper.updateEnchantments($$9, $$32 -> $$1.lookupOrThrow(Registries.ENCHANTMENT).listElements().filter($$0 -> $$0.is(EnchantmentTags.CURSE)).forEach($$3 -> {
            int $$4 = Math.max($$10.getLevel((Holder<Enchantment>)$$3), $$11.getLevel((Holder<Enchantment>)$$3));
            if ($$4 > 0) {
                $$32.upgrade((Holder<Enchantment>)$$3, $$4);
            }
        }));
        return $$9;
    }

    @Override
    public RecipeSerializer<RepairItemRecipe> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}

