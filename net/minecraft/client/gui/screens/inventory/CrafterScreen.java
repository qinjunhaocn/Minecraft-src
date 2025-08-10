/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CrafterSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CrafterScreen
extends AbstractContainerScreen<CrafterMenu> {
    private static final ResourceLocation DISABLED_SLOT_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/disabled_slot");
    private static final ResourceLocation POWERED_REDSTONE_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/powered_redstone");
    private static final ResourceLocation UNPOWERED_REDSTONE_LOCATION_SPRITE = ResourceLocation.withDefaultNamespace("container/crafter/unpowered_redstone");
    private static final ResourceLocation CONTAINER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/crafter.png");
    private static final Component DISABLED_SLOT_TOOLTIP = Component.translatable("gui.togglable_slot");
    private final Player player;

    public CrafterScreen(CrafterMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        this.player = $$1.player;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void slotClicked(Slot $$0, int $$1, int $$2, ClickType $$3) {
        if ($$0 instanceof CrafterSlot && !$$0.hasItem() && !this.player.isSpectator()) {
            switch ($$3) {
                case PICKUP: {
                    if (((CrafterMenu)this.menu).isSlotDisabled($$1)) {
                        this.enableSlot($$1);
                        break;
                    }
                    if (!((CrafterMenu)this.menu).getCarried().isEmpty()) break;
                    this.disableSlot($$1);
                    break;
                }
                case SWAP: {
                    ItemStack $$4 = this.player.getInventory().getItem($$2);
                    if (!((CrafterMenu)this.menu).isSlotDisabled($$1) || $$4.isEmpty()) break;
                    this.enableSlot($$1);
                }
            }
        }
        super.slotClicked($$0, $$1, $$2, $$3);
    }

    private void enableSlot(int $$0) {
        this.updateSlotState($$0, true);
    }

    private void disableSlot(int $$0) {
        this.updateSlotState($$0, false);
    }

    private void updateSlotState(int $$0, boolean $$1) {
        ((CrafterMenu)this.menu).setSlotState($$0, $$1);
        super.handleSlotStateChanged($$0, ((CrafterMenu)this.menu).containerId, $$1);
        float $$2 = $$1 ? 1.0f : 0.75f;
        this.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4f, $$2);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void renderSlot(GuiGraphics $$0, Slot $$1) {
        if ($$1 instanceof CrafterSlot) {
            CrafterSlot $$2 = (CrafterSlot)$$1;
            if (((CrafterMenu)this.menu).isSlotDisabled($$1.index)) {
                this.renderDisabledSlot($$0, $$2);
                return;
            }
        }
        super.renderSlot($$0, $$1);
    }

    private void renderDisabledSlot(GuiGraphics $$0, CrafterSlot $$1) {
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, DISABLED_SLOT_LOCATION_SPRITE, $$1.x - 1, $$1.y - 1, 18, 18);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderRedstone($$0);
        this.renderTooltip($$0, $$1, $$2);
        if (this.hoveredSlot instanceof CrafterSlot && !((CrafterMenu)this.menu).isSlotDisabled(this.hoveredSlot.index) && ((CrafterMenu)this.menu).getCarried().isEmpty() && !this.hoveredSlot.hasItem() && !this.player.isSpectator()) {
            $$0.setTooltipForNextFrame(this.font, DISABLED_SLOT_TOOLTIP, $$1, $$2);
        }
    }

    private void renderRedstone(GuiGraphics $$0) {
        ResourceLocation $$4;
        int $$1 = this.width / 2 + 9;
        int $$2 = this.height / 2 - 48;
        if (((CrafterMenu)this.menu).isPowered()) {
            ResourceLocation $$3 = POWERED_REDSTONE_LOCATION_SPRITE;
        } else {
            $$4 = UNPOWERED_REDSTONE_LOCATION_SPRITE;
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$4, $$1, $$2, 16, 16);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        $$0.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
    }
}

