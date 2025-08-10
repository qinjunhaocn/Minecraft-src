/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.joml.Vector2i
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu>
extends Screen
implements MenuAccess<T> {
    public static final ResourceLocation INVENTORY_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png");
    private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_back");
    private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_front");
    protected static final int BACKGROUND_TEXTURE_WIDTH = 256;
    protected static final int BACKGROUND_TEXTURE_HEIGHT = 256;
    private static final float SNAPBACK_SPEED = 100.0f;
    private static final int QUICKDROP_DELAY = 500;
    protected int imageWidth = 176;
    protected int imageHeight = 166;
    protected int titleLabelX;
    protected int titleLabelY;
    protected int inventoryLabelX;
    protected int inventoryLabelY;
    private final List<ItemSlotMouseAction> itemSlotMouseActions;
    protected final T menu;
    protected final Component playerInventoryTitle;
    @Nullable
    protected Slot hoveredSlot;
    @Nullable
    private Slot clickedSlot;
    @Nullable
    private Slot quickdropSlot;
    @Nullable
    private Slot lastClickSlot;
    @Nullable
    private SnapbackData snapbackData;
    protected int leftPos;
    protected int topPos;
    private boolean isSplittingStack;
    private ItemStack draggingItem = ItemStack.EMPTY;
    private long quickdropTime;
    protected final Set<Slot> quickCraftSlots = Sets.newHashSet();
    protected boolean isQuickCrafting;
    private int quickCraftingType;
    private int quickCraftingButton;
    private boolean skipNextRelease;
    private int quickCraftingRemainder;
    private long lastClickTime;
    private int lastClickButton;
    private boolean doubleclick;
    private ItemStack lastQuickMoved = ItemStack.EMPTY;

    public AbstractContainerScreen(T $$0, Inventory $$1, Component $$2) {
        super($$2);
        this.menu = $$0;
        this.playerInventoryTitle = $$1.getDisplayName();
        this.skipNextRelease = true;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
        this.itemSlotMouseActions = new ArrayList<ItemSlotMouseAction>();
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.itemSlotMouseActions.clear();
        this.addItemSlotMouseAction(new BundleMouseActions(this.minecraft));
    }

    protected void addItemSlotMouseAction(ItemSlotMouseAction $$0) {
        this.itemSlotMouseActions.add($$0);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderContents($$0, $$1, $$2, $$3);
        this.renderCarriedItem($$0, $$1, $$2);
        this.renderSnapbackItem($$0);
    }

    public void renderContents(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        super.render($$0, $$1, $$2, $$3);
        $$0.pose().pushMatrix();
        $$0.pose().translate((float)$$4, (float)$$5);
        this.renderLabels($$0, $$1, $$2);
        Slot $$6 = this.hoveredSlot;
        this.hoveredSlot = this.getHoveredSlot($$1, $$2);
        this.renderSlotHighlightBack($$0);
        this.renderSlots($$0);
        this.renderSlotHighlightFront($$0);
        if ($$6 != null && $$6 != this.hoveredSlot) {
            this.onStopHovering($$6);
        }
        $$0.pose().popMatrix();
    }

    public void renderCarriedItem(GuiGraphics $$0, int $$1, int $$2) {
        ItemStack $$3;
        ItemStack itemStack = $$3 = this.draggingItem.isEmpty() ? ((AbstractContainerMenu)this.menu).getCarried() : this.draggingItem;
        if (!$$3.isEmpty()) {
            int $$4 = 8;
            int $$5 = this.draggingItem.isEmpty() ? 8 : 16;
            String $$6 = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                $$3 = $$3.copyWithCount(Mth.ceil((float)$$3.getCount() / 2.0f));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1 && ($$3 = $$3.copyWithCount(this.quickCraftingRemainder)).isEmpty()) {
                $$6 = String.valueOf(ChatFormatting.YELLOW) + "0";
            }
            $$0.nextStratum();
            this.renderFloatingItem($$0, $$3, $$1 - 8, $$2 - $$5, $$6);
        }
    }

    public void renderSnapbackItem(GuiGraphics $$0) {
        if (this.snapbackData != null) {
            float $$1 = Mth.clamp((float)(Util.getMillis() - this.snapbackData.time) / 100.0f, 0.0f, 1.0f);
            int $$2 = this.snapbackData.end.x - this.snapbackData.start.x;
            int $$3 = this.snapbackData.end.y - this.snapbackData.start.y;
            int $$4 = this.snapbackData.start.x + (int)((float)$$2 * $$1);
            int $$5 = this.snapbackData.start.y + (int)((float)$$3 * $$1);
            $$0.nextStratum();
            this.renderFloatingItem($$0, this.snapbackData.item, $$4, $$5, null);
            if ($$1 >= 1.0f) {
                this.snapbackData = null;
            }
        }
    }

    protected void renderSlots(GuiGraphics $$0) {
        for (Slot $$1 : ((AbstractContainerMenu)this.menu).slots) {
            if (!$$1.isActive()) continue;
            this.renderSlot($$0, $$1);
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.renderTransparentBackground($$0);
        this.renderBg($$0, $$3, $$1, $$2);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            for (ItemSlotMouseAction $$4 : this.itemSlotMouseActions) {
                if (!$$4.matches(this.hoveredSlot) || !$$4.onMouseScrolled($$2, $$3, this.hoveredSlot.index, this.hoveredSlot.getItem())) continue;
                return true;
            }
        }
        return false;
    }

    private void renderSlotHighlightBack(GuiGraphics $$0) {
        if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
        }
    }

    private void renderSlotHighlightFront(GuiGraphics $$0) {
        if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
        }
    }

    protected void renderTooltip(GuiGraphics $$0, int $$1, int $$2) {
        if (this.hoveredSlot == null || !this.hoveredSlot.hasItem()) {
            return;
        }
        ItemStack $$3 = this.hoveredSlot.getItem();
        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty() || this.showTooltipWithItemInHand($$3)) {
            $$0.setTooltipForNextFrame(this.font, this.getTooltipFromContainerItem($$3), $$3.getTooltipImage(), $$1, $$2, $$3.get(DataComponents.TOOLTIP_STYLE));
        }
    }

    private boolean showTooltipWithItemInHand(ItemStack $$0) {
        return $$0.getTooltipImage().map(ClientTooltipComponent::create).map(ClientTooltipComponent::showTooltipWithItemInHand).orElse(false);
    }

    protected List<Component> getTooltipFromContainerItem(ItemStack $$0) {
        return AbstractContainerScreen.getTooltipFromItem(this.minecraft, $$0);
    }

    private void renderFloatingItem(GuiGraphics $$0, ItemStack $$1, int $$2, int $$3, @Nullable String $$4) {
        $$0.renderItem($$1, $$2, $$3);
        $$0.renderItemDecorations(this.font, $$1, $$2, $$3 - (this.draggingItem.isEmpty() ? 0 : 8), $$4);
    }

    protected void renderLabels(GuiGraphics $$0, int $$1, int $$2) {
        $$0.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
        $$0.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
    }

    protected abstract void renderBg(GuiGraphics var1, float var2, int var3, int var4);

    protected void renderSlot(GuiGraphics $$0, Slot $$1) {
        ResourceLocation $$12;
        int $$2 = $$1.x;
        int $$3 = $$1.y;
        ItemStack $$4 = $$1.getItem();
        boolean $$5 = false;
        boolean $$6 = $$1 == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack $$7 = ((AbstractContainerMenu)this.menu).getCarried();
        String $$8 = null;
        if ($$1 == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !$$4.isEmpty()) {
            $$4 = $$4.copyWithCount($$4.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains($$1) && !$$7.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }
            if (AbstractContainerMenu.canItemQuickReplace($$1, $$7, true) && ((AbstractContainerMenu)this.menu).canDragTo($$1)) {
                $$5 = true;
                int $$9 = Math.min($$7.getMaxStackSize(), $$1.getMaxStackSize($$7));
                int $$10 = $$1.getItem().isEmpty() ? 0 : $$1.getItem().getCount();
                int $$11 = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, $$7) + $$10;
                if ($$11 > $$9) {
                    $$11 = $$9;
                    $$8 = ChatFormatting.YELLOW.toString() + $$9;
                }
                $$4 = $$7.copyWithCount($$11);
            } else {
                this.quickCraftSlots.remove($$1);
                this.recalculateQuickCraftRemaining();
            }
        }
        if ($$4.isEmpty() && $$1.isActive() && ($$12 = $$1.getNoItemIcon()) != null) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$12, $$2, $$3, 16, 16);
            $$6 = true;
        }
        if (!$$6) {
            if ($$5) {
                $$0.fill($$2, $$3, $$2 + 16, $$3 + 16, -2130706433);
            }
            int $$13 = $$1.x + $$1.y * this.imageWidth;
            if ($$1.isFake()) {
                $$0.renderFakeItem($$4, $$2, $$3, $$13);
            } else {
                $$0.renderItem($$4, $$2, $$3, $$13);
            }
            $$0.renderItemDecorations(this.font, $$4, $$2, $$3, $$8);
        }
    }

    private void recalculateQuickCraftRemaining() {
        ItemStack $$0 = ((AbstractContainerMenu)this.menu).getCarried();
        if ($$0.isEmpty() || !this.isQuickCrafting) {
            return;
        }
        if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = $$0.getMaxStackSize();
            return;
        }
        this.quickCraftingRemainder = $$0.getCount();
        for (Slot $$1 : this.quickCraftSlots) {
            ItemStack $$2 = $$1.getItem();
            int $$3 = $$2.isEmpty() ? 0 : $$2.getCount();
            int $$4 = Math.min($$0.getMaxStackSize(), $$1.getMaxStackSize($$0));
            int $$5 = Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, $$0) + $$3, $$4);
            this.quickCraftingRemainder -= $$5 - $$3;
        }
    }

    @Nullable
    private Slot getHoveredSlot(double $$0, double $$1) {
        for (Slot $$2 : ((AbstractContainerMenu)this.menu).slots) {
            if (!$$2.isActive() || !this.isHovering($$2, $$0, $$1)) continue;
            return $$2;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (super.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        boolean $$3 = this.minecraft.options.keyPickItem.matchesMouse($$2) && this.minecraft.player.hasInfiniteMaterials();
        Slot $$4 = this.getHoveredSlot($$0, $$1);
        long $$5 = Util.getMillis();
        this.doubleclick = this.lastClickSlot == $$4 && $$5 - this.lastClickTime < 250L && this.lastClickButton == $$2;
        this.skipNextRelease = false;
        if ($$2 == 0 || $$2 == 1 || $$3) {
            int $$6 = this.leftPos;
            int $$7 = this.topPos;
            boolean $$8 = this.hasClickedOutside($$0, $$1, $$6, $$7, $$2);
            int $$9 = -1;
            if ($$4 != null) {
                $$9 = $$4.index;
            }
            if ($$8) {
                $$9 = -999;
            }
            if (this.minecraft.options.touchscreen().get().booleanValue() && $$8 && ((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
                this.onClose();
                return true;
            }
            if ($$9 != -1) {
                if (this.minecraft.options.touchscreen().get().booleanValue()) {
                    if ($$4 != null && $$4.hasItem()) {
                        this.clickedSlot = $$4;
                        this.draggingItem = ItemStack.EMPTY;
                        this.isSplittingStack = $$2 == 1;
                    } else {
                        this.clickedSlot = null;
                    }
                } else if (!this.isQuickCrafting) {
                    if (((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
                        if ($$3) {
                            this.slotClicked($$4, $$9, $$2, ClickType.CLONE);
                        } else {
                            boolean $$10 = $$9 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                            ClickType $$11 = ClickType.PICKUP;
                            if ($$10) {
                                this.lastQuickMoved = $$4 != null && $$4.hasItem() ? $$4.getItem().copy() : ItemStack.EMPTY;
                                $$11 = ClickType.QUICK_MOVE;
                            } else if ($$9 == -999) {
                                $$11 = ClickType.THROW;
                            }
                            this.slotClicked($$4, $$9, $$2, $$11);
                        }
                        this.skipNextRelease = true;
                    } else {
                        this.isQuickCrafting = true;
                        this.quickCraftingButton = $$2;
                        this.quickCraftSlots.clear();
                        if ($$2 == 0) {
                            this.quickCraftingType = 0;
                        } else if ($$2 == 1) {
                            this.quickCraftingType = 1;
                        } else if ($$3) {
                            this.quickCraftingType = 2;
                        }
                    }
                }
            }
        } else {
            this.checkHotbarMouseClicked($$2);
        }
        this.lastClickSlot = $$4;
        this.lastClickTime = $$5;
        this.lastClickButton = $$2;
        return true;
    }

    private void checkHotbarMouseClicked(int $$0) {
        if (this.hoveredSlot != null && ((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
            if (this.minecraft.options.keySwapOffhand.matchesMouse($$0)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return;
            }
            for (int $$1 = 0; $$1 < 9; ++$$1) {
                if (!this.minecraft.options.keyHotbarSlots[$$1].matchesMouse($$0)) continue;
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, $$1, ClickType.SWAP);
            }
        }
    }

    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        return $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + this.imageWidth) || $$1 >= (double)($$3 + this.imageHeight);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        Slot $$5 = this.getHoveredSlot($$0, $$1);
        ItemStack $$6 = ((AbstractContainerMenu)this.menu).getCarried();
        if (this.clickedSlot != null && this.minecraft.options.touchscreen().get().booleanValue()) {
            if ($$2 == 0 || $$2 == 1) {
                if (this.draggingItem.isEmpty()) {
                    if ($$5 != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                        this.draggingItem = this.clickedSlot.getItem().copy();
                    }
                } else if (this.draggingItem.getCount() > 1 && $$5 != null && AbstractContainerMenu.canItemQuickReplace($$5, this.draggingItem, false)) {
                    long $$7 = Util.getMillis();
                    if (this.quickdropSlot == $$5) {
                        if ($$7 - this.quickdropTime > 500L) {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.slotClicked($$5, $$5.index, 1, ClickType.PICKUP);
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.quickdropTime = $$7 + 750L;
                            this.draggingItem.shrink(1);
                        }
                    } else {
                        this.quickdropSlot = $$5;
                        this.quickdropTime = $$7;
                    }
                }
            }
        } else if (this.isQuickCrafting && $$5 != null && !$$6.isEmpty() && ($$6.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace($$5, $$6, true) && $$5.mayPlace($$6) && ((AbstractContainerMenu)this.menu).canDragTo($$5)) {
            this.quickCraftSlots.add($$5);
            this.recalculateQuickCraftRemaining();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        Slot $$3 = this.getHoveredSlot($$0, $$1);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        boolean $$6 = this.hasClickedOutside($$0, $$1, $$4, $$5, $$2);
        int $$7 = -1;
        if ($$3 != null) {
            $$7 = $$3.index;
        }
        if ($$6) {
            $$7 = -999;
        }
        if (this.doubleclick && $$3 != null && $$2 == 0 && ((AbstractContainerMenu)this.menu).canTakeItemForPickAll(ItemStack.EMPTY, $$3)) {
            if (AbstractContainerScreen.hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    for (Slot $$8 : ((AbstractContainerMenu)this.menu).slots) {
                        if ($$8 == null || !$$8.mayPickup(this.minecraft.player) || !$$8.hasItem() || $$8.container != $$3.container || !AbstractContainerMenu.canItemQuickReplace($$8, this.lastQuickMoved, true)) continue;
                        this.slotClicked($$8, $$8.index, $$2, ClickType.QUICK_MOVE);
                    }
                }
            } else {
                this.slotClicked($$3, $$7, $$2, ClickType.PICKUP_ALL);
            }
            this.doubleclick = false;
            this.lastClickTime = 0L;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != $$2) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                this.skipNextRelease = true;
                return true;
            }
            if (this.skipNextRelease) {
                this.skipNextRelease = false;
                return true;
            }
            if (this.clickedSlot != null && this.minecraft.options.touchscreen().get().booleanValue()) {
                if ($$2 == 0 || $$2 == 1) {
                    if (this.draggingItem.isEmpty() && $$3 != this.clickedSlot) {
                        this.draggingItem = this.clickedSlot.getItem();
                    }
                    boolean $$9 = AbstractContainerMenu.canItemQuickReplace($$3, this.draggingItem, false);
                    if ($$7 != -1 && !this.draggingItem.isEmpty() && $$9) {
                        this.slotClicked(this.clickedSlot, this.clickedSlot.index, $$2, ClickType.PICKUP);
                        this.slotClicked($$3, $$7, 0, ClickType.PICKUP);
                        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
                            this.snapbackData = null;
                        } else {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, $$2, ClickType.PICKUP);
                            this.snapbackData = new SnapbackData(this.draggingItem, new Vector2i((int)$$0, (int)$$1), new Vector2i(this.clickedSlot.x + $$4, this.clickedSlot.y + $$5), Util.getMillis());
                        }
                    } else if (!this.draggingItem.isEmpty()) {
                        this.snapbackData = new SnapbackData(this.draggingItem, new Vector2i((int)$$0, (int)$$1), new Vector2i(this.clickedSlot.x + $$4, this.clickedSlot.y + $$5), Util.getMillis());
                    }
                    this.clearDraggingState();
                }
            } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);
                for (Slot $$10 : this.quickCraftSlots) {
                    this.slotClicked($$10, $$10.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
                }
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
            } else if (!((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse($$2)) {
                    this.slotClicked($$3, $$7, $$2, ClickType.CLONE);
                } else {
                    boolean $$11;
                    boolean bl = $$11 = $$7 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                    if ($$11) {
                        this.lastQuickMoved = $$3 != null && $$3.hasItem() ? $$3.getItem().copy() : ItemStack.EMPTY;
                    }
                    this.slotClicked($$3, $$7, $$2, $$11 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }
        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty()) {
            this.lastClickTime = 0L;
        }
        this.isQuickCrafting = false;
        return true;
    }

    public void clearDraggingState() {
        this.draggingItem = ItemStack.EMPTY;
        this.clickedSlot = null;
    }

    private boolean isHovering(Slot $$0, double $$1, double $$2) {
        return this.isHovering($$0.x, $$0.y, 16, 16, $$1, $$2);
    }

    protected boolean isHovering(int $$0, int $$1, int $$2, int $$3, double $$4, double $$5) {
        int $$6 = this.leftPos;
        int $$7 = this.topPos;
        return ($$4 -= (double)$$6) >= (double)($$0 - 1) && $$4 < (double)($$0 + $$2 + 1) && ($$5 -= (double)$$7) >= (double)($$1 - 1) && $$5 < (double)($$1 + $$3 + 1);
    }

    private void onStopHovering(Slot $$0) {
        if ($$0.hasItem()) {
            for (ItemSlotMouseAction $$1 : this.itemSlotMouseActions) {
                if (!$$1.matches($$0)) continue;
                $$1.onStopHovering($$0);
            }
        }
    }

    protected void slotClicked(Slot $$0, int $$1, int $$2, ClickType $$3) {
        if ($$0 != null) {
            $$1 = $$0.index;
        }
        this.onMouseClickAction($$0, $$3);
        this.minecraft.gameMode.handleInventoryMouseClick(((AbstractContainerMenu)this.menu).containerId, $$1, $$2, $$3, this.minecraft.player);
    }

    void onMouseClickAction(@Nullable Slot $$0, ClickType $$1) {
        if ($$0 != null && $$0.hasItem()) {
            for (ItemSlotMouseAction $$2 : this.itemSlotMouseActions) {
                if (!$$2.matches($$0)) continue;
                $$2.onSlotClicked($$0, $$1);
            }
        }
    }

    protected void handleSlotStateChanged(int $$0, int $$1, boolean $$2) {
        this.minecraft.gameMode.handleSlotStateChanged($$0, $$1, $$2);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) {
            return true;
        }
        if (this.minecraft.options.keyInventory.matches($$0, $$1)) {
            this.onClose();
            return true;
        }
        this.checkHotbarKeyPressed($$0, $$1);
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.matches($$0, $$1)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
            } else if (this.minecraft.options.keyDrop.matches($$0, $$1)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, AbstractContainerScreen.hasControlDown() ? 1 : 0, ClickType.THROW);
            }
        }
        return true;
    }

    protected boolean checkHotbarKeyPressed(int $$0, int $$1) {
        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty() && this.hoveredSlot != null) {
            if (this.minecraft.options.keySwapOffhand.matches($$0, $$1)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                return true;
            }
            for (int $$2 = 0; $$2 < 9; ++$$2) {
                if (!this.minecraft.options.keyHotbarSlots[$$2].matches($$0, $$1)) continue;
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, $$2, ClickType.SWAP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.minecraft.player == null) {
            return;
        }
        ((AbstractContainerMenu)this.menu).removed(this.minecraft.player);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public final void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.isRemoved()) {
            this.minecraft.player.closeContainer();
        } else {
            this.containerTick();
        }
    }

    protected void containerTick() {
    }

    @Override
    public T getMenu() {
        return this.menu;
    }

    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        if (this.hoveredSlot != null) {
            this.onStopHovering(this.hoveredSlot);
        }
        super.onClose();
    }

    static final class SnapbackData
    extends Record {
        final ItemStack item;
        final Vector2i start;
        final Vector2i end;
        final long time;

        SnapbackData(ItemStack $$0, Vector2i $$1, Vector2i $$2, long $$3) {
            this.item = $$0;
            this.start = $$1;
            this.end = $$2;
            this.time = $$3;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{SnapbackData.class, "item;start;end;time", "item", "start", "end", "time"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SnapbackData.class, "item;start;end;time", "item", "start", "end", "time"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SnapbackData.class, "item;start;end;time", "item", "start", "end", "time"}, this, $$0);
        }

        public ItemStack item() {
            return this.item;
        }

        public Vector2i start() {
            return this.start;
        }

        public Vector2i end() {
            return this.end;
        }

        public long time() {
            return this.time;
        }
    }
}

