/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3x2f
 */
package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.ScreenArea;
import org.joml.Matrix3x2f;

public interface PictureInPictureRenderState
extends ScreenArea {
    public static final Matrix3x2f IDENTITY_POSE = new Matrix3x2f();

    public int x0();

    public int x1();

    public int y0();

    public int y1();

    public float scale();

    default public Matrix3x2f pose() {
        return IDENTITY_POSE;
    }

    @Nullable
    public ScreenRectangle scissorArea();

    @Nullable
    public static ScreenRectangle getBounds(int $$0, int $$1, int $$2, int $$3, @Nullable ScreenRectangle $$4) {
        ScreenRectangle $$5 = new ScreenRectangle($$0, $$1, $$2 - $$0, $$3 - $$1);
        return $$4 != null ? $$4.intersection($$5) : $$5;
    }
}

