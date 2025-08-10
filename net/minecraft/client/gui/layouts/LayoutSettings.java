/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.layouts;

public interface LayoutSettings {
    public LayoutSettings padding(int var1);

    public LayoutSettings padding(int var1, int var2);

    public LayoutSettings padding(int var1, int var2, int var3, int var4);

    public LayoutSettings paddingLeft(int var1);

    public LayoutSettings paddingTop(int var1);

    public LayoutSettings paddingRight(int var1);

    public LayoutSettings paddingBottom(int var1);

    public LayoutSettings paddingHorizontal(int var1);

    public LayoutSettings paddingVertical(int var1);

    public LayoutSettings align(float var1, float var2);

    public LayoutSettings alignHorizontally(float var1);

    public LayoutSettings alignVertically(float var1);

    default public LayoutSettings alignHorizontallyLeft() {
        return this.alignHorizontally(0.0f);
    }

    default public LayoutSettings alignHorizontallyCenter() {
        return this.alignHorizontally(0.5f);
    }

    default public LayoutSettings alignHorizontallyRight() {
        return this.alignHorizontally(1.0f);
    }

    default public LayoutSettings alignVerticallyTop() {
        return this.alignVertically(0.0f);
    }

    default public LayoutSettings alignVerticallyMiddle() {
        return this.alignVertically(0.5f);
    }

    default public LayoutSettings alignVerticallyBottom() {
        return this.alignVertically(1.0f);
    }

    public LayoutSettings copy();

    public LayoutSettingsImpl getExposed();

    public static LayoutSettings defaults() {
        return new LayoutSettingsImpl();
    }

    public static class LayoutSettingsImpl
    implements LayoutSettings {
        public int paddingLeft;
        public int paddingTop;
        public int paddingRight;
        public int paddingBottom;
        public float xAlignment;
        public float yAlignment;

        public LayoutSettingsImpl() {
        }

        public LayoutSettingsImpl(LayoutSettingsImpl $$0) {
            this.paddingLeft = $$0.paddingLeft;
            this.paddingTop = $$0.paddingTop;
            this.paddingRight = $$0.paddingRight;
            this.paddingBottom = $$0.paddingBottom;
            this.xAlignment = $$0.xAlignment;
            this.yAlignment = $$0.yAlignment;
        }

        @Override
        public LayoutSettingsImpl padding(int $$0) {
            return this.padding($$0, $$0);
        }

        @Override
        public LayoutSettingsImpl padding(int $$0, int $$1) {
            return this.paddingHorizontal($$0).paddingVertical($$1);
        }

        @Override
        public LayoutSettingsImpl padding(int $$0, int $$1, int $$2, int $$3) {
            return this.paddingLeft($$0).paddingRight($$2).paddingTop($$1).paddingBottom($$3);
        }

        @Override
        public LayoutSettingsImpl paddingLeft(int $$0) {
            this.paddingLeft = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl paddingTop(int $$0) {
            this.paddingTop = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl paddingRight(int $$0) {
            this.paddingRight = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl paddingBottom(int $$0) {
            this.paddingBottom = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl paddingHorizontal(int $$0) {
            return this.paddingLeft($$0).paddingRight($$0);
        }

        @Override
        public LayoutSettingsImpl paddingVertical(int $$0) {
            return this.paddingTop($$0).paddingBottom($$0);
        }

        @Override
        public LayoutSettingsImpl align(float $$0, float $$1) {
            this.xAlignment = $$0;
            this.yAlignment = $$1;
            return this;
        }

        @Override
        public LayoutSettingsImpl alignHorizontally(float $$0) {
            this.xAlignment = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl alignVertically(float $$0) {
            this.yAlignment = $$0;
            return this;
        }

        @Override
        public LayoutSettingsImpl copy() {
            return new LayoutSettingsImpl(this);
        }

        @Override
        public LayoutSettingsImpl getExposed() {
            return this;
        }

        @Override
        public /* synthetic */ LayoutSettings copy() {
            return this.copy();
        }

        @Override
        public /* synthetic */ LayoutSettings alignVertically(float f) {
            return this.alignVertically(f);
        }

        @Override
        public /* synthetic */ LayoutSettings alignHorizontally(float f) {
            return this.alignHorizontally(f);
        }

        @Override
        public /* synthetic */ LayoutSettings align(float f, float f2) {
            return this.align(f, f2);
        }

        @Override
        public /* synthetic */ LayoutSettings paddingVertical(int n) {
            return this.paddingVertical(n);
        }

        @Override
        public /* synthetic */ LayoutSettings paddingHorizontal(int n) {
            return this.paddingHorizontal(n);
        }

        @Override
        public /* synthetic */ LayoutSettings paddingBottom(int n) {
            return this.paddingBottom(n);
        }

        @Override
        public /* synthetic */ LayoutSettings paddingRight(int n) {
            return this.paddingRight(n);
        }

        @Override
        public /* synthetic */ LayoutSettings paddingTop(int n) {
            return this.paddingTop(n);
        }

        @Override
        public /* synthetic */ LayoutSettings paddingLeft(int n) {
            return this.paddingLeft(n);
        }

        @Override
        public /* synthetic */ LayoutSettings padding(int n, int n2, int n3, int n4) {
            return this.padding(n, n2, n3, n4);
        }

        @Override
        public /* synthetic */ LayoutSettings padding(int n, int n2) {
            return this.padding(n, n2);
        }

        @Override
        public /* synthetic */ LayoutSettings padding(int n) {
            return this.padding(n);
        }
    }
}

