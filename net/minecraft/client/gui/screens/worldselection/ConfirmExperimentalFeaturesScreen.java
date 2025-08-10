/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens.worldselection;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.flag.FeatureFlags;

public class ConfirmExperimentalFeaturesScreen
extends Screen {
    private static final Component TITLE = Component.translatable("selectWorld.experimental.title");
    private static final Component MESSAGE = Component.translatable("selectWorld.experimental.message");
    private static final Component DETAILS_BUTTON = Component.translatable("selectWorld.experimental.details");
    private static final int COLUMN_SPACING = 10;
    private static final int DETAILS_BUTTON_WIDTH = 100;
    private final BooleanConsumer callback;
    final Collection<Pack> enabledPacks;
    private final GridLayout layout = new GridLayout().columnSpacing(10).rowSpacing(20);

    public ConfirmExperimentalFeaturesScreen(Collection<Pack> $$0, BooleanConsumer $$1) {
        super(TITLE);
        this.enabledPacks = $$0;
        this.callback = $$1;
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(super.getNarrationMessage(), MESSAGE);
    }

    @Override
    protected void init() {
        super.init();
        GridLayout.RowHelper $$02 = this.layout.createRowHelper(2);
        LayoutSettings $$12 = $$02.newCellSettings().alignHorizontallyCenter();
        $$02.addChild(new StringWidget(this.title, this.font), 2, $$12);
        MultiLineTextWidget $$2 = $$02.addChild(new MultiLineTextWidget(MESSAGE, this.font).setCentered(true), 2, $$12);
        $$2.setMaxWidth(310);
        $$02.addChild(Button.builder(DETAILS_BUTTON, $$0 -> this.minecraft.setScreen(new DetailsScreen())).width(100).build(), 2, $$12);
        $$02.addChild(Button.builder(CommonComponents.GUI_PROCEED, $$0 -> this.callback.accept(true)).build());
        $$02.addChild(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.callback.accept(false)).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.layout.arrangeElements();
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        FrameLayout.alignInRectangle(this.layout, 0, 0, this.width, this.height, 0.5f, 0.5f);
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    class DetailsScreen
    extends Screen {
        private static final Component TITLE = Component.translatable("selectWorld.experimental.details.title");
        final HeaderAndFooterLayout layout;
        @Nullable
        private PackList list;

        DetailsScreen() {
            super(TITLE);
            this.layout = new HeaderAndFooterLayout(this);
        }

        @Override
        protected void init() {
            this.layout.addTitleHeader(TITLE, this.font);
            this.list = this.layout.addToContents(new PackList(this, this.minecraft, ConfirmExperimentalFeaturesScreen.this.enabledPacks));
            this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).build());
            this.layout.visitWidgets($$1 -> {
                AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
            });
            this.repositionElements();
        }

        @Override
        protected void repositionElements() {
            if (this.list != null) {
                this.list.updateSize(this.width, this.layout);
            }
            this.layout.arrangeElements();
        }

        @Override
        public void onClose() {
            this.minecraft.setScreen(ConfirmExperimentalFeaturesScreen.this);
        }

        class PackList
        extends ObjectSelectionList<PackListEntry> {
            public PackList(DetailsScreen detailsScreen, Minecraft $$0, Collection<Pack> $$1) {
                super($$0, detailsScreen.width, detailsScreen.layout.getContentHeight(), detailsScreen.layout.getHeaderHeight(), ($$0.font.lineHeight + 2) * 3);
                for (Pack $$2 : $$1) {
                    String $$3 = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_SET, $$2.getRequestedFeatures());
                    if ($$3.isEmpty()) continue;
                    MutableComponent $$4 = ComponentUtils.mergeStyles($$2.getTitle().copy(), Style.EMPTY.withBold(true));
                    MutableComponent $$5 = Component.a("selectWorld.experimental.details.entry", $$3);
                    this.addEntry(detailsScreen.new PackListEntry($$4, $$5, MultiLineLabel.create(detailsScreen.font, $$5, this.getRowWidth())));
                }
            }

            @Override
            public int getRowWidth() {
                return this.width * 3 / 4;
            }
        }

        class PackListEntry
        extends ObjectSelectionList.Entry<PackListEntry> {
            private final Component packId;
            private final Component message;
            private final MultiLineLabel splitMessage;

            PackListEntry(Component $$0, Component $$1, MultiLineLabel $$2) {
                this.packId = $$0;
                this.message = $$1;
                this.splitMessage = $$2;
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                $$0.drawString(((DetailsScreen)DetailsScreen.this).minecraft.font, this.packId, $$3, $$2, -1);
                this.splitMessage.renderLeftAligned($$0, $$3, $$2 + 12, ((DetailsScreen)DetailsScreen.this).font.lineHeight, -1);
            }

            @Override
            public Component getNarration() {
                return Component.a("narrator.select", CommonComponents.a(this.packId, this.message));
            }
        }
    }
}

