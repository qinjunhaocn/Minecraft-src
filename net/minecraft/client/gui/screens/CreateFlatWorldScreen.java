/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PresetFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class CreateFlatWorldScreen
extends Screen {
    private static final Component TITLE = Component.translatable("createWorld.customize.flat.title");
    static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
    private static final int SLOT_BG_SIZE = 18;
    private static final int SLOT_STAT_HEIGHT = 20;
    private static final int SLOT_BG_X = 1;
    private static final int SLOT_BG_Y = 1;
    private static final int SLOT_FG_X = 2;
    private static final int SLOT_FG_Y = 2;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 64);
    protected final CreateWorldScreen parent;
    private final Consumer<FlatLevelGeneratorSettings> applySettings;
    FlatLevelGeneratorSettings generator;
    @Nullable
    private DetailsList list;
    @Nullable
    private Button deleteLayerButton;

    public CreateFlatWorldScreen(CreateWorldScreen $$0, Consumer<FlatLevelGeneratorSettings> $$1, FlatLevelGeneratorSettings $$2) {
        super(TITLE);
        this.parent = $$0;
        this.applySettings = $$1;
        this.generator = $$2;
    }

    public FlatLevelGeneratorSettings settings() {
        return this.generator;
    }

    public void setConfig(FlatLevelGeneratorSettings $$0) {
        this.generator = $$0;
        if (this.list != null) {
            this.list.resetRows();
            this.updateButtonValidity();
        }
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);
        this.list = this.layout.addToContents(new DetailsList());
        LinearLayout $$02 = this.layout.addToFooter(LinearLayout.vertical().spacing(4));
        $$02.defaultCellSetting().alignVerticallyMiddle();
        LinearLayout $$1 = $$02.addChild(LinearLayout.horizontal().spacing(8));
        LinearLayout $$2 = $$02.addChild(LinearLayout.horizontal().spacing(8));
        this.deleteLayerButton = $$1.addChild(Button.builder(Component.translatable("createWorld.customize.flat.removeLayer"), $$0 -> {
            if (!this.hasValidSelection()) {
                return;
            }
            List<FlatLayerInfo> $$1 = this.generator.getLayersInfo();
            int $$2 = this.list.children().indexOf(this.list.getSelected());
            int $$3 = $$1.size() - $$2 - 1;
            $$1.remove($$3);
            this.list.setSelected($$1.isEmpty() ? null : (DetailsList.Entry)this.list.children().get(Math.min($$2, $$1.size() - 1)));
            this.generator.updateLayers();
            this.list.resetRows();
            this.updateButtonValidity();
        }).build());
        $$1.addChild(Button.builder(Component.translatable("createWorld.customize.presets"), $$0 -> {
            this.minecraft.setScreen(new PresetFlatWorldScreen(this));
            this.generator.updateLayers();
            this.updateButtonValidity();
        }).build());
        $$2.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.applySettings.accept(this.generator);
            this.onClose();
            this.generator.updateLayers();
        }).build());
        $$2.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            this.onClose();
            this.generator.updateLayers();
        }).build());
        this.generator.updateLayers();
        this.updateButtonValidity();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
        this.layout.arrangeElements();
    }

    void updateButtonValidity() {
        if (this.deleteLayerButton != null) {
            this.deleteLayerButton.active = this.hasValidSelection();
        }
    }

    private boolean hasValidSelection() {
        return this.list != null && this.list.getSelected() != null;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    class DetailsList
    extends ObjectSelectionList<Entry> {
        private static final Component LAYER_MATERIAL_TITLE = Component.translatable("createWorld.customize.flat.tile").withStyle(ChatFormatting.UNDERLINE);
        private static final Component HEIGHT_TITLE = Component.translatable("createWorld.customize.flat.height").withStyle(ChatFormatting.UNDERLINE);

        public DetailsList() {
            super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height - 103, 43, 24, (int)((double)CreateFlatWorldScreen.this.font.lineHeight * 1.5));
            for (int $$0 = 0; $$0 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++$$0) {
                this.addEntry(new Entry());
            }
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            CreateFlatWorldScreen.this.updateButtonValidity();
        }

        public void resetRows() {
            int $$0 = this.children().indexOf(this.getSelected());
            this.clearEntries();
            for (int $$1 = 0; $$1 < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++$$1) {
                this.addEntry(new Entry());
            }
            List $$2 = this.children();
            if ($$0 >= 0 && $$0 < $$2.size()) {
                this.setSelected((Entry)$$2.get($$0));
            }
        }

        @Override
        protected void renderHeader(GuiGraphics $$0, int $$1, int $$2) {
            $$0.drawString(CreateFlatWorldScreen.this.font, LAYER_MATERIAL_TITLE, $$1, $$2, -1);
            $$0.drawString(CreateFlatWorldScreen.this.font, HEIGHT_TITLE, $$1 + this.getRowWidth() - CreateFlatWorldScreen.this.font.width(HEIGHT_TITLE) - 8, $$2, -1);
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            Entry() {
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                MutableComponent $$16;
                FlatLayerInfo $$10 = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - $$1 - 1);
                BlockState $$11 = $$10.getBlockState();
                ItemStack $$12 = this.getDisplayItem($$11);
                this.blitSlot($$0, $$3, $$2, $$12);
                int $$13 = $$2 + $$5 / 2 - CreateFlatWorldScreen.this.font.lineHeight / 2;
                $$0.drawString(CreateFlatWorldScreen.this.font, $$12.getHoverName(), $$3 + 18 + 5, $$13, -1);
                if ($$1 == 0) {
                    MutableComponent $$14 = Component.a("createWorld.customize.flat.layer.top", $$10.getHeight());
                } else if ($$1 == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
                    MutableComponent $$15 = Component.a("createWorld.customize.flat.layer.bottom", $$10.getHeight());
                } else {
                    $$16 = Component.a("createWorld.customize.flat.layer", $$10.getHeight());
                }
                $$0.drawString(CreateFlatWorldScreen.this.font, $$16, $$3 + $$4 - CreateFlatWorldScreen.this.font.width($$16) - 8, $$13, -1);
            }

            private ItemStack getDisplayItem(BlockState $$0) {
                Item $$1 = $$0.getBlock().asItem();
                if ($$1 == Items.AIR) {
                    if ($$0.is(Blocks.WATER)) {
                        $$1 = Items.WATER_BUCKET;
                    } else if ($$0.is(Blocks.LAVA)) {
                        $$1 = Items.LAVA_BUCKET;
                    }
                }
                return new ItemStack($$1);
            }

            @Override
            public Component getNarration() {
                FlatLayerInfo $$0 = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - DetailsList.this.children().indexOf(this) - 1);
                ItemStack $$1 = this.getDisplayItem($$0.getBlockState());
                if (!$$1.isEmpty()) {
                    return Component.a("narrator.select", $$1.getHoverName());
                }
                return CommonComponents.EMPTY;
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                DetailsList.this.setSelected(this);
                return super.mouseClicked($$0, $$1, $$2);
            }

            private void blitSlot(GuiGraphics $$0, int $$1, int $$2, ItemStack $$3) {
                this.blitSlotBg($$0, $$1 + 1, $$2 + 1);
                if (!$$3.isEmpty()) {
                    $$0.renderFakeItem($$3, $$1 + 2, $$2 + 2);
                }
            }

            private void blitSlotBg(GuiGraphics $$0, int $$1, int $$2) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, $$1, $$2, 18, 18);
            }
        }
    }
}

