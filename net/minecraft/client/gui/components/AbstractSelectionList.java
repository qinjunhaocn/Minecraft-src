/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractSelectionList<E extends Entry<E>>
extends AbstractContainerWidget {
    private static final ResourceLocation MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/menu_list_background.png");
    private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
    protected final Minecraft minecraft;
    protected final int itemHeight;
    private final List<E> children = new TrackedList();
    protected boolean centerListVertically = true;
    private boolean renderHeader;
    protected int headerHeight;
    @Nullable
    private E selected;
    @Nullable
    private E hovered;

    public AbstractSelectionList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4) {
        super(0, $$3, $$1, $$2, CommonComponents.EMPTY);
        this.minecraft = $$0;
        this.itemHeight = $$4;
    }

    public AbstractSelectionList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        this($$0, $$1, $$2, $$3, $$4);
        this.renderHeader = true;
        this.headerHeight = $$5;
    }

    @Nullable
    public E getSelected() {
        return this.selected;
    }

    public void setSelectedIndex(int $$0) {
        if ($$0 == -1) {
            this.setSelected(null);
        } else if (this.getItemCount() != 0) {
            this.setSelected(this.getEntry($$0));
        }
    }

    public void setSelected(@Nullable E $$0) {
        this.selected = $$0;
    }

    public E getFirstElement() {
        return (E)((Entry)this.children.get(0));
    }

    @Nullable
    public E getFocused() {
        return (E)((Entry)super.getFocused());
    }

    public final List<E> children() {
        return this.children;
    }

    protected void clearEntries() {
        this.children.clear();
        this.selected = null;
    }

    public void replaceEntries(Collection<E> $$0) {
        this.clearEntries();
        this.children.addAll($$0);
    }

    protected E getEntry(int $$0) {
        return (E)((Entry)this.children().get($$0));
    }

    protected int addEntry(E $$0) {
        this.children.add($$0);
        return this.children.size() - 1;
    }

    protected void addEntryToTop(E $$0) {
        double $$1 = (double)this.maxScrollAmount() - this.scrollAmount();
        this.children.add(0, $$0);
        this.setScrollAmount((double)this.maxScrollAmount() - $$1);
    }

    protected boolean removeEntryFromTop(E $$0) {
        double $$1 = (double)this.maxScrollAmount() - this.scrollAmount();
        boolean $$2 = this.removeEntry($$0);
        this.setScrollAmount((double)this.maxScrollAmount() - $$1);
        return $$2;
    }

    protected int getItemCount() {
        return this.children().size();
    }

    protected boolean isSelectedItem(int $$0) {
        return Objects.equals(this.getSelected(), this.children().get($$0));
    }

    @Nullable
    protected final E getEntryAtPosition(double $$0, double $$1) {
        int $$2 = this.getRowWidth() / 2;
        int $$3 = this.getX() + this.width / 2;
        int $$4 = $$3 - $$2;
        int $$5 = $$3 + $$2;
        int $$6 = Mth.floor($$1 - (double)this.getY()) - this.headerHeight + (int)this.scrollAmount() - 4;
        int $$7 = $$6 / this.itemHeight;
        if ($$0 >= (double)$$4 && $$0 <= (double)$$5 && $$7 >= 0 && $$6 >= 0 && $$7 < this.getItemCount()) {
            return (E)((Entry)this.children().get($$7));
        }
        return null;
    }

    public void updateSize(int $$0, HeaderAndFooterLayout $$1) {
        this.updateSizeAndPosition($$0, $$1.getContentHeight(), $$1.getHeaderHeight());
    }

    public void updateSizeAndPosition(int $$0, int $$1, int $$2) {
        this.setSize($$0, $$1);
        this.setPosition(0, $$2);
        this.refreshScrollAmount();
    }

    @Override
    protected int contentHeight() {
        return this.getItemCount() * this.itemHeight + this.headerHeight + 4;
    }

    protected void renderHeader(GuiGraphics $$0, int $$1, int $$2) {
    }

    protected void renderDecorations(GuiGraphics $$0, int $$1, int $$2) {
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        this.hovered = this.isMouseOver($$1, $$2) ? this.getEntryAtPosition($$1, $$2) : null;
        this.renderListBackground($$0);
        this.enableScissor($$0);
        if (this.renderHeader) {
            int $$4 = this.getRowLeft();
            int $$5 = this.getY() + 4 - (int)this.scrollAmount();
            this.renderHeader($$0, $$4, $$5);
        }
        this.renderListItems($$0, $$1, $$2, $$3);
        $$0.disableScissor();
        this.renderListSeparators($$0);
        this.renderScrollbar($$0);
        this.renderDecorations($$0, $$1, $$2);
    }

    protected void renderListSeparators(GuiGraphics $$0) {
        ResourceLocation $$1 = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
        ResourceLocation $$2 = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
        $$0.blit(RenderPipelines.GUI_TEXTURED, $$1, this.getX(), this.getY() - 2, 0.0f, 0.0f, this.getWidth(), 2, 32, 2);
        $$0.blit(RenderPipelines.GUI_TEXTURED, $$2, this.getX(), this.getBottom(), 0.0f, 0.0f, this.getWidth(), 2, 32, 2);
    }

    protected void renderListBackground(GuiGraphics $$0) {
        ResourceLocation $$1 = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
        $$0.blit(RenderPipelines.GUI_TEXTURED, $$1, this.getX(), this.getY(), this.getRight(), this.getBottom() + (int)this.scrollAmount(), this.getWidth(), this.getHeight(), 32, 32);
    }

    protected void enableScissor(GuiGraphics $$0) {
        $$0.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
    }

    protected void centerScrollOn(E $$0) {
        this.setScrollAmount(this.children().indexOf($$0) * this.itemHeight + this.itemHeight / 2 - this.height / 2);
    }

    protected void ensureVisible(E $$0) {
        int $$3;
        int $$1 = this.getRowTop(this.children().indexOf($$0));
        int $$2 = $$1 - this.getY() - 4 - this.itemHeight;
        if ($$2 < 0) {
            this.scroll($$2);
        }
        if (($$3 = this.getBottom() - $$1 - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-$$3);
        }
    }

    private void scroll(int $$0) {
        this.setScrollAmount(this.scrollAmount() + (double)$$0);
    }

    @Override
    protected double scrollRate() {
        return (double)this.itemHeight / 2.0;
    }

    @Override
    protected int scrollBarX() {
        return this.getRowRight() + 6 + 2;
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double $$0, double $$1) {
        return Optional.ofNullable(this.getEntryAtPosition($$0, $$1));
    }

    @Override
    public void setFocused(@Nullable GuiEventListener $$0) {
        GuiEventListener $$1 = this.getFocused();
        if ($$1 != $$0 && $$1 instanceof ContainerEventHandler) {
            ContainerEventHandler $$2 = (ContainerEventHandler)$$1;
            $$2.setFocused(null);
        }
        super.setFocused($$0);
        int $$3 = this.children.indexOf($$0);
        if ($$3 >= 0) {
            Entry $$4 = (Entry)this.children.get($$3);
            this.setSelected($$4);
            if (this.minecraft.getLastInputType().isKeyboard()) {
                this.ensureVisible($$4);
            }
        }
    }

    @Nullable
    protected E nextEntry(ScreenDirection $$02) {
        return (E)this.nextEntry($$02, $$0 -> true);
    }

    @Nullable
    protected E nextEntry(ScreenDirection $$0, Predicate<E> $$1) {
        return this.nextEntry($$0, $$1, this.getSelected());
    }

    @Nullable
    protected E nextEntry(ScreenDirection $$0, Predicate<E> $$1, @Nullable E $$2) {
        int $$3;
        switch ($$0) {
            default: {
                throw new MatchException(null, null);
            }
            case RIGHT: 
            case LEFT: {
                int n = 0;
                break;
            }
            case UP: {
                int n = -1;
                break;
            }
            case DOWN: {
                int n = $$3 = 1;
            }
        }
        if (!this.children().isEmpty() && $$3 != 0) {
            if ($$2 == null) {
                int $$4 = $$3 > 0 ? 0 : this.children().size() - 1;
            } else {
                int $$5 = this.children().indexOf($$2) + $$3;
            }
            for (void $$6 = $$5; $$6 >= 0 && $$6 < this.children.size(); $$6 += $$3) {
                Entry $$7 = (Entry)this.children().get((int)$$6);
                if (!$$1.test($$7)) continue;
                return (E)$$7;
            }
        }
        return null;
    }

    protected void renderListItems(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        int $$4 = this.getRowLeft();
        int $$5 = this.getRowWidth();
        int $$6 = this.itemHeight - 4;
        int $$7 = this.getItemCount();
        for (int $$8 = 0; $$8 < $$7; ++$$8) {
            int $$9 = this.getRowTop($$8);
            int $$10 = this.getRowBottom($$8);
            if ($$10 < this.getY() || $$9 > this.getBottom()) continue;
            this.renderItem($$0, $$1, $$2, $$3, $$8, $$4, $$9, $$5, $$6);
        }
    }

    protected void renderItem(GuiGraphics $$0, int $$1, int $$2, float $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        E $$9 = this.getEntry($$4);
        ((Entry)$$9).renderBack($$0, $$4, $$6, $$5, $$7, $$8, $$1, $$2, Objects.equals(this.hovered, $$9), $$3);
        if (this.isSelectedItem($$4)) {
            int $$10 = this.isFocused() ? -1 : -8355712;
            this.renderSelection($$0, $$6, $$7, $$8, $$10, -16777216);
        }
        ((Entry)$$9).render($$0, $$4, $$6, $$5, $$7, $$8, $$1, $$2, Objects.equals(this.hovered, $$9), $$3);
    }

    protected void renderSelection(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        int $$6 = this.getX() + (this.width - $$2) / 2;
        int $$7 = this.getX() + (this.width + $$2) / 2;
        $$0.fill($$6, $$1 - 2, $$7, $$1 + $$3 + 2, $$4);
        $$0.fill($$6 + 1, $$1 - 1, $$7 - 1, $$1 + $$3 + 1, $$5);
    }

    public int getRowLeft() {
        return this.getX() + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    public int getRowTop(int $$0) {
        return this.getY() + 4 - (int)this.scrollAmount() + $$0 * this.itemHeight + this.headerHeight;
    }

    public int getRowBottom(int $$0) {
        return this.getRowTop($$0) + this.itemHeight;
    }

    public int getRowWidth() {
        return 220;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        if (this.hovered != null) {
            return NarratableEntry.NarrationPriority.HOVERED;
        }
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Nullable
    protected E remove(int $$0) {
        Entry $$1 = (Entry)this.children.get($$0);
        if (this.removeEntry((Entry)this.children.get($$0))) {
            return (E)$$1;
        }
        return null;
    }

    protected boolean removeEntry(E $$0) {
        boolean $$1 = this.children.remove($$0);
        if ($$1 && $$0 == this.getSelected()) {
            this.setSelected(null);
        }
        return $$1;
    }

    @Nullable
    protected E getHovered() {
        return this.hovered;
    }

    void bindEntryToSelf(Entry<E> $$0) {
        $$0.list = this;
    }

    protected void narrateListElementPosition(NarrationElementOutput $$0, E $$1) {
        int $$3;
        List<E> $$2 = this.children();
        if ($$2.size() > 1 && ($$3 = $$2.indexOf($$1)) != -1) {
            $$0.add(NarratedElementType.POSITION, Component.a("narrator.position.list", $$3 + 1, $$2.size()));
        }
    }

    @Override
    @Nullable
    public /* synthetic */ GuiEventListener getFocused() {
        return this.getFocused();
    }

    class TrackedList
    extends AbstractList<E> {
        private final List<E> delegate = Lists.newArrayList();

        TrackedList() {
        }

        @Override
        public E get(int $$0) {
            return (Entry)this.delegate.get($$0);
        }

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public E set(int $$0, E $$1) {
            Entry $$2 = (Entry)this.delegate.set($$0, $$1);
            AbstractSelectionList.this.bindEntryToSelf($$1);
            return $$2;
        }

        @Override
        public void add(int $$0, E $$1) {
            this.delegate.add($$0, $$1);
            AbstractSelectionList.this.bindEntryToSelf($$1);
        }

        @Override
        public E remove(int $$0) {
            return (Entry)this.delegate.remove($$0);
        }

        @Override
        public /* synthetic */ Object remove(int n) {
            return this.remove(n);
        }

        @Override
        public /* synthetic */ void add(int n, Object object) {
            this.add(n, (E)((Entry)object));
        }

        @Override
        public /* synthetic */ Object set(int n, Object object) {
            return this.set(n, (E)((Entry)object));
        }

        @Override
        public /* synthetic */ Object get(int n) {
            return this.get(n);
        }
    }

    protected static abstract class Entry<E extends Entry<E>>
    implements GuiEventListener {
        @Deprecated
        AbstractSelectionList<E> list;

        protected Entry() {
        }

        @Override
        public void setFocused(boolean $$0) {
        }

        @Override
        public boolean isFocused() {
            return this.list.getFocused() == this;
        }

        public abstract void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        public void renderBack(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
        }

        @Override
        public boolean isMouseOver(double $$0, double $$1) {
            return Objects.equals(this.list.getEntryAtPosition($$0, $$1), this);
        }
    }
}

