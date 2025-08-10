/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class StatsScreen
extends Screen {
    private static final Component TITLE = Component.translatable("gui.stats");
    static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
    static final ResourceLocation HEADER_SPRITE = ResourceLocation.withDefaultNamespace("statistics/header");
    static final ResourceLocation SORT_UP_SPRITE = ResourceLocation.withDefaultNamespace("statistics/sort_up");
    static final ResourceLocation SORT_DOWN_SPRITE = ResourceLocation.withDefaultNamespace("statistics/sort_down");
    private static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
    static final Component NO_VALUE_DISPLAY = Component.translatable("stats.none");
    private static final Component GENERAL_BUTTON = Component.translatable("stat.generalButton");
    private static final Component ITEMS_BUTTON = Component.translatable("stat.itemsButton");
    private static final Component MOBS_BUTTON = Component.translatable("stat.mobsButton");
    protected final Screen lastScreen;
    private static final int LIST_WIDTH = 280;
    private static final int PADDING = 5;
    private static final int FOOTER_HEIGHT = 58;
    private HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 58);
    @Nullable
    private GeneralStatisticsList statsList;
    @Nullable
    ItemStatisticsList itemStatsList;
    @Nullable
    private MobsStatisticsList mobsStatsList;
    final StatsCounter stats;
    @Nullable
    private ObjectSelectionList<?> activeList;
    private boolean isLoading = true;

    public StatsScreen(Screen $$0, StatsCounter $$1) {
        super(TITLE);
        this.lastScreen = $$0;
        this.stats = $$1;
    }

    @Override
    protected void init() {
        this.layout.addToContents(new LoadingDotsWidget(this.font, PENDING_TEXT));
        this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
    }

    public void initLists() {
        this.statsList = new GeneralStatisticsList(this.minecraft);
        this.itemStatsList = new ItemStatisticsList(this.minecraft);
        this.mobsStatsList = new MobsStatisticsList(this.minecraft);
    }

    public void initButtons() {
        HeaderAndFooterLayout $$02 = new HeaderAndFooterLayout(this, 33, 58);
        $$02.addTitleHeader(TITLE, this.font);
        LinearLayout $$12 = $$02.addToFooter(LinearLayout.vertical()).spacing(5);
        $$12.defaultCellSetting().alignHorizontallyCenter();
        LinearLayout $$2 = $$12.addChild(LinearLayout.horizontal()).spacing(5);
        $$2.addChild(Button.builder(GENERAL_BUTTON, $$0 -> this.setActiveList(this.statsList)).width(120).build());
        Button $$3 = $$2.addChild(Button.builder(ITEMS_BUTTON, $$0 -> this.setActiveList(this.itemStatsList)).width(120).build());
        Button $$4 = $$2.addChild(Button.builder(MOBS_BUTTON, $$0 -> this.setActiveList(this.mobsStatsList)).width(120).build());
        $$12.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).width(200).build());
        if (this.itemStatsList != null && this.itemStatsList.children().isEmpty()) {
            $$3.active = false;
        }
        if (this.mobsStatsList != null && this.mobsStatsList.children().isEmpty()) {
            $$4.active = false;
        }
        this.layout = $$02;
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.activeList != null) {
            this.activeList.updateSize(this.width, this.layout);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public void onStatsUpdated() {
        if (this.isLoading) {
            this.initLists();
            this.setActiveList(this.statsList);
            this.initButtons();
            this.setInitialFocus();
            this.isLoading = false;
        }
    }

    public void setActiveList(@Nullable ObjectSelectionList<?> $$0) {
        if (this.activeList != null) {
            this.removeWidget(this.activeList);
        }
        if ($$0 != null) {
            this.addRenderableWidget($$0);
            this.activeList = $$0;
            this.repositionElements();
        }
    }

    static String getTranslationKey(Stat<ResourceLocation> $$0) {
        return "stat." + $$0.getValue().toString().replace(':', '.');
    }

    class GeneralStatisticsList
    extends ObjectSelectionList<Entry> {
        public GeneralStatisticsList(Minecraft $$02) {
            super($$02, StatsScreen.this.width, StatsScreen.this.height - 33 - 58, 33, 14);
            ObjectArrayList $$1 = new ObjectArrayList(Stats.CUSTOM.iterator());
            $$1.sort(Comparator.comparing($$0 -> I18n.a(StatsScreen.getTranslationKey($$0), new Object[0])));
            for (Stat $$2 : $$1) {
                this.addEntry(new Entry($$2));
            }
        }

        @Override
        public int getRowWidth() {
            return 280;
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private final Stat<ResourceLocation> stat;
            private final Component statDisplay;

            Entry(Stat<ResourceLocation> $$0) {
                this.stat = $$0;
                this.statDisplay = Component.translatable(StatsScreen.getTranslationKey($$0));
            }

            private String getValueText() {
                return this.stat.format(StatsScreen.this.stats.getValue(this.stat));
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                int $$10 = $$2 + $$5 / 2 - ((StatsScreen)StatsScreen.this).font.lineHeight / 2;
                int $$11 = $$1 % 2 == 0 ? -1 : -4539718;
                $$0.drawString(StatsScreen.this.font, this.statDisplay, $$3 + 2, $$10, $$11);
                String $$12 = this.getValueText();
                $$0.drawString(StatsScreen.this.font, $$12, $$3 + $$4 - StatsScreen.this.font.width($$12) - 4, $$10, $$11);
            }

            @Override
            public Component getNarration() {
                return Component.a("narrator.select", Component.empty().append(this.statDisplay).append(CommonComponents.SPACE).append(this.getValueText()));
            }
        }
    }

    class ItemStatisticsList
    extends ObjectSelectionList<ItemRow> {
        private static final int SLOT_BG_SIZE = 18;
        private static final int SLOT_STAT_HEIGHT = 22;
        private static final int SLOT_BG_Y = 1;
        private static final int SORT_NONE = 0;
        private static final int SORT_DOWN = -1;
        private static final int SORT_UP = 1;
        private final ResourceLocation[] iconSprites;
        protected final List<StatType<Block>> blockColumns;
        protected final List<StatType<Item>> itemColumns;
        protected final Comparator<ItemRow> itemStatSorter;
        @Nullable
        protected StatType<?> sortColumn;
        protected int headerPressed;
        protected int sortOrder;

        public ItemStatisticsList(Minecraft $$0) {
            super($$0, StatsScreen.this.width, StatsScreen.this.height - 33 - 58, 33, 22, 22);
            this.iconSprites = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("statistics/block_mined"), ResourceLocation.withDefaultNamespace("statistics/item_broken"), ResourceLocation.withDefaultNamespace("statistics/item_crafted"), ResourceLocation.withDefaultNamespace("statistics/item_used"), ResourceLocation.withDefaultNamespace("statistics/item_picked_up"), ResourceLocation.withDefaultNamespace("statistics/item_dropped")};
            this.itemStatSorter = new ItemRowComparator();
            this.headerPressed = -1;
            this.blockColumns = Lists.newArrayList();
            this.blockColumns.add(Stats.BLOCK_MINED);
            this.itemColumns = Lists.newArrayList(Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED);
            Set<Item> $$1 = Sets.newIdentityHashSet();
            for (Item $$2 : BuiltInRegistries.ITEM) {
                boolean $$3 = false;
                for (StatType<Item> statType : this.itemColumns) {
                    if (!statType.contains($$2) || StatsScreen.this.stats.getValue(statType.get($$2)) <= 0) continue;
                    $$3 = true;
                }
                if (!$$3) continue;
                $$1.add($$2);
            }
            for (Block $$5 : BuiltInRegistries.BLOCK) {
                boolean $$6 = false;
                for (StatType<FeatureElement> statType : this.blockColumns) {
                    if (!statType.contains($$5) || StatsScreen.this.stats.getValue(statType.get($$5)) <= 0) continue;
                    $$6 = true;
                }
                if (!$$6) continue;
                $$1.add($$5.asItem());
            }
            $$1.remove(Items.AIR);
            for (Item $$8 : $$1) {
                this.addEntry(new ItemRow($$8));
            }
        }

        int getColumnX(int $$0) {
            return 75 + 40 * $$0;
        }

        @Override
        protected void renderHeader(GuiGraphics $$0, int $$1, int $$2) {
            if (!this.minecraft.mouseHandler.isLeftPressed()) {
                this.headerPressed = -1;
            }
            for (int $$3 = 0; $$3 < this.iconSprites.length; ++$$3) {
                ResourceLocation $$4 = this.headerPressed == $$3 ? SLOT_SPRITE : HEADER_SPRITE;
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$4, $$1 + this.getColumnX($$3) - 18, $$2 + 1, 18, 18);
            }
            if (this.sortColumn != null) {
                int $$5 = this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
                ResourceLocation $$6 = this.sortOrder == 1 ? SORT_UP_SPRITE : SORT_DOWN_SPRITE;
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$6, $$1 + $$5, $$2 + 1, 18, 18);
            }
            for (int $$7 = 0; $$7 < this.iconSprites.length; ++$$7) {
                int $$8 = this.headerPressed == $$7 ? 1 : 0;
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.iconSprites[$$7], $$1 + this.getColumnX($$7) - 18 + $$8, $$2 + 1 + $$8, 18, 18);
            }
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            boolean $$3 = super.mouseClicked($$0, $$1, $$2);
            if (!$$3 && this.clickedHeader((int)($$0 - ((double)this.getX() + (double)this.width / 2.0 - (double)this.getRowWidth() / 2.0)), (int)($$1 - (double)this.getY()) + (int)this.scrollAmount() - 4)) {
                return true;
            }
            return $$3;
        }

        protected boolean clickedHeader(int $$0, int $$1) {
            this.headerPressed = -1;
            for (int $$2 = 0; $$2 < this.iconSprites.length; ++$$2) {
                int $$3 = $$0 - this.getColumnX($$2);
                if ($$3 < -36 || $$3 > 0) continue;
                this.headerPressed = $$2;
                break;
            }
            if (this.headerPressed >= 0) {
                this.sortByColumn(this.getColumn(this.headerPressed));
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                return true;
            }
            return false;
        }

        @Override
        public int getRowWidth() {
            return 280;
        }

        private StatType<?> getColumn(int $$0) {
            return $$0 < this.blockColumns.size() ? this.blockColumns.get($$0) : this.itemColumns.get($$0 - this.blockColumns.size());
        }

        private int getColumnIndex(StatType<?> $$0) {
            int $$1 = this.blockColumns.indexOf($$0);
            if ($$1 >= 0) {
                return $$1;
            }
            int $$2 = this.itemColumns.indexOf($$0);
            if ($$2 >= 0) {
                return $$2 + this.blockColumns.size();
            }
            return -1;
        }

        @Override
        protected void renderDecorations(GuiGraphics $$0, int $$1, int $$2) {
            if ($$2 < this.getY() || $$2 > this.getBottom()) {
                return;
            }
            ItemRow $$3 = (ItemRow)this.getHovered();
            int $$4 = this.getRowLeft();
            if ($$3 != null) {
                if ($$1 < $$4 || $$1 > $$4 + 18) {
                    return;
                }
                Item $$5 = $$3.getItem();
                $$0.setTooltipForNextFrame(StatsScreen.this.font, $$5.getName(), $$1, $$2, $$5.components().get(DataComponents.TOOLTIP_STYLE));
            } else {
                Component $$6 = null;
                int $$7 = $$1 - $$4;
                for (int $$8 = 0; $$8 < this.iconSprites.length; ++$$8) {
                    int $$9 = this.getColumnX($$8);
                    if ($$7 < $$9 - 18 || $$7 > $$9) continue;
                    $$6 = this.getColumn($$8).getDisplayName();
                    break;
                }
                if ($$6 != null) {
                    $$0.setTooltipForNextFrame(StatsScreen.this.font, $$6, $$1, $$2);
                }
            }
        }

        protected void sortByColumn(StatType<?> $$0) {
            if ($$0 != this.sortColumn) {
                this.sortColumn = $$0;
                this.sortOrder = -1;
            } else if (this.sortOrder == -1) {
                this.sortOrder = 1;
            } else {
                this.sortColumn = null;
                this.sortOrder = 0;
            }
            this.children().sort(this.itemStatSorter);
        }

        class ItemRowComparator
        implements Comparator<ItemRow> {
            ItemRowComparator() {
            }

            @Override
            public int compare(ItemRow $$0, ItemRow $$1) {
                int $$11;
                int $$10;
                Item $$2 = $$0.getItem();
                Item $$3 = $$1.getItem();
                if (ItemStatisticsList.this.sortColumn == null) {
                    boolean $$4 = false;
                    boolean $$5 = false;
                } else if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
                    StatType<?> $$6 = ItemStatisticsList.this.sortColumn;
                    int $$7 = $$2 instanceof BlockItem ? StatsScreen.this.stats.getValue($$6, ((BlockItem)$$2).getBlock()) : -1;
                    int $$8 = $$3 instanceof BlockItem ? StatsScreen.this.stats.getValue($$6, ((BlockItem)$$3).getBlock()) : -1;
                } else {
                    StatType<?> $$9 = ItemStatisticsList.this.sortColumn;
                    $$10 = StatsScreen.this.stats.getValue($$9, $$2);
                    $$11 = StatsScreen.this.stats.getValue($$9, $$3);
                }
                if ($$10 == $$11) {
                    return ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId($$2), Item.getId($$3));
                }
                return ItemStatisticsList.this.sortOrder * Integer.compare($$10, $$11);
            }

            @Override
            public /* synthetic */ int compare(Object object, Object object2) {
                return this.compare((ItemRow)object, (ItemRow)object2);
            }
        }

        class ItemRow
        extends ObjectSelectionList.Entry<ItemRow> {
            private final Item item;

            ItemRow(Item $$0) {
                this.item = $$0;
            }

            public Item getItem() {
                return this.item;
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, $$3, $$2, 18, 18);
                $$0.renderFakeItem(this.item.getDefaultInstance(), $$3 + 1, $$2 + 1);
                if (StatsScreen.this.itemStatsList != null) {
                    for (int $$10 = 0; $$10 < StatsScreen.this.itemStatsList.blockColumns.size(); ++$$10) {
                        Stat<?> $$13;
                        Item item = this.item;
                        if (item instanceof BlockItem) {
                            BlockItem $$11 = (BlockItem)item;
                            Stat<Block> $$12 = StatsScreen.this.itemStatsList.blockColumns.get($$10).get($$11.getBlock());
                        } else {
                            $$13 = null;
                        }
                        this.renderStat($$0, $$13, $$3 + ItemStatisticsList.this.getColumnX($$10), $$2 + $$5 / 2 - ((StatsScreen)StatsScreen.this).font.lineHeight / 2, $$1 % 2 == 0);
                    }
                    for (int $$14 = 0; $$14 < StatsScreen.this.itemStatsList.itemColumns.size(); ++$$14) {
                        this.renderStat($$0, StatsScreen.this.itemStatsList.itemColumns.get($$14).get(this.item), $$3 + ItemStatisticsList.this.getColumnX($$14 + StatsScreen.this.itemStatsList.blockColumns.size()), $$2 + $$5 / 2 - ((StatsScreen)StatsScreen.this).font.lineHeight / 2, $$1 % 2 == 0);
                    }
                }
            }

            protected void renderStat(GuiGraphics $$0, @Nullable Stat<?> $$1, int $$2, int $$3, boolean $$4) {
                Component $$5 = $$1 == null ? NO_VALUE_DISPLAY : Component.literal($$1.format(StatsScreen.this.stats.getValue($$1)));
                $$0.drawString(StatsScreen.this.font, $$5, $$2 - StatsScreen.this.font.width($$5), $$3, $$4 ? -1 : -4539718);
            }

            @Override
            public Component getNarration() {
                return Component.a("narrator.select", this.item.getName());
            }
        }
    }

    class MobsStatisticsList
    extends ObjectSelectionList<MobRow> {
        public MobsStatisticsList(Minecraft $$0) {
            super($$0, StatsScreen.this.width, StatsScreen.this.height - 33 - 58, 33, ((StatsScreen)StatsScreen.this).font.lineHeight * 4);
            for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE) {
                if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(entityType)) <= 0 && StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(entityType)) <= 0) continue;
                this.addEntry(new MobRow(entityType));
            }
        }

        @Override
        public int getRowWidth() {
            return 280;
        }

        class MobRow
        extends ObjectSelectionList.Entry<MobRow> {
            private final Component mobName;
            private final Component kills;
            private final Component killedBy;
            private final boolean hasKills;
            private final boolean wasKilledBy;

            public MobRow(EntityType<?> $$0) {
                this.mobName = $$0.getDescription();
                int $$1 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get($$0));
                if ($$1 == 0) {
                    this.kills = Component.a("stat_type.minecraft.killed.none", this.mobName);
                    this.hasKills = false;
                } else {
                    this.kills = Component.a("stat_type.minecraft.killed", $$1, this.mobName);
                    this.hasKills = true;
                }
                int $$2 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get($$0));
                if ($$2 == 0) {
                    this.killedBy = Component.a("stat_type.minecraft.killed_by.none", this.mobName);
                    this.wasKilledBy = false;
                } else {
                    this.killedBy = Component.a("stat_type.minecraft.killed_by", this.mobName, $$2);
                    this.wasKilledBy = true;
                }
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                $$0.drawString(StatsScreen.this.font, this.mobName, $$3 + 2, $$2 + 1, -1);
                $$0.drawString(StatsScreen.this.font, this.kills, $$3 + 2 + 10, $$2 + 1 + ((StatsScreen)StatsScreen.this).font.lineHeight, this.hasKills ? -4539718 : -8355712);
                $$0.drawString(StatsScreen.this.font, this.killedBy, $$3 + 2 + 10, $$2 + 1 + ((StatsScreen)StatsScreen.this).font.lineHeight * 2, this.wasKilledBy ? -4539718 : -8355712);
            }

            @Override
            public Component getNarration() {
                return Component.a("narrator.select", CommonComponents.a(this.kills, this.killedBy));
            }
        }
    }
}

