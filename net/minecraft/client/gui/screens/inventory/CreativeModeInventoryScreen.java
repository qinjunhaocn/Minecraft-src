/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeInventoryListener;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

public class CreativeModeInventoryScreen
extends AbstractContainerScreen<ItemPickerMenu> {
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    private static final ResourceLocation[] UNSELECTED_TOP_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_7")};
    private static final ResourceLocation[] SELECTED_TOP_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_7")};
    private static final ResourceLocation[] UNSELECTED_BOTTOM_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_7")};
    private static final ResourceLocation[] SELECTED_BOTTOM_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_7")};
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLS = 9;
    private static final int TAB_WIDTH = 26;
    private static final int TAB_HEIGHT = 32;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    static final SimpleContainer CONTAINER = new SimpleContainer(45);
    private static final Component TRASH_SLOT_TOOLTIP = Component.translatable("inventory.binSlot");
    private static CreativeModeTab selectedTab = CreativeModeTabs.getDefaultTab();
    private float scrollOffs;
    private boolean scrolling;
    private EditBox searchBox;
    @Nullable
    private List<Slot> originalSlots;
    @Nullable
    private Slot destroyItemSlot;
    private CreativeInventoryListener listener;
    private boolean ignoreTextInput;
    private boolean hasClickedOutside;
    private final Set<TagKey<Item>> visibleTags = new HashSet<TagKey<Item>>();
    private final boolean displayOperatorCreativeTab;
    private final EffectsInInventory effects;

    public CreativeModeInventoryScreen(LocalPlayer $$0, FeatureFlagSet $$1, boolean $$2) {
        super(new ItemPickerMenu($$0), $$0.getInventory(), CommonComponents.EMPTY);
        $$0.containerMenu = this.menu;
        this.imageHeight = 136;
        this.imageWidth = 195;
        this.displayOperatorCreativeTab = $$2;
        this.tryRebuildTabContents($$0.connection.searchTrees(), $$1, this.hasPermissions($$0), $$0.level().registryAccess());
        this.effects = new EffectsInInventory(this);
    }

    private boolean hasPermissions(Player $$0) {
        return $$0.canUseGameMasterBlocks() && this.displayOperatorCreativeTab;
    }

    private void tryRefreshInvalidatedTabs(FeatureFlagSet $$0, boolean $$1, HolderLookup.Provider $$2) {
        ClientPacketListener $$3 = this.minecraft.getConnection();
        if (this.tryRebuildTabContents($$3 != null ? $$3.searchTrees() : null, $$0, $$1, $$2)) {
            for (CreativeModeTab $$4 : CreativeModeTabs.allTabs()) {
                Collection<ItemStack> $$5 = $$4.getDisplayItems();
                if ($$4 != selectedTab) continue;
                if ($$4.getType() == CreativeModeTab.Type.CATEGORY && $$5.isEmpty()) {
                    this.selectTab(CreativeModeTabs.getDefaultTab());
                    continue;
                }
                this.refreshCurrentTabContents($$5);
            }
        }
    }

    private boolean tryRebuildTabContents(@Nullable SessionSearchTrees $$0, FeatureFlagSet $$1, boolean $$2, HolderLookup.Provider $$3) {
        if (!CreativeModeTabs.tryRebuildTabContents($$1, $$2, $$3)) {
            return false;
        }
        if ($$0 != null) {
            List $$4 = List.copyOf(CreativeModeTabs.searchTab().getDisplayItems());
            $$0.updateCreativeTooltips($$3, $$4);
            $$0.updateCreativeTags($$4);
        }
        return true;
    }

    private void refreshCurrentTabContents(Collection<ItemStack> $$0) {
        int $$1 = ((ItemPickerMenu)this.menu).getRowIndexForScroll(this.scrollOffs);
        ((ItemPickerMenu)this.menu).items.clear();
        if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
            this.refreshSearchResults();
        } else {
            ((ItemPickerMenu)this.menu).items.addAll($$0);
        }
        this.scrollOffs = ((ItemPickerMenu)this.menu).getScrollForRowIndex($$1);
        ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.minecraft == null) {
            return;
        }
        LocalPlayer $$0 = this.minecraft.player;
        if ($$0 != null) {
            this.tryRefreshInvalidatedTabs($$0.connection.enabledFeatures(), this.hasPermissions($$0), $$0.level().registryAccess());
            if (!$$0.hasInfiniteMaterials()) {
                this.minecraft.setScreen(new InventoryScreen($$0));
            }
        }
    }

    @Override
    protected void slotClicked(@Nullable Slot $$0, int $$1, int $$2, ClickType $$3) {
        if (this.isCreativeSlot($$0)) {
            this.searchBox.moveCursorToEnd(false);
            this.searchBox.setHighlightPos(0);
        }
        boolean $$4 = $$3 == ClickType.QUICK_MOVE;
        ClickType clickType = $$3 = $$1 == -999 && $$3 == ClickType.PICKUP ? ClickType.THROW : $$3;
        if ($$3 == ClickType.THROW && !this.minecraft.player.canDropItems()) {
            return;
        }
        this.onMouseClickAction($$0, $$3);
        if ($$0 != null || selectedTab.getType() == CreativeModeTab.Type.INVENTORY || $$3 == ClickType.QUICK_CRAFT) {
            if ($$0 != null && !$$0.mayPickup(this.minecraft.player)) {
                return;
            }
            if ($$0 == this.destroyItemSlot && $$4) {
                for (int $$5 = 0; $$5 < this.minecraft.player.inventoryMenu.getItems().size(); ++$$5) {
                    this.minecraft.player.inventoryMenu.getSlot($$5).set(ItemStack.EMPTY);
                    this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, $$5);
                }
            } else if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
                if ($$0 == this.destroyItemSlot) {
                    ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                } else if ($$3 == ClickType.THROW && $$0 != null && $$0.hasItem()) {
                    ItemStack $$6 = $$0.remove($$2 == 0 ? 1 : $$0.getItem().getMaxStackSize());
                    ItemStack $$7 = $$0.getItem();
                    this.minecraft.player.drop($$6, true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop($$6);
                    this.minecraft.gameMode.handleCreativeModeItemAdd($$7, ((SlotWrapper)$$0).target.index);
                } else if ($$3 == ClickType.THROW && $$1 == -999 && !((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                    this.minecraft.player.drop(((ItemPickerMenu)this.menu).getCarried(), true);
                    this.minecraft.gameMode.handleCreativeModeItemDrop(((ItemPickerMenu)this.menu).getCarried());
                    ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                } else {
                    this.minecraft.player.inventoryMenu.clicked($$0 == null ? $$1 : ((SlotWrapper)$$0).target.index, $$2, $$3, this.minecraft.player);
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }
            } else if ($$3 != ClickType.QUICK_CRAFT && $$0.container == CONTAINER) {
                ItemStack $$8 = ((ItemPickerMenu)this.menu).getCarried();
                ItemStack $$9 = $$0.getItem();
                if ($$3 == ClickType.SWAP) {
                    if (!$$9.isEmpty()) {
                        this.minecraft.player.getInventory().setItem($$2, $$9.copyWithCount($$9.getMaxStackSize()));
                        this.minecraft.player.inventoryMenu.broadcastChanges();
                    }
                    return;
                }
                if ($$3 == ClickType.CLONE) {
                    if (((ItemPickerMenu)this.menu).getCarried().isEmpty() && $$0.hasItem()) {
                        ItemStack $$10 = $$0.getItem();
                        ((ItemPickerMenu)this.menu).setCarried($$10.copyWithCount($$10.getMaxStackSize()));
                    }
                    return;
                }
                if ($$3 == ClickType.THROW) {
                    if (!$$9.isEmpty()) {
                        ItemStack $$11 = $$9.copyWithCount($$2 == 0 ? 1 : $$9.getMaxStackSize());
                        this.minecraft.player.drop($$11, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop($$11);
                    }
                    return;
                }
                if (!$$8.isEmpty() && !$$9.isEmpty() && ItemStack.isSameItemSameComponents($$8, $$9)) {
                    if ($$2 == 0) {
                        if ($$4) {
                            $$8.setCount($$8.getMaxStackSize());
                        } else if ($$8.getCount() < $$8.getMaxStackSize()) {
                            $$8.grow(1);
                        }
                    } else {
                        $$8.shrink(1);
                    }
                } else if ($$9.isEmpty() || !$$8.isEmpty()) {
                    if ($$2 == 0) {
                        ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                    } else if (!((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                        ((ItemPickerMenu)this.menu).getCarried().shrink(1);
                    }
                } else {
                    int $$12 = $$4 ? $$9.getMaxStackSize() : $$9.getCount();
                    ((ItemPickerMenu)this.menu).setCarried($$9.copyWithCount($$12));
                }
            } else if (this.menu != null) {
                ItemStack $$13 = $$0 == null ? ItemStack.EMPTY : ((ItemPickerMenu)this.menu).getSlot($$0.index).getItem();
                ((ItemPickerMenu)this.menu).clicked($$0 == null ? $$1 : $$0.index, $$2, $$3, this.minecraft.player);
                if (AbstractContainerMenu.getQuickcraftHeader($$2) == 2) {
                    for (int $$14 = 0; $$14 < 9; ++$$14) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(((ItemPickerMenu)this.menu).getSlot(45 + $$14).getItem(), 36 + $$14);
                    }
                } else if ($$0 != null && Inventory.isHotbarSlot($$0.getContainerSlot()) && selectedTab.getType() != CreativeModeTab.Type.INVENTORY) {
                    if ($$3 == ClickType.THROW && !$$13.isEmpty() && !((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                        int $$15 = $$2 == 0 ? 1 : $$13.getCount();
                        ItemStack $$16 = $$13.copyWithCount($$15);
                        $$13.shrink($$15);
                        this.minecraft.player.drop($$16, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop($$16);
                    }
                    this.minecraft.player.inventoryMenu.broadcastChanges();
                }
            }
        } else if (!((ItemPickerMenu)this.menu).getCarried().isEmpty() && this.hasClickedOutside) {
            if (!this.minecraft.player.canDropItems()) {
                return;
            }
            if ($$2 == 0) {
                this.minecraft.player.drop(((ItemPickerMenu)this.menu).getCarried(), true);
                this.minecraft.gameMode.handleCreativeModeItemDrop(((ItemPickerMenu)this.menu).getCarried());
                ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
            }
            if ($$2 == 1) {
                ItemStack $$17 = ((ItemPickerMenu)this.menu).getCarried().split(1);
                this.minecraft.player.drop($$17, true);
                this.minecraft.gameMode.handleCreativeModeItemDrop($$17);
            }
        }
    }

    private boolean isCreativeSlot(@Nullable Slot $$0) {
        return $$0 != null && $$0.container == CONTAINER;
    }

    @Override
    protected void init() {
        if (this.minecraft.player.hasInfiniteMaterials()) {
            super.init();
            this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, this.font.lineHeight, Component.translatable("itemGroup.search"));
            this.searchBox.setMaxLength(50);
            this.searchBox.setBordered(false);
            this.searchBox.setVisible(false);
            this.searchBox.setTextColor(-1);
            this.addWidget(this.searchBox);
            CreativeModeTab $$0 = selectedTab;
            selectedTab = CreativeModeTabs.getDefaultTab();
            this.selectTab($$0);
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
            this.listener = new CreativeInventoryListener(this.minecraft);
            this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
            if (!selectedTab.shouldDisplay()) {
                this.selectTab(CreativeModeTabs.getDefaultTab());
            }
        } else {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        }
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        int $$3 = ((ItemPickerMenu)this.menu).getRowIndexForScroll(this.scrollOffs);
        String $$4 = this.searchBox.getValue();
        this.init($$0, $$1, $$2);
        this.searchBox.setValue($$4);
        if (!this.searchBox.getValue().isEmpty()) {
            this.refreshSearchResults();
        }
        this.scrollOffs = ((ItemPickerMenu)this.menu).getScrollForRowIndex($$3);
        ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
    }

    @Override
    public void removed() {
        super.removed();
        if (this.minecraft.player != null && this.minecraft.player.getInventory() != null) {
            this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
        }
    }

    @Override
    public boolean a(char $$0, int $$1) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (selectedTab.getType() != CreativeModeTab.Type.SEARCH) {
            return false;
        }
        String $$2 = this.searchBox.getValue();
        if (this.searchBox.a($$0, $$1)) {
            if (!Objects.equals($$2, this.searchBox.getValue())) {
                this.refreshSearchResults();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        this.ignoreTextInput = false;
        if (selectedTab.getType() != CreativeModeTab.Type.SEARCH) {
            if (this.minecraft.options.keyChat.matches($$0, $$1)) {
                this.ignoreTextInput = true;
                this.selectTab(CreativeModeTabs.searchTab());
                return true;
            }
            return super.keyPressed($$0, $$1, $$2);
        }
        boolean $$3 = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot.hasItem();
        boolean $$4 = InputConstants.getKey($$0, $$1).getNumericKeyValue().isPresent();
        if ($$3 && $$4 && this.checkHotbarKeyPressed($$0, $$1)) {
            this.ignoreTextInput = true;
            return true;
        }
        String $$5 = this.searchBox.getValue();
        if (this.searchBox.keyPressed($$0, $$1, $$2)) {
            if (!Objects.equals($$5, this.searchBox.getValue())) {
                this.refreshSearchResults();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && $$0 != 256) {
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public boolean keyReleased(int $$0, int $$1, int $$2) {
        this.ignoreTextInput = false;
        return super.keyReleased($$0, $$1, $$2);
    }

    private void refreshSearchResults() {
        ((ItemPickerMenu)this.menu).items.clear();
        this.visibleTags.clear();
        String $$0 = this.searchBox.getValue();
        if ($$0.isEmpty()) {
            ((ItemPickerMenu)this.menu).items.addAll(selectedTab.getDisplayItems());
        } else {
            ClientPacketListener $$1 = this.minecraft.getConnection();
            if ($$1 != null) {
                SearchTree<ItemStack> $$4;
                SessionSearchTrees $$2 = $$1.searchTrees();
                if ($$0.startsWith("#")) {
                    $$0 = $$0.substring(1);
                    SearchTree<ItemStack> $$3 = $$2.creativeTagSearch();
                    this.updateVisibleTags($$0);
                } else {
                    $$4 = $$2.creativeNameSearch();
                }
                ((ItemPickerMenu)this.menu).items.addAll($$4.search($$0.toLowerCase(Locale.ROOT)));
            }
        }
        this.scrollOffs = 0.0f;
        ((ItemPickerMenu)this.menu).scrollTo(0.0f);
    }

    private void updateVisibleTags(String $$0) {
        Predicate<ResourceLocation> $$5;
        int $$12 = $$0.indexOf(58);
        if ($$12 == -1) {
            Predicate<ResourceLocation> $$22 = $$1 -> $$1.getPath().contains($$0);
        } else {
            String $$3 = $$0.substring(0, $$12).trim();
            String $$4 = $$0.substring($$12 + 1).trim();
            $$5 = $$2 -> $$2.getNamespace().contains($$3) && $$2.getPath().contains($$4);
        }
        BuiltInRegistries.ITEM.getTags().map(HolderSet.Named::key).filter($$1 -> $$5.test($$1.location())).forEach(this.visibleTags::add);
    }

    @Override
    protected void renderLabels(GuiGraphics $$0, int $$1, int $$2) {
        if (selectedTab.showTitle()) {
            $$0.drawString(this.font, selectedTab.getDisplayName(), 8, 6, -12566464, false);
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if ($$2 == 0) {
            double $$3 = $$0 - (double)this.leftPos;
            double $$4 = $$1 - (double)this.topPos;
            for (CreativeModeTab $$5 : CreativeModeTabs.tabs()) {
                if (!this.checkTabClicked($$5, $$3, $$4)) continue;
                return true;
            }
            if (selectedTab.getType() != CreativeModeTab.Type.INVENTORY && this.insideScrollbar($$0, $$1)) {
                this.scrolling = this.canScroll();
                return true;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if ($$2 == 0) {
            double $$3 = $$0 - (double)this.leftPos;
            double $$4 = $$1 - (double)this.topPos;
            this.scrolling = false;
            for (CreativeModeTab $$5 : CreativeModeTabs.tabs()) {
                if (!this.checkTabClicked($$5, $$3, $$4)) continue;
                this.selectTab($$5);
                return true;
            }
        }
        return super.mouseReleased($$0, $$1, $$2);
    }

    private boolean canScroll() {
        return selectedTab.canScroll() && ((ItemPickerMenu)this.menu).canScroll();
    }

    private void selectTab(CreativeModeTab $$0) {
        CreativeModeTab $$1 = selectedTab;
        selectedTab = $$0;
        this.quickCraftSlots.clear();
        ((ItemPickerMenu)this.menu).items.clear();
        this.clearDraggingState();
        if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR) {
            HotbarManager $$2 = this.minecraft.getHotbarManager();
            for (int $$3 = 0; $$3 < 9; ++$$3) {
                Hotbar $$4 = $$2.get($$3);
                if ($$4.isEmpty()) {
                    for (int $$5 = 0; $$5 < 9; ++$$5) {
                        if ($$5 == $$3) {
                            ItemStack $$6 = new ItemStack(Items.PAPER);
                            $$6.set(DataComponents.CREATIVE_SLOT_LOCK, Unit.INSTANCE);
                            Component $$7 = this.minecraft.options.keyHotbarSlots[$$3].getTranslatedKeyMessage();
                            Component $$8 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                            $$6.set(DataComponents.ITEM_NAME, Component.a("inventory.hotbarInfo", $$8, $$7));
                            ((ItemPickerMenu)this.menu).items.add($$6);
                            continue;
                        }
                        ((ItemPickerMenu)this.menu).items.add(ItemStack.EMPTY);
                    }
                    continue;
                }
                ((ItemPickerMenu)this.menu).items.addAll($$4.load(this.minecraft.level.registryAccess()));
            }
        } else if (selectedTab.getType() == CreativeModeTab.Type.CATEGORY) {
            ((ItemPickerMenu)this.menu).items.addAll(selectedTab.getDisplayItems());
        }
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            InventoryMenu $$9 = this.minecraft.player.inventoryMenu;
            if (this.originalSlots == null) {
                this.originalSlots = ImmutableList.copyOf(((ItemPickerMenu)this.menu).slots);
            }
            ((ItemPickerMenu)this.menu).slots.clear();
            for (int $$10 = 0; $$10 < $$9.slots.size(); ++$$10) {
                int $$25;
                int $$23;
                if ($$10 >= 5 && $$10 < 9) {
                    int $$11 = $$10 - 5;
                    int $$12 = $$11 / 2;
                    int $$13 = $$11 % 2;
                    int $$14 = 54 + $$12 * 54;
                    int $$15 = 6 + $$13 * 27;
                } else if ($$10 >= 0 && $$10 < 5) {
                    int $$16 = -2000;
                    int $$17 = -2000;
                } else if ($$10 == 45) {
                    int $$18 = 35;
                    int $$19 = 20;
                } else {
                    int $$20 = $$10 - 9;
                    int $$21 = $$20 % 9;
                    int $$22 = $$20 / 9;
                    $$23 = 9 + $$21 * 18;
                    if ($$10 >= 36) {
                        int $$24 = 112;
                    } else {
                        $$25 = 54 + $$22 * 18;
                    }
                }
                SlotWrapper $$26 = new SlotWrapper($$9.slots.get($$10), $$10, $$23, $$25);
                ((ItemPickerMenu)this.menu).slots.add($$26);
            }
            this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
            ((ItemPickerMenu)this.menu).slots.add(this.destroyItemSlot);
        } else if ($$1.getType() == CreativeModeTab.Type.INVENTORY) {
            ((ItemPickerMenu)this.menu).slots.clear();
            ((ItemPickerMenu)this.menu).slots.addAll(this.originalSlots);
            this.originalSlots = null;
        }
        if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
            this.searchBox.setVisible(true);
            this.searchBox.setCanLoseFocus(false);
            this.searchBox.setFocused(true);
            if ($$1 != $$0) {
                this.searchBox.setValue("");
            }
            this.refreshSearchResults();
        } else {
            this.searchBox.setVisible(false);
            this.searchBox.setCanLoseFocus(true);
            this.searchBox.setFocused(false);
            this.searchBox.setValue("");
        }
        this.scrollOffs = 0.0f;
        ((ItemPickerMenu)this.menu).scrollTo(0.0f);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (super.mouseScrolled($$0, $$1, $$2, $$3)) {
            return true;
        }
        if (!this.canScroll()) {
            return false;
        }
        this.scrollOffs = ((ItemPickerMenu)this.menu).subtractInputFromScroll(this.scrollOffs, $$3);
        ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
        return true;
    }

    @Override
    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        boolean $$5 = $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + this.imageWidth) || $$1 >= (double)($$3 + this.imageHeight);
        this.hasClickedOutside = $$5 && !this.checkTabClicked(selectedTab, $$0, $$1);
        return this.hasClickedOutside;
    }

    protected boolean insideScrollbar(double $$0, double $$1) {
        int $$2 = this.leftPos;
        int $$3 = this.topPos;
        int $$4 = $$2 + 175;
        int $$5 = $$3 + 18;
        int $$6 = $$4 + 14;
        int $$7 = $$5 + 112;
        return $$0 >= (double)$$4 && $$1 >= (double)$$5 && $$0 < (double)$$6 && $$1 < (double)$$7;
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.scrolling) {
            int $$5 = this.topPos + 18;
            int $$6 = $$5 + 112;
            this.scrollOffs = ((float)$$1 - (float)$$5 - 7.5f) / ((float)($$6 - $$5) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
            return true;
        }
        return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.effects.renderEffects($$0, $$1, $$2);
        super.render($$0, $$1, $$2, $$3);
        this.effects.renderTooltip($$0, $$1, $$2);
        for (CreativeModeTab $$4 : CreativeModeTabs.tabs()) {
            if (this.checkTabHovering($$0, $$4, $$1, $$2)) break;
        }
        if (this.destroyItemSlot != null && selectedTab.getType() == CreativeModeTab.Type.INVENTORY && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, $$1, $$2)) {
            $$0.setTooltipForNextFrame(this.font, TRASH_SLOT_TOOLTIP, $$1, $$2);
        }
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    public boolean showsActiveEffects() {
        return this.effects.canSeeEffects();
    }

    @Override
    public List<Component> getTooltipFromContainerItem(ItemStack $$0) {
        boolean $$1 = this.hoveredSlot != null && this.hoveredSlot instanceof CustomCreativeSlot;
        boolean $$22 = selectedTab.getType() == CreativeModeTab.Type.CATEGORY;
        boolean $$3 = selectedTab.getType() == CreativeModeTab.Type.SEARCH;
        TooltipFlag.Default $$4 = this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        TooltipFlag.Default $$5 = $$1 ? $$4.asCreative() : $$4;
        List<Component> $$6 = $$0.getTooltipLines(Item.TooltipContext.of(this.minecraft.level), this.minecraft.player, $$5);
        if ($$6.isEmpty()) {
            return $$6;
        }
        if (!$$22 || !$$1) {
            ArrayList<Component> $$7 = Lists.newArrayList($$6);
            if ($$3 && $$1) {
                this.visibleTags.forEach($$2 -> {
                    if ($$0.is((TagKey<Item>)((Object)$$2))) {
                        $$7.add(1, Component.literal("#" + String.valueOf($$2.location())).withStyle(ChatFormatting.DARK_PURPLE));
                    }
                });
            }
            int $$8 = 1;
            for (CreativeModeTab $$9 : CreativeModeTabs.tabs()) {
                if ($$9.getType() == CreativeModeTab.Type.SEARCH || !$$9.contains($$0)) continue;
                $$7.add($$8++, $$9.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
            }
            return $$7;
        }
        return $$6;
    }

    @Override
    protected void renderBg(GuiGraphics $$0, float $$1, int $$2, int $$3) {
        for (CreativeModeTab $$4 : CreativeModeTabs.tabs()) {
            if ($$4 == selectedTab) continue;
            this.renderTabButton($$0, $$4);
        }
        $$0.blit(RenderPipelines.GUI_TEXTURED, selectedTab.getBackgroundTexture(), this.leftPos, this.topPos, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
        this.searchBox.render($$0, $$2, $$3, $$1);
        int $$5 = this.leftPos + 175;
        int $$6 = this.topPos + 18;
        int $$7 = $$6 + 112;
        if (selectedTab.canScroll()) {
            ResourceLocation $$8 = this.canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$8, $$5, $$6 + (int)((float)($$7 - $$6 - 17) * this.scrollOffs), 12, 15);
        }
        this.renderTabButton($$0, selectedTab);
        if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            InventoryScreen.renderEntityInInventoryFollowsMouse($$0, this.leftPos + 73, this.topPos + 6, this.leftPos + 105, this.topPos + 49, 20, 0.0625f, $$2, $$3, this.minecraft.player);
        }
    }

    private int getTabX(CreativeModeTab $$0) {
        int $$1 = $$0.column();
        int $$2 = 27;
        int $$3 = 27 * $$1;
        if ($$0.isAlignedRight()) {
            $$3 = this.imageWidth - 27 * (7 - $$1) + 1;
        }
        return $$3;
    }

    private int getTabY(CreativeModeTab $$0) {
        int $$1 = 0;
        $$1 = $$0.row() == CreativeModeTab.Row.TOP ? ($$1 -= 32) : ($$1 += this.imageHeight);
        return $$1;
    }

    protected boolean checkTabClicked(CreativeModeTab $$0, double $$1, double $$2) {
        int $$3 = this.getTabX($$0);
        int $$4 = this.getTabY($$0);
        return $$1 >= (double)$$3 && $$1 <= (double)($$3 + 26) && $$2 >= (double)$$4 && $$2 <= (double)($$4 + 32);
    }

    protected boolean checkTabHovering(GuiGraphics $$0, CreativeModeTab $$1, int $$2, int $$3) {
        int $$5;
        int $$4 = this.getTabX($$1);
        if (this.isHovering($$4 + 3, ($$5 = this.getTabY($$1)) + 3, 21, 27, $$2, $$3)) {
            $$0.setTooltipForNextFrame(this.font, $$1.getDisplayName(), $$2, $$3);
            return true;
        }
        return false;
    }

    protected void renderTabButton(GuiGraphics $$0, CreativeModeTab $$1) {
        ResourceLocation[] $$8;
        boolean $$2 = $$1 == selectedTab;
        boolean $$3 = $$1.row() == CreativeModeTab.Row.TOP;
        int $$4 = $$1.column();
        int $$5 = this.leftPos + this.getTabX($$1);
        int $$6 = this.topPos - ($$3 ? 28 : -(this.imageHeight - 4));
        if ($$3) {
            ResourceLocation[] $$7 = $$2 ? SELECTED_TOP_TABS : UNSELECTED_TOP_TABS;
        } else {
            $$8 = $$2 ? SELECTED_BOTTOM_TABS : UNSELECTED_BOTTOM_TABS;
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$8[Mth.clamp($$4, 0, $$8.length)], $$5, $$6, 26, 32);
        int $$9 = $$5 + 13 - 8;
        int $$10 = $$6 + 16 - 8 + ($$3 ? 1 : -1);
        $$0.renderItem($$1.getIconItem(), $$9, $$10);
    }

    public boolean isInventoryOpen() {
        return selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
    }

    public static void handleHotbarLoadOrSave(Minecraft $$0, int $$1, boolean $$2, boolean $$3) {
        LocalPlayer $$4 = $$0.player;
        RegistryAccess $$5 = $$4.level().registryAccess();
        HotbarManager $$6 = $$0.getHotbarManager();
        Hotbar $$7 = $$6.get($$1);
        if ($$2) {
            List<ItemStack> $$8 = $$7.load($$5);
            for (int $$9 = 0; $$9 < Inventory.getSelectionSize(); ++$$9) {
                ItemStack $$10 = $$8.get($$9);
                $$4.getInventory().setItem($$9, $$10);
                $$0.gameMode.handleCreativeModeItemAdd($$10, 36 + $$9);
            }
            $$4.inventoryMenu.broadcastChanges();
        } else if ($$3) {
            $$7.storeFrom($$4.getInventory(), $$5);
            Component $$11 = $$0.options.keyHotbarSlots[$$1].getTranslatedKeyMessage();
            Component $$12 = $$0.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
            MutableComponent $$13 = Component.a("inventory.hotbarSaved", $$12, $$11);
            $$0.gui.setOverlayMessage($$13, false);
            $$0.getNarrator().saySystemNow($$13);
            $$6.save();
        }
    }

    public static class ItemPickerMenu
    extends AbstractContainerMenu {
        public final NonNullList<ItemStack> items = NonNullList.create();
        private final AbstractContainerMenu inventoryMenu;

        public ItemPickerMenu(Player $$0) {
            super(null, 0);
            this.inventoryMenu = $$0.inventoryMenu;
            Inventory $$1 = $$0.getInventory();
            for (int $$2 = 0; $$2 < 5; ++$$2) {
                for (int $$3 = 0; $$3 < 9; ++$$3) {
                    this.addSlot(new CustomCreativeSlot(CONTAINER, $$2 * 9 + $$3, 9 + $$3 * 18, 18 + $$2 * 18));
                }
            }
            this.addInventoryHotbarSlots($$1, 9, 112);
            this.scrollTo(0.0f);
        }

        @Override
        public boolean stillValid(Player $$0) {
            return true;
        }

        protected int calculateRowCount() {
            return Mth.positiveCeilDiv(this.items.size(), 9) - 5;
        }

        protected int getRowIndexForScroll(float $$0) {
            return Math.max((int)((double)($$0 * (float)this.calculateRowCount()) + 0.5), 0);
        }

        protected float getScrollForRowIndex(int $$0) {
            return Mth.clamp((float)$$0 / (float)this.calculateRowCount(), 0.0f, 1.0f);
        }

        protected float subtractInputFromScroll(float $$0, double $$1) {
            return Mth.clamp($$0 - (float)($$1 / (double)this.calculateRowCount()), 0.0f, 1.0f);
        }

        public void scrollTo(float $$0) {
            int $$1 = this.getRowIndexForScroll($$0);
            for (int $$2 = 0; $$2 < 5; ++$$2) {
                for (int $$3 = 0; $$3 < 9; ++$$3) {
                    int $$4 = $$3 + ($$2 + $$1) * 9;
                    if ($$4 >= 0 && $$4 < this.items.size()) {
                        CONTAINER.setItem($$3 + $$2 * 9, this.items.get($$4));
                        continue;
                    }
                    CONTAINER.setItem($$3 + $$2 * 9, ItemStack.EMPTY);
                }
            }
        }

        public boolean canScroll() {
            return this.items.size() > 45;
        }

        @Override
        public ItemStack quickMoveStack(Player $$0, int $$1) {
            Slot $$2;
            if ($$1 >= this.slots.size() - 9 && $$1 < this.slots.size() && ($$2 = (Slot)this.slots.get($$1)) != null && $$2.hasItem()) {
                $$2.setByPlayer(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
            return $$1.container != CONTAINER;
        }

        @Override
        public boolean canDragTo(Slot $$0) {
            return $$0.container != CONTAINER;
        }

        @Override
        public ItemStack getCarried() {
            return this.inventoryMenu.getCarried();
        }

        @Override
        public void setCarried(ItemStack $$0) {
            this.inventoryMenu.setCarried($$0);
        }
    }

    static class SlotWrapper
    extends Slot {
        final Slot target;

        public SlotWrapper(Slot $$0, int $$1, int $$2, int $$3) {
            super($$0.container, $$1, $$2, $$3);
            this.target = $$0;
        }

        @Override
        public void onTake(Player $$0, ItemStack $$1) {
            this.target.onTake($$0, $$1);
        }

        @Override
        public boolean mayPlace(ItemStack $$0) {
            return this.target.mayPlace($$0);
        }

        @Override
        public ItemStack getItem() {
            return this.target.getItem();
        }

        @Override
        public boolean hasItem() {
            return this.target.hasItem();
        }

        @Override
        public void setByPlayer(ItemStack $$0, ItemStack $$1) {
            this.target.setByPlayer($$0, $$1);
        }

        @Override
        public void set(ItemStack $$0) {
            this.target.set($$0);
        }

        @Override
        public void setChanged() {
            this.target.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return this.target.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(ItemStack $$0) {
            return this.target.getMaxStackSize($$0);
        }

        @Override
        @Nullable
        public ResourceLocation getNoItemIcon() {
            return this.target.getNoItemIcon();
        }

        @Override
        public ItemStack remove(int $$0) {
            return this.target.remove($$0);
        }

        @Override
        public boolean isActive() {
            return this.target.isActive();
        }

        @Override
        public boolean mayPickup(Player $$0) {
            return this.target.mayPickup($$0);
        }
    }

    static class CustomCreativeSlot
    extends Slot {
        public CustomCreativeSlot(Container $$0, int $$1, int $$2, int $$3) {
            super($$0, $$1, $$2, $$3);
        }

        @Override
        public boolean mayPickup(Player $$0) {
            ItemStack $$1 = this.getItem();
            if (super.mayPickup($$0) && !$$1.isEmpty()) {
                return $$1.isItemEnabled($$0.level().enabledFeatures()) && !$$1.has(DataComponents.CREATIVE_SLOT_LOCK);
            }
            return $$1.isEmpty();
        }
    }
}

