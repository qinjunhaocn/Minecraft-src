/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class ContainerScreen
extends AbstractContainerScreen<ChestMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private final int containerRows;

    public ContainerScreen(ChestMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        int $$3 = 222;
        int $$4 = 114;
        this.containerRows = $$0.getRowCount();
        this.imageHeight = 114 + this.containerRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        $$0.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.containerRows * 18 + 17, 256, 256);
        $$0.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, $$4, $$5 + this.containerRows * 18 + 17, 0.0f, 126.0f, this.imageWidth, 96, 256, 256);
    }
}

