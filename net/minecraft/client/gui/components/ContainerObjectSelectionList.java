/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class ContainerObjectSelectionList<E extends Entry<E>>
extends AbstractSelectionList<E> {
    public ContainerObjectSelectionList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    public ContainerObjectSelectionList(Minecraft $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent $$02) {
        if (this.getItemCount() == 0) {
            return null;
        }
        if ($$02 instanceof FocusNavigationEvent.ArrowNavigation) {
            ComponentPath $$6;
            FocusNavigationEvent.ArrowNavigation $$1 = (FocusNavigationEvent.ArrowNavigation)$$02;
            Entry $$2 = (Entry)this.getFocused();
            if ($$1.direction().getAxis() == ScreenAxis.HORIZONTAL && $$2 != null) {
                return ComponentPath.path(this, $$2.nextFocusPath($$02));
            }
            int $$3 = -1;
            ScreenDirection $$4 = $$1.direction();
            if ($$2 != null) {
                $$3 = $$2.children().indexOf($$2.getFocused());
            }
            if ($$3 == -1) {
                switch ($$4) {
                    case LEFT: {
                        $$3 = Integer.MAX_VALUE;
                        $$4 = ScreenDirection.DOWN;
                        break;
                    }
                    case RIGHT: {
                        $$3 = 0;
                        $$4 = ScreenDirection.DOWN;
                        break;
                    }
                    default: {
                        $$3 = 0;
                    }
                }
            }
            Entry $$5 = $$2;
            do {
                if (($$5 = this.nextEntry($$4, $$0 -> !$$0.children().isEmpty(), $$5)) != null) continue;
                return null;
            } while (($$6 = $$5.focusPathAtIndex($$1, $$3)) == null);
            return ComponentPath.path(this, $$6);
        }
        return super.nextFocusPath($$02);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener $$0) {
        if (this.getFocused() == $$0) {
            return;
        }
        super.setFocused($$0);
        if ($$0 == null) {
            this.setSelected(null);
        }
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarratableEntry.NarrationPriority.FOCUSED;
        }
        return super.narrationPriority();
    }

    @Override
    protected boolean isSelectedItem(int $$0) {
        return false;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        Entry $$1 = (Entry)this.getHovered();
        if ($$1 != null) {
            $$1.updateNarration($$0.nest());
            this.narrateListElementPosition($$0, $$1);
        } else {
            Entry $$2 = (Entry)this.getFocused();
            if ($$2 != null) {
                $$2.updateNarration($$0.nest());
                this.narrateListElementPosition($$0, $$2);
            }
        }
        $$0.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
    }

    public static abstract class Entry<E extends Entry<E>>
    extends AbstractSelectionList.Entry<E>
    implements ContainerEventHandler {
        @Nullable
        private GuiEventListener focused;
        @Nullable
        private NarratableEntry lastNarratable;
        private boolean dragging;

        @Override
        public boolean isDragging() {
            return this.dragging;
        }

        @Override
        public void setDragging(boolean $$0) {
            this.dragging = $$0;
        }

        @Override
        public boolean mouseClicked(double $$0, double $$1, int $$2) {
            return ContainerEventHandler.super.mouseClicked($$0, $$1, $$2);
        }

        @Override
        public void setFocused(@Nullable GuiEventListener $$0) {
            if (this.focused != null) {
                this.focused.setFocused(false);
            }
            if ($$0 != null) {
                $$0.setFocused(true);
            }
            this.focused = $$0;
        }

        @Override
        @Nullable
        public GuiEventListener getFocused() {
            return this.focused;
        }

        @Nullable
        public ComponentPath focusPathAtIndex(FocusNavigationEvent $$0, int $$1) {
            if (this.children().isEmpty()) {
                return null;
            }
            ComponentPath $$2 = this.children().get(Math.min($$1, this.children().size() - 1)).nextFocusPath($$0);
            return ComponentPath.path(this, $$2);
        }

        @Override
        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
            if ($$0 instanceof FocusNavigationEvent.ArrowNavigation) {
                int $$3;
                int $$2;
                FocusNavigationEvent.ArrowNavigation $$1 = (FocusNavigationEvent.ArrowNavigation)$$0;
                switch ($$1.direction()) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case UP: 
                    case DOWN: {
                        int n = 0;
                        break;
                    }
                    case LEFT: {
                        int n = -1;
                        break;
                    }
                    case RIGHT: {
                        int n = $$2 = 1;
                    }
                }
                if ($$2 == 0) {
                    return null;
                }
                for (int $$4 = $$3 = Mth.clamp($$2 + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1); $$4 >= 0 && $$4 < this.children().size(); $$4 += $$2) {
                    GuiEventListener $$5 = this.children().get($$4);
                    ComponentPath $$6 = $$5.nextFocusPath($$0);
                    if ($$6 == null) continue;
                    return ComponentPath.path(this, $$6);
                }
            }
            return ContainerEventHandler.super.nextFocusPath($$0);
        }

        public abstract List<? extends NarratableEntry> narratables();

        void updateNarration(NarrationElementOutput $$0) {
            List<NarratableEntry> $$1 = this.narratables();
            Screen.NarratableSearchResult $$2 = Screen.findNarratableWidget($$1, this.lastNarratable);
            if ($$2 != null) {
                if ($$2.priority.isTerminal()) {
                    this.lastNarratable = $$2.entry;
                }
                if ($$1.size() > 1) {
                    $$0.add(NarratedElementType.POSITION, Component.a("narrator.position.object_list", $$2.index + 1, $$1.size()));
                    if ($$2.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                        $$0.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
                    }
                }
                $$2.entry.updateNarration($$0.nest());
            }
        }
    }
}

