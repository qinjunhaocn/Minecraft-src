/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AnvilScreen
extends ItemCombinerScreen<AnvilMenu> {
    private static final ResourceLocation TEXT_FIELD_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field");
    private static final ResourceLocation TEXT_FIELD_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/text_field_disabled");
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/anvil/error");
    private static final ResourceLocation ANVIL_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/anvil.png");
    private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    private EditBox name;
    private final Player player;

    public AnvilScreen(AnvilMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2, ANVIL_LOCATION);
        this.player = $$1.player;
        this.titleLabelX = 60;
    }

    @Override
    protected void subInit() {
        int $$0 = (this.width - this.imageWidth) / 2;
        int $$1 = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, $$0 + 62, $$1 + 24, 103, 12, Component.translatable("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(50);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue("");
        this.addRenderableWidget(this.name);
        this.name.setEditable(((AnvilMenu)this.menu).getSlot(0).hasItem());
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.name);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.name.getValue();
        this.init($$0, $$1, $$2);
        this.name.setValue($$3);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.player.closeContainer();
        }
        if (this.name.keyPressed($$0, $$1, $$2) || this.name.canConsumeInput()) {
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private void onNameChanged(String $$0) {
        Slot $$1 = ((AnvilMenu)this.menu).getSlot(0);
        if (!$$1.hasItem()) {
            return;
        }
        String $$2 = $$0;
        if (!$$1.getItem().has(DataComponents.CUSTOM_NAME) && $$2.equals($$1.getItem().getHoverName().getString())) {
            $$2 = "";
        }
        if (((AnvilMenu)this.menu).setItemName($$2)) {
            this.minecraft.player.connection.send(new ServerboundRenameItemPacket($$2));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics $$0, int $$1, int $$2) {
        super.renderLabels($$0, $$1, $$2);
        int $$3 = ((AnvilMenu)this.menu).getCost();
        if ($$3 > 0) {
            MutableComponent $$7;
            int $$4 = -8323296;
            if ($$3 >= 40 && !this.minecraft.player.hasInfiniteMaterials()) {
                Component $$5 = TOO_EXPENSIVE_TEXT;
                $$4 = -40864;
            } else if (!((AnvilMenu)this.menu).getSlot(2).hasItem()) {
                Object $$6 = null;
            } else {
                $$7 = Component.a("container.repair.cost", $$3);
                if (!((AnvilMenu)this.menu).getSlot(2).mayPickup(this.player)) {
                    $$4 = -40864;
                }
            }
            if ($$7 != null) {
                int $$8 = this.imageWidth - 8 - this.font.width($$7) - 2;
                int $$9 = 69;
                $$0.fill($$8 - 2, 67, this.imageWidth - 8, 79, 0x4F000000);
                $$0.drawString(this.font, $$7, $$8, 69, $$4);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        super.renderBg($$0, $$1, $$2, $$3);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ((AnvilMenu)this.menu).getSlot(0).hasItem() ? TEXT_FIELD_SPRITE : TEXT_FIELD_DISABLED_SPRITE, this.leftPos + 59, this.topPos + 20, 110, 16);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics $$0, int $$1, int $$2) {
        if ((((AnvilMenu)this.menu).getSlot(0).hasItem() || ((AnvilMenu)this.menu).getSlot(1).hasItem()) && !((AnvilMenu)this.menu).getSlot(((AnvilMenu)this.menu).getResultSlot()).hasItem()) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, $$1 + 99, $$2 + 45, 28, 21);
        }
    }

    @Override
    public void slotChanged(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
        if ($$1 == 0) {
            this.name.setValue($$2.isEmpty() ? "" : $$2.getHoverName().getString());
            this.name.setEditable(!$$2.isEmpty());
            this.setFocused(this.name);
        }
    }
}

