/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3x2f
 */
package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.ScreenArea;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;

public final class GuiTextRenderState
implements ScreenArea {
    public final Font font;
    public final FormattedCharSequence text;
    public final Matrix3x2f pose;
    public final int x;
    public final int y;
    public final int color;
    public final int backgroundColor;
    public final boolean dropShadow;
    @Nullable
    public final ScreenRectangle scissor;
    @Nullable
    private Font.PreparedText preparedText;
    @Nullable
    private ScreenRectangle bounds;

    public GuiTextRenderState(Font $$0, FormattedCharSequence $$1, Matrix3x2f $$2, int $$3, int $$4, int $$5, int $$6, boolean $$7, @Nullable ScreenRectangle $$8) {
        this.font = $$0;
        this.text = $$1;
        this.pose = $$2;
        this.x = $$3;
        this.y = $$4;
        this.color = $$5;
        this.backgroundColor = $$6;
        this.dropShadow = $$7;
        this.scissor = $$8;
    }

    public Font.PreparedText ensurePrepared() {
        if (this.preparedText == null) {
            this.preparedText = this.font.prepareText(this.text, (float)this.x, (float)this.y, this.color, this.dropShadow, this.backgroundColor);
            ScreenRectangle $$0 = this.preparedText.bounds();
            if ($$0 != null) {
                $$0 = $$0.transformMaxBounds(this.pose);
                this.bounds = this.scissor != null ? this.scissor.intersection($$0) : $$0;
            }
        }
        return this.preparedText;
    }

    @Override
    @Nullable
    public ScreenRectangle bounds() {
        this.ensurePrepared();
        return this.bounds;
    }
}

