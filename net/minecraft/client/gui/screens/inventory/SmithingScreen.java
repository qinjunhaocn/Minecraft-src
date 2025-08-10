/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.equipment.Equippable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SmithingScreen
extends ItemCombinerScreen<SmithingMenu> {
    private static final ResourceLocation ERROR_SPRITE = ResourceLocation.withDefaultNamespace("container/smithing/error");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM = ResourceLocation.withDefaultNamespace("container/slot/smithing_template_armor_trim");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE = ResourceLocation.withDefaultNamespace("container/slot/smithing_template_netherite_upgrade");
    private static final Component MISSING_TEMPLATE_TOOLTIP = Component.translatable("container.upgrade.missing_template_tooltip");
    private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
    private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES = List.of((Object)EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, (Object)EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);
    private static final int TITLE_LABEL_X = 44;
    private static final int TITLE_LABEL_Y = 15;
    private static final int ERROR_ICON_WIDTH = 28;
    private static final int ERROR_ICON_HEIGHT = 21;
    private static final int ERROR_ICON_X = 65;
    private static final int ERROR_ICON_Y = 46;
    private static final int TOOLTIP_WIDTH = 115;
    private static final int ARMOR_STAND_Y_ROT = 210;
    private static final int ARMOR_STAND_X_ROT = 25;
    private static final Vector3f ARMOR_STAND_TRANSLATION = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232f, 0.0f, (float)Math.PI);
    private static final int ARMOR_STAND_SCALE = 25;
    private static final int ARMOR_STAND_LEFT = 121;
    private static final int ARMOR_STAND_TOP = 20;
    private static final int ARMOR_STAND_RIGHT = 161;
    private static final int ARMOR_STAND_BOTTOM = 80;
    private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(1);
    private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(2);
    @Nullable
    private ArmorStand armorStandPreview;

    public SmithingScreen(SmithingMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2, ResourceLocation.withDefaultNamespace("textures/gui/container/smithing.png"));
        this.titleLabelX = 44;
        this.titleLabelY = 15;
    }

    @Override
    protected void subInit() {
        this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0, 0.0, 0.0);
        this.armorStandPreview.setNoBasePlate(true);
        this.armorStandPreview.setShowArms(true);
        this.armorStandPreview.yBodyRot = 210.0f;
        this.armorStandPreview.setXRot(25.0f);
        this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
        this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
        this.updateArmorStandPreview(((SmithingMenu)this.menu).getSlot(3).getItem());
    }

    @Override
    public void containerTick() {
        super.containerTick();
        Optional<SmithingTemplateItem> $$0 = this.getTemplateItem();
        this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.baseIcon.tick($$0.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
        this.additionalIcon.tick($$0.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
    }

    private Optional<SmithingTemplateItem> getTemplateItem() {
        Item item;
        ItemStack $$0 = ((SmithingMenu)this.menu).getSlot(0).getItem();
        if (!$$0.isEmpty() && (item = $$0.getItem()) instanceof SmithingTemplateItem) {
            SmithingTemplateItem $$1 = (SmithingTemplateItem)item;
            return Optional.of($$1);
        }
        return Optional.empty();
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderOnboardingTooltips($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        super.renderBg($$0, $$1, $$2, $$3);
        this.templateIcon.render(this.menu, $$0, $$1, this.leftPos, this.topPos);
        this.baseIcon.render(this.menu, $$0, $$1, this.leftPos, this.topPos);
        this.additionalIcon.render(this.menu, $$0, $$1, this.leftPos, this.topPos);
        int $$4 = this.leftPos + 121;
        int $$5 = this.topPos + 20;
        int $$6 = this.leftPos + 161;
        int $$7 = this.topPos + 80;
        InventoryScreen.renderEntityInInventory($$0, $$4, $$5, $$6, $$7, 25.0f, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ANGLE, null, this.armorStandPreview);
    }

    @Override
    public void slotChanged(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
        if ($$1 == 3) {
            this.updateArmorStandPreview($$2);
        }
    }

    private void updateArmorStandPreview(ItemStack $$0) {
        if (this.armorStandPreview == null) {
            return;
        }
        for (EquipmentSlot $$1 : EquipmentSlot.VALUES) {
            this.armorStandPreview.setItemSlot($$1, ItemStack.EMPTY);
        }
        if (!$$0.isEmpty()) {
            Equippable $$2 = $$0.get(DataComponents.EQUIPPABLE);
            EquipmentSlot $$3 = $$2 != null ? $$2.slot() : EquipmentSlot.OFFHAND;
            this.armorStandPreview.setItemSlot($$3, $$0.copy());
        }
    }

    @Override
    protected void renderErrorIcon(GuiGraphics $$0, int $$1, int $$2) {
        if (this.hasRecipeError()) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, $$1 + 65, $$2 + 46, 28, 21);
        }
    }

    private void renderOnboardingTooltips(GuiGraphics $$0, int $$1, int $$2) {
        Optional<Component> $$32 = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(65, 46, 28, 21, $$1, $$2)) {
            $$32 = Optional.of(ERROR_TOOLTIP);
        }
        if (this.hoveredSlot != null) {
            ItemStack $$4 = ((SmithingMenu)this.menu).getSlot(0).getItem();
            ItemStack $$5 = this.hoveredSlot.getItem();
            if ($$4.isEmpty()) {
                if (this.hoveredSlot.index == 0) {
                    $$32 = Optional.of(MISSING_TEMPLATE_TOOLTIP);
                }
            } else {
                Item item = $$4.getItem();
                if (item instanceof SmithingTemplateItem) {
                    SmithingTemplateItem $$6 = (SmithingTemplateItem)item;
                    if ($$5.isEmpty()) {
                        if (this.hoveredSlot.index == 1) {
                            $$32 = Optional.of($$6.getBaseSlotDescription());
                        } else if (this.hoveredSlot.index == 2) {
                            $$32 = Optional.of($$6.getAdditionSlotDescription());
                        }
                    }
                }
            }
        }
        $$32.ifPresent($$3 -> $$0.setTooltipForNextFrame(this.font, this.font.split((FormattedText)$$3, 115), $$1, $$2));
    }

    private boolean hasRecipeError() {
        return ((SmithingMenu)this.menu).hasRecipeError();
    }
}

