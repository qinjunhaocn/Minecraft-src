/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item.crafting;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeCache {
    private final Entry[] entries;
    private WeakReference<RecipeManager> cachedRecipeManager = new WeakReference<Object>(null);

    public RecipeCache(int $$0) {
        this.entries = new Entry[$$0];
    }

    public Optional<RecipeHolder<CraftingRecipe>> get(ServerLevel $$0, CraftingInput $$1) {
        if ($$1.isEmpty()) {
            return Optional.empty();
        }
        this.validateRecipeManager($$0);
        for (int $$2 = 0; $$2 < this.entries.length; ++$$2) {
            Entry $$3 = this.entries[$$2];
            if ($$3 == null || !$$3.matches($$1)) continue;
            this.moveEntryToFront($$2);
            return Optional.ofNullable($$3.value());
        }
        return this.compute($$1, $$0);
    }

    private void validateRecipeManager(ServerLevel $$0) {
        RecipeManager $$1 = $$0.recipeAccess();
        if ($$1 != this.cachedRecipeManager.get()) {
            this.cachedRecipeManager = new WeakReference<RecipeManager>($$1);
            Arrays.fill((Object[])this.entries, null);
        }
    }

    private Optional<RecipeHolder<CraftingRecipe>> compute(CraftingInput $$0, ServerLevel $$1) {
        Optional<RecipeHolder<CraftingRecipe>> $$2 = $$1.recipeAccess().getRecipeFor(RecipeType.CRAFTING, $$0, $$1);
        this.insert($$0, $$2.orElse(null));
        return $$2;
    }

    private void moveEntryToFront(int $$0) {
        if ($$0 > 0) {
            Entry $$1 = this.entries[$$0];
            System.arraycopy(this.entries, 0, this.entries, 1, $$0);
            this.entries[0] = $$1;
        }
    }

    private void insert(CraftingInput $$0, @Nullable RecipeHolder<CraftingRecipe> $$1) {
        NonNullList<ItemStack> $$2 = NonNullList.withSize($$0.size(), ItemStack.EMPTY);
        for (int $$3 = 0; $$3 < $$0.size(); ++$$3) {
            $$2.set($$3, $$0.getItem($$3).copyWithCount(1));
        }
        System.arraycopy(this.entries, 0, this.entries, 1, this.entries.length - 1);
        this.entries[0] = new Entry($$2, $$0.width(), $$0.height(), $$1);
    }

    record Entry(NonNullList<ItemStack> key, int width, int height, @Nullable RecipeHolder<CraftingRecipe> value) {
        public boolean matches(CraftingInput $$0) {
            if (this.width != $$0.width() || this.height != $$0.height()) {
                return false;
            }
            for (int $$1 = 0; $$1 < this.key.size(); ++$$1) {
                if (ItemStack.isSameItemSameComponents(this.key.get($$1), $$0.getItem($$1))) continue;
                return false;
            }
            return true;
        }

        @Nullable
        public RecipeHolder<CraftingRecipe> value() {
            return this.value;
        }
    }
}

