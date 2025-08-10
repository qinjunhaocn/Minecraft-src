/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyTableScreen
extends AbstractContainerScreen<CartographyTableMenu> {
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/error");
    private static final ResourceLocation SCALED_MAP_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/scaled_map");
    private static final ResourceLocation DUPLICATED_MAP_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/duplicated_map");
    private static final ResourceLocation MAP_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/map");
    private static final ResourceLocation LOCKED_SPRITE = ResourceLocation.withDefaultNamespace("container/cartography_table/locked");
    private static final ResourceLocation BG_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/cartography_table.png");
    private final MapRenderState mapRenderState = new MapRenderState();

    public CartographyTableScreen(CartographyTableMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        this.titleLabelY -= 2;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        MapItemSavedData $$14;
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        $$0.blit(RenderPipelines.GUI_TEXTURED, BG_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        ItemStack $$6 = ((CartographyTableMenu)this.menu).getSlot(1).getItem();
        boolean $$7 = $$6.is(Items.MAP);
        boolean $$8 = $$6.is(Items.PAPER);
        boolean $$9 = $$6.is(Items.GLASS_PANE);
        ItemStack $$10 = ((CartographyTableMenu)this.menu).getSlot(0).getItem();
        MapId $$11 = $$10.get(DataComponents.MAP_ID);
        boolean $$12 = false;
        if ($$11 != null) {
            MapItemSavedData $$13 = MapItem.getSavedData($$11, (Level)this.minecraft.level);
            if ($$13 != null) {
                if ($$13.locked) {
                    $$12 = true;
                    if ($$8 || $$9) {
                        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, $$4 + 35, $$5 + 31, 28, 21);
                    }
                }
                if ($$8 && $$13.scale >= 4) {
                    $$12 = true;
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, $$4 + 35, $$5 + 31, 28, 21);
                }
            }
        } else {
            $$14 = null;
        }
        this.renderResultingMap($$0, $$11, $$14, $$7, $$8, $$9, $$12);
    }

    private void renderResultingMap(GuiGraphics $$0, @Nullable MapId $$1, @Nullable MapItemSavedData $$2, boolean $$3, boolean $$4, boolean $$5, boolean $$6) {
        int $$7 = this.leftPos;
        int $$8 = this.topPos;
        if ($$4 && !$$6) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SCALED_MAP_SPRITE, $$7 + 67, $$8 + 13, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 85, $$8 + 31, 0.226f);
        } else if ($$3) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, DUPLICATED_MAP_SPRITE, $$7 + 67 + 16, $$8 + 13, 50, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 86, $$8 + 16, 0.34f);
            $$0.nextStratum();
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, DUPLICATED_MAP_SPRITE, $$7 + 67, $$8 + 13 + 16, 50, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 70, $$8 + 32, 0.34f);
        } else if ($$5) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MAP_SPRITE, $$7 + 67, $$8 + 13, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 71, $$8 + 17, 0.45f);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, LOCKED_SPRITE, $$7 + 118, $$8 + 60, 10, 14);
        } else {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MAP_SPRITE, $$7 + 67, $$8 + 13, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 71, $$8 + 17, 0.45f);
        }
    }

    private void renderMap(GuiGraphics $$0, @Nullable MapId $$1, @Nullable MapItemSavedData $$2, int $$3, int $$4, float $$5) {
        if ($$1 != null && $$2 != null) {
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)$$3, (float)$$4);
            $$0.pose().scale($$5, $$5);
            this.minecraft.getMapRenderer().extractRenderState($$1, $$2, this.mapRenderState);
            $$0.submitMapRenderState(this.mapRenderState);
            $$0.pose().popMatrix();
        }
    }
}

