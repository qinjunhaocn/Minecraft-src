/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public abstract class RecipeBookComponent<T extends RecipeBookMenu>
implements Renderable,
GuiEventListener,
NarratableEntry {
    public static final WidgetSprites RECIPE_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/button"), ResourceLocation.withDefaultNamespace("recipe_book/button_highlighted"));
    protected static final ResourceLocation RECIPE_BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png");
    private static final int BACKGROUND_TEXTURE_WIDTH = 256;
    private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
    private static final Component SEARCH_HINT = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    public static final int IMAGE_WIDTH = 147;
    public static final int IMAGE_HEIGHT = 166;
    private static final int OFFSET_X_POSITION = 86;
    private static final int BORDER_WIDTH = 8;
    private static final Component ALL_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.all");
    private static final int TICKS_TO_SWAP_SLOT = 30;
    private int xOffset;
    private int width;
    private int height;
    private float time;
    @Nullable
    private RecipeDisplayId lastPlacedRecipe;
    private final GhostSlots ghostSlots;
    private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
    @Nullable
    private RecipeBookTabButton selectedTab;
    protected StateSwitchingButton filterButton;
    protected final T menu;
    protected Minecraft minecraft;
    @Nullable
    private EditBox searchBox;
    private String lastSearch = "";
    private final List<TabInfo> tabInfos;
    private ClientRecipeBook book;
    private final RecipeBookPage recipeBookPage;
    @Nullable
    private RecipeDisplayId lastRecipe;
    @Nullable
    private RecipeCollection lastRecipeCollection;
    private final StackedItemContents stackedContents = new StackedItemContents();
    private int timesInventoryChanged;
    private boolean ignoreTextInput;
    private boolean visible;
    private boolean widthTooNarrow;
    @Nullable
    private ScreenRectangle magnifierIconPlacement;

    public RecipeBookComponent(T $$0, List<TabInfo> $$1) {
        this.menu = $$0;
        this.tabInfos = $$1;
        SlotSelectTime $$2 = () -> Mth.floor(this.time / 30.0f);
        this.ghostSlots = new GhostSlots($$2);
        this.recipeBookPage = new RecipeBookPage(this, $$2, $$0 instanceof AbstractFurnaceMenu);
    }

    public void init(int $$0, int $$1, Minecraft $$2, boolean $$3) {
        this.minecraft = $$2;
        this.width = $$0;
        this.height = $$1;
        this.widthTooNarrow = $$3;
        this.book = $$2.player.getRecipeBook();
        this.timesInventoryChanged = $$2.player.getInventory().getTimesChanged();
        this.visible = this.isVisibleAccordingToBookData();
        if (this.visible) {
            this.initVisuals();
        }
    }

    private void initVisuals() {
        boolean $$02 = this.isFiltering();
        this.xOffset = this.widthTooNarrow ? 0 : 86;
        int $$1 = this.getXOrigin();
        int $$2 = this.getYOrigin();
        this.stackedContents.clear();
        this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
        ((RecipeBookMenu)this.menu).fillCraftSlotsStackedContents(this.stackedContents);
        String $$3 = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.minecraft.font, $$1 + 25, $$2 + 13, 81, this.minecraft.font.lineHeight + 5, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(-1);
        this.searchBox.setValue($$3);
        this.searchBox.setHint(SEARCH_HINT);
        this.magnifierIconPlacement = ScreenRectangle.of(ScreenAxis.HORIZONTAL, $$1 + 8, this.searchBox.getY(), this.searchBox.getX() - this.getXOrigin(), this.searchBox.getHeight());
        this.recipeBookPage.init(this.minecraft, $$1, $$2);
        this.filterButton = new StateSwitchingButton($$1 + 110, $$2 + 12, 26, 16, $$02);
        this.updateFilterButtonTooltip();
        this.initFilterButtonTextures();
        this.tabButtons.clear();
        for (TabInfo $$4 : this.tabInfos) {
            this.tabButtons.add(new RecipeBookTabButton($$4));
        }
        if (this.selectedTab != null) {
            this.selectedTab = this.tabButtons.stream().filter($$0 -> $$0.getCategory().equals(this.selectedTab.getCategory())).findFirst().orElse(null);
        }
        if (this.selectedTab == null) {
            this.selectedTab = this.tabButtons.get(0);
        }
        this.selectedTab.setStateTriggered(true);
        this.selectMatchingRecipes();
        this.updateTabs($$02);
        this.updateCollections(false, $$02);
    }

    private int getYOrigin() {
        return (this.height - 166) / 2;
    }

    private int getXOrigin() {
        return (this.width - 147) / 2 - this.xOffset;
    }

    private void updateFilterButtonTooltip() {
        this.filterButton.setTooltip(this.filterButton.isStateTriggered() ? Tooltip.create(this.getRecipeFilterName()) : Tooltip.create(ALL_RECIPES_TOOLTIP));
    }

    protected abstract void initFilterButtonTextures();

    public int updateScreenPosition(int $$0, int $$1) {
        int $$3;
        if (this.isVisible() && !this.widthTooNarrow) {
            int $$2 = 177 + ($$0 - $$1 - 200) / 2;
        } else {
            $$3 = ($$0 - $$1) / 2;
        }
        return $$3;
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    public boolean isVisible() {
        return this.visible;
    }

    private boolean isVisibleAccordingToBookData() {
        return this.book.isOpen(((RecipeBookMenu)this.menu).getRecipeBookType());
    }

    protected void setVisible(boolean $$0) {
        if ($$0) {
            this.initVisuals();
        }
        this.visible = $$0;
        this.book.setOpen(((RecipeBookMenu)this.menu).getRecipeBookType(), $$0);
        if (!$$0) {
            this.recipeBookPage.setInvisible();
        }
        this.sendUpdateSettings();
    }

    protected abstract boolean isCraftingSlot(Slot var1);

    public void slotClicked(@Nullable Slot $$0) {
        if ($$0 != null && this.isCraftingSlot($$0)) {
            this.lastPlacedRecipe = null;
            this.ghostSlots.clear();
            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }
    }

    private void selectMatchingRecipes() {
        for (TabInfo $$0 : this.tabInfos) {
            for (RecipeCollection $$1 : this.book.getCollection($$0.category())) {
                this.selectMatchingRecipes($$1, this.stackedContents);
            }
        }
    }

    protected abstract void selectMatchingRecipes(RecipeCollection var1, StackedItemContents var2);

    private void updateCollections(boolean $$02, boolean $$1) {
        ClientPacketListener $$5;
        List<RecipeCollection> $$2 = this.book.getCollection(this.selectedTab.getCategory());
        ArrayList<RecipeCollection> $$3 = Lists.newArrayList($$2);
        $$3.removeIf($$0 -> !$$0.hasAnySelected());
        String $$4 = this.searchBox.getValue();
        if (!$$4.isEmpty() && ($$5 = this.minecraft.getConnection()) != null) {
            ObjectLinkedOpenHashSet $$6 = new ObjectLinkedOpenHashSet($$5.searchTrees().recipes().search($$4.toLowerCase(Locale.ROOT)));
            $$3.removeIf(arg_0 -> RecipeBookComponent.lambda$updateCollections$3((ObjectSet)$$6, arg_0));
        }
        if ($$1) {
            $$3.removeIf($$0 -> !$$0.hasCraftable());
        }
        this.recipeBookPage.updateCollections($$3, $$02, $$1);
    }

    private void updateTabs(boolean $$0) {
        int $$1 = (this.width - 147) / 2 - this.xOffset - 30;
        int $$2 = (this.height - 166) / 2 + 3;
        int $$3 = 27;
        int $$4 = 0;
        for (RecipeBookTabButton $$5 : this.tabButtons) {
            ExtendedRecipeBookCategory $$6 = $$5.getCategory();
            if ($$6 instanceof SearchRecipeBookCategory) {
                $$5.visible = true;
                $$5.setPosition($$1, $$2 + 27 * $$4++);
                continue;
            }
            if (!$$5.updateVisibility(this.book)) continue;
            $$5.setPosition($$1, $$2 + 27 * $$4++);
            $$5.startAnimation(this.book, $$0);
        }
    }

    public void tick() {
        boolean $$0 = this.isVisibleAccordingToBookData();
        if (this.isVisible() != $$0) {
            this.setVisible($$0);
        }
        if (!this.isVisible()) {
            return;
        }
        if (this.timesInventoryChanged != this.minecraft.player.getInventory().getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.getInventory().getTimesChanged();
        }
    }

    private void updateStackedContents() {
        this.stackedContents.clear();
        this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
        ((RecipeBookMenu)this.menu).fillCraftSlotsStackedContents(this.stackedContents);
        this.selectMatchingRecipes();
        this.updateCollections(false, this.isFiltering());
    }

    private boolean isFiltering() {
        return this.book.isFiltering(((RecipeBookMenu)this.menu).getRecipeBookType());
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (!this.isVisible()) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.time += $$3;
        }
        int $$4 = this.getXOrigin();
        int $$5 = this.getYOrigin();
        $$0.blit(RenderPipelines.GUI_TEXTURED, RECIPE_BOOK_LOCATION, $$4, $$5, 1.0f, 1.0f, 147, 166, 256, 256);
        this.searchBox.render($$0, $$1, $$2, $$3);
        for (RecipeBookTabButton $$6 : this.tabButtons) {
            $$6.render($$0, $$1, $$2, $$3);
        }
        this.filterButton.render($$0, $$1, $$2, $$3);
        this.recipeBookPage.render($$0, $$4, $$5, $$1, $$2, $$3);
    }

    public void renderTooltip(GuiGraphics $$0, int $$1, int $$2, @Nullable Slot $$3) {
        if (!this.isVisible()) {
            return;
        }
        this.recipeBookPage.renderTooltip($$0, $$1, $$2);
        this.ghostSlots.renderTooltip($$0, this.minecraft, $$1, $$2, $$3);
    }

    protected abstract Component getRecipeFilterName();

    public void renderGhostRecipe(GuiGraphics $$0, boolean $$1) {
        this.ghostSlots.render($$0, this.minecraft, $$1);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.recipeBookPage.mouseClicked($$0, $$1, $$2, this.getXOrigin(), this.getYOrigin(), 147, 166)) {
            RecipeDisplayId $$3 = this.recipeBookPage.getLastClickedRecipe();
            RecipeCollection $$4 = this.recipeBookPage.getLastClickedRecipeCollection();
            if ($$3 != null && $$4 != null) {
                if (!this.tryPlaceRecipe($$4, $$3)) {
                    return false;
                }
                this.lastRecipeCollection = $$4;
                this.lastRecipe = $$3;
                if (!this.isOffsetNextToMainGUI()) {
                    this.setVisible(false);
                }
            }
            return true;
        }
        if (this.searchBox != null) {
            boolean $$5;
            boolean bl = $$5 = this.magnifierIconPlacement != null && this.magnifierIconPlacement.containsPoint(Mth.floor($$0), Mth.floor($$1));
            if ($$5 || this.searchBox.mouseClicked($$0, $$1, $$2)) {
                this.searchBox.setFocused(true);
                return true;
            }
            this.searchBox.setFocused(false);
        }
        if (this.filterButton.mouseClicked($$0, $$1, $$2)) {
            boolean $$6 = this.toggleFiltering();
            this.filterButton.setStateTriggered($$6);
            this.updateFilterButtonTooltip();
            this.sendUpdateSettings();
            this.updateCollections(false, $$6);
            return true;
        }
        for (RecipeBookTabButton $$7 : this.tabButtons) {
            if (!$$7.mouseClicked($$0, $$1, $$2)) continue;
            if (this.selectedTab != $$7) {
                if (this.selectedTab != null) {
                    this.selectedTab.setStateTriggered(false);
                }
                this.selectedTab = $$7;
                this.selectedTab.setStateTriggered(true);
                this.updateCollections(true, this.isFiltering());
            }
            return true;
        }
        return false;
    }

    private boolean tryPlaceRecipe(RecipeCollection $$0, RecipeDisplayId $$1) {
        if (!$$0.isCraftable($$1) && $$1.equals((Object)this.lastPlacedRecipe)) {
            return false;
        }
        this.lastPlacedRecipe = $$1;
        this.ghostSlots.clear();
        this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, $$1, Screen.hasShiftDown());
        return true;
    }

    private boolean toggleFiltering() {
        RecipeBookType $$0 = ((RecipeBookMenu)this.menu).getRecipeBookType();
        boolean $$1 = !this.book.isFiltering($$0);
        this.book.setFiltering($$0, $$1);
        return $$1;
    }

    public boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        if (!this.isVisible()) {
            return true;
        }
        boolean $$7 = $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + $$4) || $$1 >= (double)($$3 + $$5);
        boolean $$8 = (double)($$2 - 147) < $$0 && $$0 < (double)$$2 && (double)$$3 < $$1 && $$1 < (double)($$3 + $$5);
        return $$7 && !$$8 && !this.selectedTab.isHoveredOrFocused();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        this.ignoreTextInput = false;
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if ($$0 == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
        }
        if (this.searchBox.keyPressed($$0, $$1, $$2)) {
            this.checkSearchStringUpdate();
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && $$0 != 256) {
            return true;
        }
        if (this.minecraft.options.keyChat.matches($$0, $$1) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
        }
        if (CommonInputs.selected($$0) && this.lastRecipeCollection != null && this.lastRecipe != null) {
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
            return this.tryPlaceRecipe(this.lastRecipeCollection, this.lastRecipe);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int $$0, int $$1, int $$2) {
        this.ignoreTextInput = false;
        return GuiEventListener.super.keyReleased($$0, $$1, $$2);
    }

    @Override
    public boolean a(char $$0, int $$1) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (!this.isVisible() || this.minecraft.player.isSpectator()) {
            return false;
        }
        if (this.searchBox.a($$0, $$1)) {
            this.checkSearchStringUpdate();
            return true;
        }
        return GuiEventListener.super.a($$0, $$1);
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return false;
    }

    @Override
    public void setFocused(boolean $$0) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    private void checkSearchStringUpdate() {
        String $$0 = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        this.pirateSpeechForThePeople($$0);
        if (!$$0.equals(this.lastSearch)) {
            this.updateCollections(false, this.isFiltering());
            this.lastSearch = $$0;
        }
    }

    private void pirateSpeechForThePeople(String $$0) {
        if ("excitedze".equals($$0)) {
            LanguageManager $$1 = this.minecraft.getLanguageManager();
            String $$2 = "en_pt";
            LanguageInfo $$3 = $$1.getLanguage("en_pt");
            if ($$3 == null || $$1.getSelected().equals("en_pt")) {
                return;
            }
            $$1.setSelected("en_pt");
            this.minecraft.options.languageCode = "en_pt";
            this.minecraft.reloadResourcePacks();
            this.minecraft.options.save();
        }
    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    public void recipesUpdated() {
        this.selectMatchingRecipes();
        this.updateTabs(this.isFiltering());
        if (this.isVisible()) {
            this.updateCollections(false, this.isFiltering());
        }
    }

    public void recipeShown(RecipeDisplayId $$0) {
        this.minecraft.player.removeRecipeHighlight($$0);
    }

    public void fillGhostRecipe(RecipeDisplay $$0) {
        this.ghostSlots.clear();
        ContextMap $$1 = SlotDisplayContext.fromLevel(Objects.requireNonNull(this.minecraft.level));
        this.fillGhostRecipe(this.ghostSlots, $$0, $$1);
    }

    protected abstract void fillGhostRecipe(GhostSlots var1, RecipeDisplay var2, ContextMap var3);

    protected void sendUpdateSettings() {
        if (this.minecraft.getConnection() != null) {
            RecipeBookType $$0 = ((RecipeBookMenu)this.menu).getRecipeBookType();
            boolean $$1 = this.book.getBookSettings().isOpen($$0);
            boolean $$2 = this.book.getBookSettings().isFiltering($$0);
            this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket($$0, $$1, $$2));
        }
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return this.visible ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput $$0) {
        ArrayList<AbstractWidget> $$12 = Lists.newArrayList();
        this.recipeBookPage.listButtons($$1 -> {
            if ($$1.isActive()) {
                $$12.add((AbstractWidget)$$1);
            }
        });
        $$12.add(this.searchBox);
        $$12.add(this.filterButton);
        $$12.addAll(this.tabButtons);
        Screen.NarratableSearchResult $$2 = Screen.findNarratableWidget($$12, null);
        if ($$2 != null) {
            $$2.entry.updateNarration($$0.nest());
        }
    }

    private static /* synthetic */ boolean lambda$updateCollections$3(ObjectSet $$0, RecipeCollection $$1) {
        return !$$0.contains((Object)$$1);
    }

    public record TabInfo(ItemStack primaryIcon, Optional<ItemStack> secondaryIcon, ExtendedRecipeBookCategory category) {
        public TabInfo(SearchRecipeBookCategory $$0) {
            this(new ItemStack(Items.COMPASS), Optional.empty(), $$0);
        }

        public TabInfo(Item $$0, RecipeBookCategory $$1) {
            this(new ItemStack($$0), Optional.empty(), (ExtendedRecipeBookCategory)$$1);
        }

        public TabInfo(Item $$0, Item $$1, RecipeBookCategory $$2) {
            this(new ItemStack($$0), Optional.of(new ItemStack($$1)), (ExtendedRecipeBookCategory)$$2);
        }
    }
}

