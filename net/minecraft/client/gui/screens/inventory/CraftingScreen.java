/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;

public class CraftingScreen
extends AbstractRecipeBookScreen<CraftingMenu> {
    private static final ResourceLocation CRAFTING_TABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/crafting_table.png");

    public CraftingScreen(CraftingMenu $$0, Inventory $$1, Component $$2) {
        super($$0, new CraftingRecipeBookComponent($$0), $$1, $$2);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 29;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 5, this.height / 2 - 49);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = this.leftPos;
        int $$5 = (this.height - this.imageHeight) / 2;
        $$0.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
    }
}

