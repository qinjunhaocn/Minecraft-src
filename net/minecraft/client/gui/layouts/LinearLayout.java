/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;

public class LinearLayout
implements Layout {
    private final GridLayout wrapped;
    private final Orientation orientation;
    private int nextChildIndex = 0;

    private LinearLayout(Orientation $$0) {
        this(0, 0, $$0);
    }

    public LinearLayout(int $$0, int $$1, Orientation $$2) {
        this.wrapped = new GridLayout($$0, $$1);
        this.orientation = $$2;
    }

    public LinearLayout spacing(int $$0) {
        this.orientation.setSpacing(this.wrapped, $$0);
        return this;
    }

    public LayoutSettings newCellSettings() {
        return this.wrapped.newCellSettings();
    }

    public LayoutSettings defaultCellSetting() {
        return this.wrapped.defaultCellSetting();
    }

    public <T extends LayoutElement> T addChild(T $$0, LayoutSettings $$1) {
        return this.orientation.addChild(this.wrapped, $$0, this.nextChildIndex++, $$1);
    }

    public <T extends LayoutElement> T addChild(T $$0) {
        return this.addChild($$0, this.newCellSettings());
    }

    public <T extends LayoutElement> T addChild(T $$0, Consumer<LayoutSettings> $$1) {
        return this.orientation.addChild(this.wrapped, $$0, this.nextChildIndex++, Util.make(this.newCellSettings(), $$1));
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> $$0) {
        this.wrapped.visitChildren($$0);
    }

    @Override
    public void arrangeElements() {
        this.wrapped.arrangeElements();
    }

    @Override
    public int getWidth() {
        return this.wrapped.getWidth();
    }

    @Override
    public int getHeight() {
        return this.wrapped.getHeight();
    }

    @Override
    public void setX(int $$0) {
        this.wrapped.setX($$0);
    }

    @Override
    public void setY(int $$0) {
        this.wrapped.setY($$0);
    }

    @Override
    public int getX() {
        return this.wrapped.getX();
    }

    @Override
    public int getY() {
        return this.wrapped.getY();
    }

    public static LinearLayout vertical() {
        return new LinearLayout(Orientation.VERTICAL);
    }

    public static LinearLayout horizontal() {
        return new LinearLayout(Orientation.HORIZONTAL);
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

        void setSpacing(GridLayout $$0, int $$1) {
            switch (this.ordinal()) {
                case 0: {
                    $$0.columnSpacing($$1);
                    break;
                }
                case 1: {
                    $$0.rowSpacing($$1);
                }
            }
        }

        public <T extends LayoutElement> T addChild(GridLayout $$0, T $$1, int $$2, LayoutSettings $$3) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> $$0.addChild($$1, 0, $$2, $$3);
                case 1 -> $$0.addChild($$1, $$2, 0, $$3);
            };
        }

        private static /* synthetic */ Orientation[] a() {
            return new Orientation[]{HORIZONTAL, VERTICAL};
        }

        static {
            $VALUES = Orientation.a();
        }
    }
}

