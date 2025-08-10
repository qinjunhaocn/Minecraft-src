/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.FurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;

public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceMenu>
extends AbstractRecipeBookScreen<T> {
    private final ResourceLocation texture;
    private final ResourceLocation litProgressSprite;
    private final ResourceLocation burnProgressSprite;

    public AbstractFurnaceScreen(T $$0, Inventory $$1, Component $$2, Component $$3, ResourceLocation $$4, ResourceLocation $$5, ResourceLocation $$6, List<RecipeBookComponent.TabInfo> $$7) {
        super($$0, new FurnaceRecipeBookComponent((AbstractFurnaceMenu)$$0, $$3, $$7), $$1, $$2);
        this.texture = $$4;
        this.litProgressSprite = $$5;
        this.burnProgressSprite = $$6;
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        $$0.blit(RenderPipelines.GUI_TEXTURED, this.texture, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        if (((AbstractFurnaceMenu)this.menu).isLit()) {
            int $$6 = 14;
            int $$7 = Mth.ceil(((AbstractFurnaceMenu)this.menu).getLitProgress() * 13.0f) + 1;
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.litProgressSprite, 14, 14, 0, 14 - $$7, $$4 + 56, $$5 + 36 + 14 - $$7, 14, $$7);
        }
        int $$8 = 24;
        int $$9 = Mth.ceil(((AbstractFurnaceMenu)this.menu).getBurnProgress() * 24.0f);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.burnProgressSprite, 24, 16, 0, 0, $$4 + 79, $$5 + 34, $$9, 16);
    }
}

