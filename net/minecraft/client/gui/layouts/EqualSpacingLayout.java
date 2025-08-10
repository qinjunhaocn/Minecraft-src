/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;

public class EqualSpacingLayout
extends AbstractLayout {
    private final Orientation orientation;
    private final List<ChildContainer> children = new ArrayList<ChildContainer>();
    private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults();

    public EqualSpacingLayout(int $$0, int $$1, Orientation $$2) {
        this(0, 0, $$0, $$1, $$2);
    }

    public EqualSpacingLayout(int $$0, int $$1, int $$2, int $$3, Orientation $$4) {
        super($$0, $$1, $$2, $$3);
        this.orientation = $$4;
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();
        if (this.children.isEmpty()) {
            return;
        }
        int $$0 = 0;
        int $$1 = this.orientation.getSecondaryLength(this);
        for (ChildContainer $$2 : this.children) {
            $$0 += this.orientation.getPrimaryLength($$2);
            $$1 = Math.max($$1, this.orientation.getSecondaryLength($$2));
        }
        int $$3 = this.orientation.getPrimaryLength(this) - $$0;
        int $$4 = this.orientation.getPrimaryPosition(this);
        Iterator<ChildContainer> $$5 = this.children.iterator();
        ChildContainer $$6 = $$5.next();
        this.orientation.setPrimaryPosition($$6, $$4);
        $$4 += this.orientation.getPrimaryLength($$6);
        if (this.children.size() >= 2) {
            Divisor $$7 = new Divisor($$3, this.children.size() - 1);
            while ($$7.hasNext()) {
                ChildContainer $$8 = $$5.next();
                this.orientation.setPrimaryPosition($$8, $$4 += $$7.nextInt());
                $$4 += this.orientation.getPrimaryLength($$8);
            }
        }
        int $$9 = this.orientation.getSecondaryPosition(this);
        for (ChildContainer $$10 : this.children) {
            this.orientation.setSecondaryPosition($$10, $$9, $$1);
        }
        switch (this.orientation.ordinal()) {
            case 0: {
                this.height = $$1;
                break;
            }
            case 1: {
                this.width = $$1;
            }
        }
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> $$0) {
        this.children.forEach($$1 -> $$0.accept($$1.child));
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    public <T extends LayoutElement> T addChild(T $$0) {
        return this.addChild($$0, this.newChildLayoutSettings());
    }

    public <T extends LayoutElement> T addChild(T $$0, LayoutSettings $$1) {
        this.children.add(new ChildContainer($$0, $$1));
        return $$0;
    }

    public <T extends LayoutElement> T addChild(T $$0, Consumer<LayoutSettings> $$1) {
        return this.addChild($$0, Util.make(this.newChildLayoutSettings(), $$1));
    }

    public static final class Orientation
    extends Enum<Orientation> {
        public static final /* enum */ Orientation HORIZONTAL = new Orientation();
        public static final /* enum */ Orientation VERTICAL = new Orientation();
        private static final /* synthetic */ Orientation[] $VALUES;

        public static Orientation[] values() {
            return (Orientation[])$VALUES.clone();
        }

        public static Orientation valueOf(String $$0) {
            return Enum.valueOf(Orientation.class, $$0);
        }

        int getPrimaryLength(LayoutElement $$0) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> $$0.getWidth();
                case 1 -> $$0.getHeight();
            };
        }

        int getPrimaryLength(ChildContainer $$0) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> $$0.getWidth();
                case 1 -> $$0.getHeight();
            };
        }

        int getSecondaryLength(LayoutElement $$0) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> $$0.getHeight();
                case 1 -> $$0.getWidth();
            };
        }

        int getSecondaryLength(ChildContainer $$0) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> $$0.getHeight();
                case 1 -> $$0.getWidth();
            };
        }

        void setPrimaryPosition(ChildContainer $$0, int $$1) {
            switch (this.ordinal()) {
                case 0: {
                    $$0.setX($$1, $$0.getWidth());
                    break;
                }
                case 1: {
                    $$0.setY($$1, $$0.getHeight());
                }
            }
        }

        void setSecondaryPosition(ChildContainer $$0, int $$1, int $$2) {
            switch (this.ordinal()) {
                case 0: {
                    $$0.setY($$1, $$2);
                    break;
                }
                case 1: {
                    $$0.setX($$1, $$2);
                }
            }
        }

        int getPrimaryPosition(LayoutElement $$0) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> $$0.getX();
                case 1 -> $$0.getY();
            };
        }

        int getSecondaryPosition(LayoutElement $$0) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> $$0.getY();
                case 1 -> $$0.getX();
            };
        }

        private static /* synthetic */ Orientation[] a() {
            return new Orientation[]{HORIZONTAL, VERTICAL};
        }

        static {
            $VALUES = Orientation.a();
        }
    }

    static class ChildContainer
    extends AbstractLayout.AbstractChildWrapper {
        protected ChildContainer(LayoutElement $$0, LayoutSettings $$1) {
            super($$0, $$1);
        }
    }
}

