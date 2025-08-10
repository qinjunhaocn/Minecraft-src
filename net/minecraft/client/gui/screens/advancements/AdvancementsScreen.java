/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;

public class AdvancementsScreen
extends Screen
implements ClientAdvancements.Listener {
    private static final ResourceLocation WINDOW_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/advancements/window.png");
    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 140;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    public static final int WINDOW_INSIDE_WIDTH = 234;
    public static final int WINDOW_INSIDE_HEIGHT = 113;
    private static final int WINDOW_TITLE_X = 8;
    private static final int WINDOW_TITLE_Y = 6;
    private static final int BACKGROUND_TEXTURE_WIDTH = 256;
    private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
    public static final int BACKGROUND_TILE_WIDTH = 16;
    public static final int BACKGROUND_TILE_HEIGHT = 16;
    public static final int BACKGROUND_TILE_COUNT_X = 14;
    public static final int BACKGROUND_TILE_COUNT_Y = 7;
    private static final double SCROLL_SPEED = 16.0;
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
    private static final Component TITLE = Component.translatable("gui.advancements");
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    @Nullable
    private final Screen lastScreen;
    private final ClientAdvancements advancements;
    private final Map<AdvancementHolder, AdvancementTab> tabs = Maps.newLinkedHashMap();
    @Nullable
    private AdvancementTab selectedTab;
    private boolean isScrolling;

    public AdvancementsScreen(ClientAdvancements $$0) {
        this($$0, null);
    }

    public AdvancementsScreen(ClientAdvancements $$0, @Nullable Screen $$1) {
        super(TITLE);
        this.advancements = $$0;
        this.lastScreen = $$1;
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(TITLE, this.font);
        this.tabs.clear();
        this.selectedTab = null;
        this.advancements.setListener(this);
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            AdvancementTab $$02 = this.tabs.values().iterator().next();
            this.advancements.setSelectedTab($$02.getRootNode().holder(), true);
        } else {
            this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getRootNode().holder(), true);
        }
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).width(200).build());
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void removed() {
        this.advancements.setListener(null);
        ClientPacketListener $$0 = this.minecraft.getConnection();
        if ($$0 != null) {
            $$0.send(ServerboundSeenAdvancementsPacket.closedScreen());
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if ($$2 == 0) {
            int $$3 = (this.width - 252) / 2;
            int $$4 = (this.height - 140) / 2;
            for (AdvancementTab $$5 : this.tabs.values()) {
                if (!$$5.isMouseOver($$3, $$4, $$0, $$1)) continue;
                this.advancements.setSelectedTab($$5.getRootNode().holder(), true);
                break;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.minecraft.options.keyAdvancements.matches($$0, $$1)) {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        int $$4 = (this.width - 252) / 2;
        int $$5 = (this.height - 140) / 2;
        $$0.nextStratum();
        this.renderInside($$0, $$4, $$5);
        $$0.nextStratum();
        this.renderWindow($$0, $$4, $$5);
        this.renderTooltips($$0, $$1, $$2, $$4, $$5);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if ($$2 != 0) {
            this.isScrolling = false;
            return false;
        }
        if (!this.isScrolling) {
            this.isScrolling = true;
        } else if (this.selectedTab != null) {
            this.selectedTab.scroll($$3, $$4);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (this.selectedTab != null) {
            this.selectedTab.scroll($$2 * 16.0, $$3 * 16.0);
            return true;
        }
        return false;
    }

    private void renderInside(GuiGraphics $$0, int $$1, int $$2) {
        AdvancementTab $$3 = this.selectedTab;
        if ($$3 == null) {
            $$0.fill($$1 + 9, $$2 + 18, $$1 + 9 + 234, $$2 + 18 + 113, -16777216);
            int $$4 = $$1 + 9 + 117;
            $$0.drawCenteredString(this.font, NO_ADVANCEMENTS_LABEL, $$4, $$2 + 18 + 56 - this.font.lineHeight / 2, -1);
            $$0.drawCenteredString(this.font, VERY_SAD_LABEL, $$4, $$2 + 18 + 113 - this.font.lineHeight, -1);
            return;
        }
        $$3.drawContents($$0, $$1 + 9, $$2 + 18);
    }

    public void renderWindow(GuiGraphics $$0, int $$1, int $$2) {
        $$0.blit(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION, $$1, $$2, 0.0f, 0.0f, 252, 140, 256, 256);
        if (this.tabs.size() > 1) {
            for (AdvancementTab $$3 : this.tabs.values()) {
                $$3.drawTab($$0, $$1, $$2, $$3 == this.selectedTab);
            }
            for (AdvancementTab $$4 : this.tabs.values()) {
                $$4.drawIcon($$0, $$1, $$2);
            }
        }
        $$0.drawString(this.font, this.selectedTab != null ? this.selectedTab.getTitle() : TITLE, $$1 + 8, $$2 + 6, -12566464, false);
    }

    private void renderTooltips(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        if (this.selectedTab != null) {
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)($$3 + 9), (float)($$4 + 18));
            $$0.nextStratum();
            this.selectedTab.drawTooltips($$0, $$1 - $$3 - 9, $$2 - $$4 - 18, $$3, $$4);
            $$0.pose().popMatrix();
        }
        if (this.tabs.size() > 1) {
            for (AdvancementTab $$5 : this.tabs.values()) {
                if (!$$5.isMouseOver($$3, $$4, $$1, $$2)) continue;
                $$0.setTooltipForNextFrame(this.font, $$5.getTitle(), $$1, $$2);
            }
        }
    }

    @Override
    public void onAddAdvancementRoot(AdvancementNode $$0) {
        AdvancementTab $$1 = AdvancementTab.create(this.minecraft, this, this.tabs.size(), $$0);
        if ($$1 == null) {
            return;
        }
        this.tabs.put($$0.holder(), $$1);
    }

    @Override
    public void onRemoveAdvancementRoot(AdvancementNode $$0) {
    }

    @Override
    public void onAddAdvancementTask(AdvancementNode $$0) {
        AdvancementTab $$1 = this.getTab($$0);
        if ($$1 != null) {
            $$1.addAdvancement($$0);
        }
    }

    @Override
    public void onRemoveAdvancementTask(AdvancementNode $$0) {
    }

    @Override
    public void onUpdateAdvancementProgress(AdvancementNode $$0, AdvancementProgress $$1) {
        AdvancementWidget $$2 = this.getAdvancementWidget($$0);
        if ($$2 != null) {
            $$2.setProgress($$1);
        }
    }

    @Override
    public void onSelectedTabChanged(@Nullable AdvancementHolder $$0) {
        this.selectedTab = this.tabs.get((Object)$$0);
    }

    @Override
    public void onAdvancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public AdvancementWidget getAdvancementWidget(AdvancementNode $$0) {
        AdvancementTab $$1 = this.getTab($$0);
        return $$1 == null ? null : $$1.getWidget($$0.holder());
    }

    @Nullable
    private AdvancementTab getTab(AdvancementNode $$0) {
        AdvancementNode $$1 = $$0.root();
        return this.tabs.get((Object)$$1.holder());
    }
}

