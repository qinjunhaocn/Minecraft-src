/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.RegionSelectionPreference;
import com.mojang.realmsclient.dto.ServiceQuality;
import com.mojang.realmsclient.gui.screens.configuration.RealmsSettingsTab;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class RealmsPreferredRegionSelectionScreen
extends Screen {
    private static final Component REGION_SELECTION_LABEL = Component.translatable("mco.configure.world.region_preference.title");
    private static final int SPACING = 8;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen parent;
    private final BiConsumer<RegionSelectionPreference, RealmsRegion> applySettings;
    final Map<RealmsRegion, ServiceQuality> regionServiceQuality;
    @Nullable
    private RegionSelectionList list;
    RealmsSettingsTab.RegionSelection selection;
    @Nullable
    private Button doneButton;

    public RealmsPreferredRegionSelectionScreen(Screen $$0, BiConsumer<RegionSelectionPreference, RealmsRegion> $$1, Map<RealmsRegion, ServiceQuality> $$2, RealmsSettingsTab.RegionSelection $$3) {
        super(REGION_SELECTION_LABEL);
        this.parent = $$0;
        this.applySettings = $$1;
        this.regionServiceQuality = $$2;
        this.selection = $$3;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    protected void init() {
        LinearLayout $$02 = this.layout.addToHeader(LinearLayout.vertical().spacing(8));
        $$02.defaultCellSetting().alignHorizontallyCenter();
        $$02.addChild(new StringWidget(this.getTitle(), this.font));
        this.list = this.layout.addToContents(new RegionSelectionList());
        LinearLayout $$12 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        this.doneButton = $$12.addChild(Button.builder(CommonComponents.GUI_DONE, $$0 -> {
            this.applySettings.accept(this.selection.preference(), this.selection.region());
            this.onClose();
        }).build());
        $$12.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).build());
        this.list.setSelected((RegionSelectionList.Entry)this.list.children().stream().filter($$0 -> Objects.equals((Object)$$0.regionSelection, (Object)this.selection)).findFirst().orElse(null));
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        this.list.updateSize(this.width, this.layout);
    }

    void updateButtonValidity() {
        this.doneButton.active = this.list.getSelected() != null;
    }

    class RegionSelectionList
    extends ObjectSelectionList<Entry> {
        RegionSelectionList() {
            super(RealmsPreferredRegionSelectionScreen.this.minecraft, RealmsPreferredRegionSelectionScreen.this.width, RealmsPreferredRegionSelectionScreen.this.height - 77, 40, 16);
            this.addEntry(new Entry(RegionSelectionPreference.AUTOMATIC_PLAYER, null));
            this.addEntry(new Entry(RegionSelectionPreference.AUTOMATIC_OWNER, null));
            RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.keySet().stream().map($$0 -> new Entry(RegionSelectionPreference.MANUAL, (RealmsRegion)((Object)$$0))).forEach($$1 -> this.addEntry($$1));
        }

        @Override
        public void setSelected(@Nullable Entry $$0) {
            super.setSelected($$0);
            if ($$0 != null) {
                RealmsPreferredRegionSelectionScreen.this.selection = $$0.regionSelection;
            }
            RealmsPreferredRegionSelectionScreen.this.updateButtonValidity();
        }

        class Entry
        extends ObjectSelectionList.Entry<Entry> {
            final RealmsSettingsTab.RegionSelection regionSelection;
            private final Component name;

            public Entry(@Nullable RegionSelectionPreference $$0, RealmsRegion $$1) {
                this(new RealmsSettingsTab.RegionSelection($$0, $$1));
            }

            public Entry(RealmsSettingsTab.RegionSelection $$0) {
                this.regionSelection = $$0;
                this.name = $$0.preference() == RegionSelectionPreference.MANUAL ? ($$0.region() != null ? Component.translatable($$0.region().translationKey) : Component.empty()) : Component.translatable($$0.preference().translationKey);
            }

            @Override
            public Component getNarration() {
                return Component.a("narrator.select", this.name);
            }

            @Override
            public void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
                $$0.drawString(RealmsPreferredRegionSelectionScreen.this.font, this.name, $$3 + 5, $$2 + 2, -1);
                if (this.regionSelection.region() != null && RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.containsKey((Object)this.regionSelection.region())) {
                    ServiceQuality $$10 = RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.getOrDefault((Object)this.regionSelection.region(), ServiceQuality.UNKNOWN);
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$10.getIcon(), $$3 + $$4 - 18, $$2 + 2, 10, 8);
                }
            }

            @Override
            public boolean mouseClicked(double $$0, double $$1, int $$2) {
                RegionSelectionList.this.setSelected(this);
                return super.mouseClicked($$0, $$1, $$2);
            }
        }
    }
}

