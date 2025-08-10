/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  org.joml.Vector2i
 */
package net.minecraft.client.gui.components.events;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Vector2i;

public interface ContainerEventHandler
extends GuiEventListener {
    public List<? extends GuiEventListener> children();

    default public Optional<GuiEventListener> getChildAt(double $$0, double $$1) {
        for (GuiEventListener guiEventListener : this.children()) {
            if (!guiEventListener.isMouseOver($$0, $$1)) continue;
            return Optional.of(guiEventListener);
        }
        return Optional.empty();
    }

    @Override
    default public boolean mouseClicked(double $$0, double $$1, int $$2) {
        Optional<GuiEventListener> $$3 = this.getChildAt($$0, $$1);
        if ($$3.isEmpty()) {
            return false;
        }
        GuiEventListener $$4 = $$3.get();
        if ($$4.mouseClicked($$0, $$1, $$2)) {
            this.setFocused($$4);
            if ($$2 == 0) {
                this.setDragging(true);
            }
        }
        return true;
    }

    @Override
    default public boolean mouseReleased(double $$0, double $$1, int $$2) {
        if ($$2 == 0 && this.isDragging()) {
            this.setDragging(false);
            if (this.getFocused() != null) {
                return this.getFocused().mouseReleased($$0, $$1, $$2);
            }
        }
        return false;
    }

    @Override
    default public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (this.getFocused() != null && this.isDragging() && $$2 == 0) {
            return this.getFocused().mouseDragged($$0, $$1, $$2, $$3, $$4);
        }
        return false;
    }

    public boolean isDragging();

    public void setDragging(boolean var1);

    @Override
    default public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        return this.getChildAt($$0, $$1).filter($$4 -> $$4.mouseScrolled($$0, $$1, $$2, $$3)).isPresent();
    }

    @Override
    default public boolean keyPressed(int $$0, int $$1, int $$2) {
        return this.getFocused() != null && this.getFocused().keyPressed($$0, $$1, $$2);
    }

    @Override
    default public boolean keyReleased(int $$0, int $$1, int $$2) {
        return this.getFocused() != null && this.getFocused().keyReleased($$0, $$1, $$2);
    }

    @Override
    default public boolean a(char $$0, int $$1) {
        return this.getFocused() != null && this.getFocused().a($$0, $$1);
    }

    @Nullable
    public GuiEventListener getFocused();

    public void setFocused(@Nullable GuiEventListener var1);

    @Override
    default public void setFocused(boolean $$0) {
    }

    @Override
    default public boolean isFocused() {
        return this.getFocused() != null;
    }

    @Override
    @Nullable
    default public ComponentPath getCurrentFocusPath() {
        GuiEventListener $$0 = this.getFocused();
        if ($$0 != null) {
            return ComponentPath.path(this, $$0.getCurrentFocusPath());
        }
        return null;
    }

    @Override
    @Nullable
    default public ComponentPath nextFocusPath(FocusNavigationEvent $$0) {
        ComponentPath $$2;
        GuiEventListener $$1 = this.getFocused();
        if ($$1 != null && ($$2 = $$1.nextFocusPath($$0)) != null) {
            return ComponentPath.path(this, $$2);
        }
        if ($$0 instanceof FocusNavigationEvent.TabNavigation) {
            FocusNavigationEvent.TabNavigation $$3 = (FocusNavigationEvent.TabNavigation)$$0;
            return this.handleTabNavigation($$3);
        }
        if ($$0 instanceof FocusNavigationEvent.ArrowNavigation) {
            FocusNavigationEvent.ArrowNavigation $$4 = (FocusNavigationEvent.ArrowNavigation)$$0;
            return this.handleArrowNavigation($$4);
        }
        return null;
    }

    @Nullable
    private ComponentPath handleTabNavigation(FocusNavigationEvent.TabNavigation $$02) {
        Supplier<GuiEventListener> $$10;
        BooleanSupplier $$9;
        int $$7;
        boolean $$1 = $$02.forward();
        GuiEventListener $$2 = this.getFocused();
        ArrayList<? extends GuiEventListener> $$3 = new ArrayList<GuiEventListener>(this.children());
        Collections.sort($$3, Comparator.comparingInt($$0 -> $$0.getTabOrderGroup()));
        int $$4 = $$3.indexOf($$2);
        if ($$2 != null && $$4 >= 0) {
            int $$5 = $$4 + ($$1 ? 1 : 0);
        } else if ($$1) {
            boolean $$6 = false;
        } else {
            $$7 = $$3.size();
        }
        ListIterator $$8 = $$3.listIterator($$7);
        BooleanSupplier booleanSupplier = $$1 ? $$8::hasNext : ($$9 = $$8::hasPrevious);
        Supplier<GuiEventListener> supplier = $$1 ? $$8::next : ($$10 = $$8::previous);
        while ($$9.getAsBoolean()) {
            GuiEventListener $$11 = $$10.get();
            ComponentPath $$12 = $$11.nextFocusPath($$02);
            if ($$12 == null) continue;
            return ComponentPath.path(this, $$12);
        }
        return null;
    }

    @Nullable
    private ComponentPath handleArrowNavigation(FocusNavigationEvent.ArrowNavigation $$0) {
        GuiEventListener $$1 = this.getFocused();
        if ($$1 == null) {
            ScreenDirection $$2 = $$0.direction();
            ScreenRectangle $$3 = this.getBorderForArrowNavigation($$2.getOpposite());
            return ComponentPath.path(this, this.nextFocusPathInDirection($$3, $$2, null, $$0));
        }
        ScreenRectangle $$4 = $$1.getRectangle();
        return ComponentPath.path(this, this.nextFocusPathInDirection($$4, $$0.direction(), $$1, $$0));
    }

    @Nullable
    private ComponentPath nextFocusPathInDirection(ScreenRectangle $$0, ScreenDirection $$12, @Nullable GuiEventListener $$2, FocusNavigationEvent $$3) {
        ScreenAxis $$4 = $$12.getAxis();
        ScreenAxis $$5 = $$4.orthogonal();
        ScreenDirection $$6 = $$5.getPositive();
        int $$7 = $$0.getBoundInDirection($$12.getOpposite());
        ArrayList<GuiEventListener> $$8 = new ArrayList<GuiEventListener>();
        for (GuiEventListener guiEventListener : this.children()) {
            ScreenRectangle $$10;
            if (guiEventListener == $$2 || !($$10 = guiEventListener.getRectangle()).overlapsInAxis($$0, $$5)) continue;
            int $$11 = $$10.getBoundInDirection($$12.getOpposite());
            if ($$12.isAfter($$11, $$7)) {
                $$8.add(guiEventListener);
                continue;
            }
            if ($$11 != $$7 || !$$12.isAfter($$10.getBoundInDirection($$12), $$0.getBoundInDirection($$12))) continue;
            $$8.add(guiEventListener);
        }
        Comparator<GuiEventListener> $$122 = Comparator.comparing($$1 -> $$1.getRectangle().getBoundInDirection($$12.getOpposite()), $$12.coordinateValueComparator());
        Comparator<GuiEventListener> comparator = Comparator.comparing($$1 -> $$1.getRectangle().getBoundInDirection($$6.getOpposite()), $$6.coordinateValueComparator());
        $$8.sort($$122.thenComparing(comparator));
        for (GuiEventListener $$14 : $$8) {
            ComponentPath $$15 = $$14.nextFocusPath($$3);
            if ($$15 == null) continue;
            return $$15;
        }
        return this.nextFocusPathVaguelyInDirection($$0, $$12, $$2, $$3);
    }

    @Nullable
    private ComponentPath nextFocusPathVaguelyInDirection(ScreenRectangle $$0, ScreenDirection $$1, @Nullable GuiEventListener $$2, FocusNavigationEvent $$3) {
        ScreenAxis $$4 = $$1.getAxis();
        ScreenAxis $$5 = $$4.orthogonal();
        ArrayList<Pair> $$6 = new ArrayList<Pair>();
        ScreenPosition $$7 = ScreenPosition.of($$4, $$0.getBoundInDirection($$1), $$0.getCenterInAxis($$5));
        for (GuiEventListener guiEventListener : this.children()) {
            ScreenRectangle $$9;
            ScreenPosition $$10;
            if (guiEventListener == $$2 || !$$1.isAfter(($$10 = ScreenPosition.of($$4, ($$9 = guiEventListener.getRectangle()).getBoundInDirection($$1.getOpposite()), $$9.getCenterInAxis($$5))).getCoordinate($$4), $$7.getCoordinate($$4))) continue;
            long $$11 = Vector2i.distanceSquared((int)$$7.x(), (int)$$7.y(), (int)$$10.x(), (int)$$10.y());
            $$6.add(Pair.of((Object)guiEventListener, (Object)$$11));
        }
        $$6.sort(Comparator.comparingDouble(Pair::getSecond));
        for (Pair pair : $$6) {
            ComponentPath $$13 = ((GuiEventListener)pair.getFirst()).nextFocusPath($$3);
            if ($$13 == null) continue;
            return $$13;
        }
        return null;
    }
}

