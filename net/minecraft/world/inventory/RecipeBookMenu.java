/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeHolder;

public abstract class RecipeBookMenu
extends AbstractContainerMenu {
    public RecipeBookMenu(MenuType<?> $$0, int $$1) {
        super($$0, $$1);
    }

    public abstract PostPlaceAction handlePlacement(boolean var1, boolean var2, RecipeHolder<?> var3, ServerLevel var4, Inventory var5);

    public abstract void fillCraftSlotsStackedContents(StackedItemContents var1);

    public abstract RecipeBookType getRecipeBookType();

    public static final class PostPlaceAction
    extends Enum<PostPlaceAction> {
        public static final /* enum */ PostPlaceAction NOTHING = new PostPlaceAction();
        public static final /* enum */ PostPlaceAction PLACE_GHOST_RECIPE = new PostPlaceAction();
        private static final /* synthetic */ PostPlaceAction[] $VALUES;

        public static PostPlaceAction[] values() {
            return (PostPlaceAction[])$VALUES.clone();
        }

        public static PostPlaceAction valueOf(String $$0) {
            return Enum.valueOf(PostPlaceAction.class, $$0);
        }

        private static /* synthetic */ PostPlaceAction[] a() {
            return new PostPlaceAction[]{NOTHING, PLACE_GHOST_RECIPE};
        }

        static {
            $VALUES = PostPlaceAction.a();
        }
    }
}

