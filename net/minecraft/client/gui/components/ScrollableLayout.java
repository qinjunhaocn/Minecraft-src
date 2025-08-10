/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;

public class ScrollableLayout
implements Layout {
    private static final int SCROLLBAR_SPACING = 4;
    private static final int SCROLLBAR_RESERVE = 10;
    final Layout content;
    private final Container container;
    private int minWidth;
    private int maxHeight;

    public ScrollableLayout(Minecraft $$0, Layout $$1, int $$2) {
        this.content = $$1;
        this.container = new Container($$0, 0, $$2);
    }

    public void setMinWidth(int $$0) {
        this.minWidth = $$0;
        this.container.setWidth(Math.max(this.content.getWidth(), $$0));
    }

    public void setMaxHeight(int $$0) {
        this.maxHeight = $$0;
        this.container.setHeight(Math.min(this.content.getHeight(), $$0));
        this.container.refreshScrollAmount();
    }

    @Override
    public void arrangeElements() {
        this.content.arrangeElements();
        int $$0 = this.content.getWidth();
        this.container.setWidth(Math.max($$0 + 20, this.minWidth));
        this.container.setHeight(Math.min(this.content.getHeight(), this.maxHeight));
        this.container.refreshScrollAmount();
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> $$0) {
        $$0.accept(this.container);
    }

    @Override
    public void setX(int $$0) {
        this.container.setX($$0);
    }

    @Override
    public void setY(int $$0) {
        this.container.setY($$0);
    }

    @Override
    public int getX() {
        return this.container.getX();
    }

    @Override
    public int getY() {
        return this.container.getY();
    }

    @Override
    public int getWidth() {
        return this.container.getWidth();
    }

    @Override
    public int getHeight() {
        return this.container.getHeight();
    }

    class Container
    extends AbstractContainerWidget {
        private final Minecraft minecraft;
        private final List<AbstractWidget> children;

        public Container(Minecraft $$0, int $$1, int $$2) {
            super(0, 0, $$1, $$2, CommonComponents.EMPTY);
            this.children = new ArrayList<AbstractWidget>();
            this.minecraft = $$0;
            ScrollableLayout.this.content.visitWidgets(this.children::add);
        }

        @Override
        protected int contentHeight() {
            return ScrollableLayout.this.content.getHeight();
        }

        @Override
        protected double scrollRate() {
            return 10.0;
        }

        @Override
        protected void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
            $$0.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
            for (AbstractWidget $$4 : this.children) {
                $$4.render($$0, $$1, $$2, $$3);
            }
            $$0.disableScissor();
            this.renderScrollbar($$0);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput $$0) {
        }

        @Override
        public ScreenRectangle getBorderForArrowNavigation(ScreenDirection $$0) {
            return new ScreenRectangle(this.getX(), this.getY(), this.width, this.contentHeight());
        }

        @Override
        public void setFocused(@Nullable GuiEventListener $$0) {
            super.setFocused($$0);
            if ($$0 == null || !this.minecraft.getLastInputType().isKeyboard()) {
                return;
            }
            ScreenRectangle $$1 = this.getRectangle();
            ScreenRectangle $$2 = $$0.getRectangle();
            int $$3 = $$2.top() - $$1.top();
            int $$4 = $$2.bottom() - $$1.bottom();
            if ($$3 < 0) {
                this.setScrollAmount(this.scrollAmount() + (double)$$3 - 14.0);
            } else if ($$4 > 0) {
                this.setScrollAmount(this.scrollAmount() + (double)$$4 + 14.0);
            }
        }

        @Override
        public void setX(int $$0) {
            super.setX($$0);
            ScrollableLayout.this.content.setX($$0 + 10);
        }

        @Override
        public void setY(int $$0) {
            super.setY($$0);
            ScrollableLayout.this.content.setY($$0 - (int)this.scrollAmount());
        }

        @Override
        public void setScrollAmount(double $$0) {
            super.setScrollAmount($$0);
            ScrollableLayout.this.content.setY(this.getRectangle().top() - (int)this.scrollAmount());
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public Collection<? extends NarratableEntry> getNarratables() {
            return this.children;
        }
    }
}

