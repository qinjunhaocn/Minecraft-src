/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.components;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.OptionalInt;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.SingleKeyCache;

public class MultiLineTextWidget
extends AbstractStringWidget {
    private OptionalInt maxWidth = OptionalInt.empty();
    private OptionalInt maxRows = OptionalInt.empty();
    private final SingleKeyCache<CacheKey, MultiLineLabel> cache = Util.singleKeyCache($$1 -> {
        if ($$1.maxRows.isPresent()) {
            return MultiLineLabel.a($$3, $$1.maxWidth, $$1.maxRows.getAsInt(), $$1.message);
        }
        return MultiLineLabel.create($$3, $$1.message, $$1.maxWidth);
    });
    private boolean centered = false;
    private boolean allowHoverComponents = false;
    @Nullable
    private Consumer<Style> componentClickHandler = null;

    public MultiLineTextWidget(Component $$0, Font $$1) {
        this(0, 0, $$0, $$1);
    }

    public MultiLineTextWidget(int $$0, int $$12, Component $$2, Font $$3) {
        super($$0, $$12, 0, 0, $$2, $$3);
        this.active = false;
    }

    @Override
    public MultiLineTextWidget setColor(int $$0) {
        super.setColor($$0);
        return this;
    }

    public MultiLineTextWidget setMaxWidth(int $$0) {
        this.maxWidth = OptionalInt.of($$0);
        return this;
    }

    public MultiLineTextWidget setMaxRows(int $$0) {
        this.maxRows = OptionalInt.of($$0);
        return this;
    }

    public MultiLineTextWidget setCentered(boolean $$0) {
        this.centered = $$0;
        return this;
    }

    public MultiLineTextWidget configureStyleHandling(boolean $$0, @Nullable Consumer<Style> $$1) {
        this.allowHoverComponents = $$0;
        this.componentClickHandler = $$1;
        return this;
    }

    @Override
    public int getWidth() {
        return this.cache.getValue(this.getFreshCacheKey()).getWidth();
    }

    @Override
    public int getHeight() {
        return this.cache.getValue(this.getFreshCacheKey()).getLineCount() * this.getFont().lineHeight;
    }

    @Override
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        MultiLineLabel $$4 = this.cache.getValue(this.getFreshCacheKey());
        int $$5 = this.getX();
        int $$6 = this.getY();
        int $$7 = this.getFont().lineHeight;
        int $$8 = this.getColor();
        if (this.centered) {
            $$4.renderCentered($$0, $$5 + this.getWidth() / 2, $$6, $$7, $$8);
        } else {
            $$4.renderLeftAligned($$0, $$5, $$6, $$7, $$8);
        }
        if (this.allowHoverComponents) {
            Style $$9 = this.getComponentStyleAt($$1, $$2);
            if (this.isHovered()) {
                $$0.renderComponentHoverEffect(this.getFont(), $$9, $$1, $$2);
            }
        }
    }

    @Nullable
    private Style getComponentStyleAt(double $$0, double $$1) {
        MultiLineLabel $$2 = this.cache.getValue(this.getFreshCacheKey());
        int $$3 = this.getX();
        int $$4 = this.getY();
        int $$5 = this.getFont().lineHeight;
        if (this.centered) {
            return $$2.getStyleAtCentered($$3 + this.getWidth() / 2, $$4, $$5, $$0, $$1);
        }
        return $$2.getStyleAtLeftAligned($$3, $$4, $$5, $$0, $$1);
    }

    @Override
    public void onClick(double $$0, double $$1) {
        Style $$2;
        if (this.componentClickHandler != null && ($$2 = this.getComponentStyleAt($$0, $$1)) != null) {
            this.componentClickHandler.accept($$2);
            return;
        }
        super.onClick($$0, $$1);
    }

    private CacheKey getFreshCacheKey() {
        return new CacheKey(this.getMessage(), this.maxWidth.orElse(Integer.MAX_VALUE), this.maxRows);
    }

    @Override
    public /* synthetic */ AbstractStringWidget setColor(int n) {
        return this.setColor(n);
    }

    static final class CacheKey
    extends Record {
        final Component message;
        final int maxWidth;
        final OptionalInt maxRows;

        CacheKey(Component $$0, int $$1, OptionalInt $$2) {
            this.message = $$0;
            this.maxWidth = $$1;
            this.maxRows = $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this, $$0);
        }

        public Component message() {
            return this.message;
        }

        public int maxWidth() {
            return this.maxWidth;
        }

        public OptionalInt maxRows() {
            return this.maxRows;
        }
    }
}

