/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameRules;

public interface RecipeCraftingHolder {
    public void setRecipeUsed(@Nullable RecipeHolder<?> var1);

    @Nullable
    public RecipeHolder<?> getRecipeUsed();

    default public void awardUsedRecipes(Player $$0, List<ItemStack> $$1) {
        RecipeHolder<?> $$2 = this.getRecipeUsed();
        if ($$2 != null) {
            $$0.triggerRecipeCrafted($$2, $$1);
            if (!$$2.value().isSpecial()) {
                $$0.awardRecipes(Collections.singleton($$2));
                this.setRecipeUsed(null);
            }
        }
    }

    default public boolean setRecipeUsed(ServerPlayer $$0, RecipeHolder<?> $$1) {
        if ($$1.value().isSpecial() || !$$0.level().getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) || $$0.getRecipeBook().contains($$1.id())) {
            this.setRecipeUsed($$1);
            return true;
        }
        return false;
    }
}

