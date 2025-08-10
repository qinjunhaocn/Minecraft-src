/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;

public class HorseInventoryScreen
extends AbstractContainerScreen<HorseInventoryMenu> {
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
    private static final ResourceLocation CHEST_SLOTS_SPRITE = ResourceLocation.withDefaultNamespace("container/horse/chest_slots");
    private static final ResourceLocation HORSE_INVENTORY_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/horse.png");
    private final AbstractHorse horse;
    private final int inventoryColumns;
    private float xMouse;
    private float yMouse;

    public HorseInventoryScreen(HorseInventoryMenu $$0, Inventory $$1, AbstractHorse $$2, int $$3) {
        super($$0, $$1, $$2.getDisplayName());
        this.horse = $$2;
        this.inventoryColumns = $$3;
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        $$0.blit(RenderPipelines.GUI_TEXTURED, HORSE_INVENTORY_LOCATION, $$4, $$5, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        if (this.inventoryColumns > 0) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, CHEST_SLOTS_SPRITE, 90, 54, 0, 0, $$4 + 79, $$5 + 17, this.inventoryColumns * 18, 54);
        }
        if (this.horse.canUseSlot(EquipmentSlot.SADDLE) && this.horse.getType().is(EntityTypeTags.CAN_EQUIP_SADDLE)) {
            this.drawSlot($$0, $$4 + 7, $$5 + 35 - 18);
        }
        boolean $$6 = this.horse instanceof Llama;
        if (this.horse.canUseSlot(EquipmentSlot.BODY) && (this.horse.getType().is(EntityTypeTags.CAN_WEAR_HORSE_ARMOR) || $$6)) {
            this.drawSlot($$0, $$4 + 7, $$5 + 35);
        }
        InventoryScreen.renderEntityInInventoryFollowsMouse($$0, $$4 + 26, $$5 + 18, $$4 + 78, $$5 + 70, 17, 0.25f, this.xMouse, this.yMouse, this.horse);
    }

    private void drawSlot(GuiGraphics $$0, int $$1, int $$2) {
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, $$1, $$2, 18, 18);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.xMouse = $$1;
        this.yMouse = $$2;
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }
}

