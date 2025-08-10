/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.tabs;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TabNavigationBar
extends AbstractContainerEventHandler
implements Renderable,
NarratableEntry {
    private static final int NO_TAB = -1;
    private static final int MAX_WIDTH = 400;
    private static final int HEIGHT = 24;
    private static final int MARGIN = 14;
    private static final Component USAGE_NARRATION = Component.translatable("narration.tab_navigation.usage");
    private final LinearLayout layout = LinearLayout.horizontal();
    private int width;
    private final TabManager tabManager;
    private final ImmutableList<Tab> tabs;
    private final ImmutableList<TabButton> tabButtons;

    TabNavigationBar(int $$0, TabManager $$1, Iterable<Tab> $$2) {
        this.width = $$0;
        this.tabManager = $$1;
        this.tabs = ImmutableList.copyOf($$2);
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        ImmutableList.Builder $$3 = ImmutableList.builder();
        for (Tab $$4 : $$2) {
            $$3.add(this.layout.addChild(new TabButton($$1, $$4, 0, 24)));
        }
        this.tabButtons = $$3.build();
    }

    public static Builder builder(TabManager $$0, int $$1) {
        return new Builder($$0, $$1);
    }

    public void setWidth(int $$0) {
        this.width = $$0;
    }

    @Override
    public boolean isMouseOver(double $$0, double $$1) {
        return $$0 >= (double)this.layout.getX() && $$1 >= (double)this.layout.getY() && $$0 < (double)(this.layout.getX() + this.layout.getWidth()) && $$1 < (double)(this.layout.getY() + this.layout.getHeight());
    }

    @Override
    public void setFocused(boolean $$0) {
        super.setFocused($$0);
        if (this.getFocused() != null) {
            this.getFocused().setFocused($$0);
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener $$0) {
        TabButton $$1;
        if ($$0 instanceof TabButton && ($$1 = (TabButton)$$0).isActive()) {
            super.setFocused($$0);
            this.tabManager.setCurrentTab($$1.tab(), true);
        }
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        TabButton $$1;
        if (!this.isFocused() && ($$1 = this.currentTabButton()) != null) {
            return ComponentPath.path(this, ComponentPath.leaf($$1));
        }
        if ($$0 instanceof FocusNavigationEvent.TabNavigation) {
            return null;
        }
        return super.nextFocusPath($$0);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.tabButtons;
    }

    public List<Tab> getTabs() {
        return this.tabs;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return this.tabButtons.stream().map(AbstractWidget::narrationPriority).max(Comparator.naturalOrder()).orElse(NarratableEntry.NarrationPriority.NONE);
    }

    @Override
    public void updateNarration(NarrationElementOutput $$0) {
        Optional $$12 = this.tabButtons.stream().filter(AbstractWidget::isHovered).findFirst().or(() -> Optional.ofNullable(this.currentTabButton()));
        $$12.ifPresent($$1 -> {
            this.narrateListElementPosition($$0.nest(), (TabButton)$$1);
            $$1.updateNarration($$0);
        });
        if (this.isFocused()) {
            $$0.add(NarratedElementType.USAGE, USAGE_NARRATION);
        }
    }

    protected void narrateListElementPosition(NarrationElementOutput $$0, TabButton $$1) {
        int $$2;
        if (this.tabs.size() > 1 && ($$2 = this.tabButtons.indexOf($$1)) != -1) {
            $$0.add(NarratedElementType.POSITION, Component.a("narrator.position.tab", $$2 + 1, this.tabs.size()));
        }
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        $$0.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 0, this.layout.getY() + this.layout.getHeight() - 2, 0.0f, 0.0f, ((TabButton)this.tabButtons.get(0)).getX(), 2, 32, 2);
        int $$4 = ((TabButton)this.tabButtons.get(this.tabButtons.size() - 1)).getRight();
        $$0.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, $$4, this.layout.getY() + this.layout.getHeight() - 2, 0.0f, 0.0f, this.width, 2, 32, 2);
        for (TabButton $$5 : this.tabButtons) {
            $$5.render($$0, $$1, $$2, $$3);
        }
    }

    @Override
    public ScreenRectangle getRectangle() {
        return this.layout.getRectangle();
    }

    public void arrangeElements() {
        int $$0 = Math.min(400, this.width) - 28;
        int $$1 = Mth.roundToward($$0 / this.tabs.size(), 2);
        for (TabButton $$2 : this.tabButtons) {
            $$2.setWidth($$1);
        }
        this.layout.arrangeElements();
        this.layout.setX(Mth.roundToward((this.width - $$0) / 2, 2));
        this.layout.setY(0);
    }

    public void selectTab(int $$0, boolean $$1) {
        if (this.isFocused()) {
            this.setFocused((GuiEventListener)this.tabButtons.get($$0));
        } else if (((TabButton)this.tabButtons.get($$0)).isActive()) {
            this.tabManager.setCurrentTab((Tab)this.tabs.get($$0), $$1);
        }
    }

    public void setTabActiveState(int $$0, boolean $$1) {
        if ($$0 >= 0 && $$0 < this.tabButtons.size()) {
            ((TabButton)this.tabButtons.get((int)$$0)).active = $$1;
        }
    }

    public void setTabTooltip(int $$0, @Nullable Tooltip $$1) {
        if ($$0 >= 0 && $$0 < this.tabButtons.size()) {
            ((TabButton)this.tabButtons.get($$0)).setTooltip($$1);
        }
    }

    public boolean keyPressed(int $$0) {
        int $$1;
        if (Screen.hasControlDown() && ($$1 = this.getNextTabIndex($$0)) != -1) {
            this.selectTab(Mth.clamp($$1, 0, this.tabs.size() - 1), true);
            return true;
        }
        return false;
    }

    private int getNextTabIndex(int $$0) {
        return this.getNextTabIndex(this.currentTabIndex(), $$0);
    }

    private int getNextTabIndex(int $$0, int $$1) {
        if ($$1 >= 49 && $$1 <= 57) {
            return $$1 - 49;
        }
        if ($$1 == 258 && $$0 != -1) {
            int $$2 = Screen.hasShiftDown() ? $$0 - 1 : $$0 + 1;
            int $$3 = Math.floorMod($$2, this.tabs.size());
            if (((TabButton)this.tabButtons.get((int)$$3)).active) {
                return $$3;
            }
            return this.getNextTabIndex($$3, $$1);
        }
        return -1;
    }

    private int currentTabIndex() {
        Tab $$0 = this.tabManager.getCurrentTab();
        int $$1 = this.tabs.indexOf($$0);
        return $$1 != -1 ? $$1 : -1;
    }

    @Nullable
    private TabButton currentTabButton() {
        int $$0 = this.currentTabIndex();
        return $$0 != -1 ? (TabButton)this.tabButtons.get($$0) : null;
    }

    public static class Builder {
        private final int width;
        private final TabManager tabManager;
        private final List<Tab> tabs = new ArrayList<Tab>();

        Builder(TabManager $$0, int $$1) {
            this.tabManager = $$0;
            this.width = $$1;
        }

        public Builder a(Tab ... $$0) {
            Collections.addAll(this.tabs, $$0);
            return this;
        }

        public TabNavigationBar build() {
            return new TabNavigationBar(this.width, this.tabManager, this.tabs);
        }
    }
}

