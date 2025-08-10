/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;

public abstract class AbstractContainerWidget
extends AbstractScrollArea
implements ContainerEventHandler {
    @Nullable
    private GuiEventListener focused;
    private boolean isDragging;

    public AbstractContainerWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public final boolean isDragging() {
        return this.isDragging;
    }

    @Override
    public final void setDragging(boolean $$0) {
        this.isDragging = $$0;
    }

    @Override
    @Nullable
    public GuiEventListener getFocused() {
        return this.focused;
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
    public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        return ContainerEventHandler.super.nextFocusPath($$0);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        boolean $$3 = this.updateScrolling($$0, $$1, $$2);
        return ContainerEventHandler.super.mouseClicked($$0, $$1, $$2) || $$3;
    }

    @Override
    public boolean mouseReleased(double $$0, double $$1, int $$2) {
        super.mouseReleased($$0, $$1, $$2);
        return ContainerEventHandler.super.mouseReleased($$0, $$1, $$2);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        super.mouseDragged($$0, $$1, $$2, $$3, $$4);
        return ContainerEventHandler.super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public void setFocused(boolean $$0) {
        ContainerEventHandler.super.setFocused($$0);
    }
}

