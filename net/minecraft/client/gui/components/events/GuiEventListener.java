/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface GuiEventListener
extends TabOrderedElement {
    public static final long DOUBLE_CLICK_THRESHOLD_MS = 250L;

    default public void mouseMoved(double $$0, double $$1) {
    }

    default public boolean mouseClicked(double $$0, double $$1, int $$2) {
        return false;
    }

    default public boolean mouseReleased(double $$0, double $$1, int $$2) {
        return false;
    }

    default public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        return false;
    }

    default public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        return false;
    }

    default public boolean keyPressed(int $$0, int $$1, int $$2) {
        return false;
    }

    default public boolean keyReleased(int $$0, int $$1, int $$2) {
        return false;
    }

    default public boolean a(char $$0, int $$1) {
        return false;
    }

    @Nullable
    default public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        return null;
    }

    default public boolean isMouseOver(double $$0, double $$1) {
        return false;
    }

    public void setFocused(boolean var1);

    public boolean isFocused();

    @Nullable
    default public ComponentPath getCurrentFocusPath() {
        if (this.isFocused()) {
            return ComponentPath.leaf(this);
        }
        return null;
    }

    default public ScreenRectangle getRectangle() {
        return ScreenRectangle.empty();
    }

    default public ScreenRectangle getBorderForArrowNavigation(ScreenDirection $$0) {
        return this.getRectangle().getBorder($$0);
    }
}

