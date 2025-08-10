/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Matrix3x2f
 */
package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.ScreenArea;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix3x2f;

public final class GuiItemRenderState
implements ScreenArea {
    private final String name;
    private final Matrix3x2f pose;
    private final TrackingItemStackRenderState itemStackRenderState;
    private final int x;
    private final int y;
    @Nullable
    private final ScreenRectangle scissorArea;
    @Nullable
    private final ScreenRectangle oversizedItemBounds;
    @Nullable
    private final ScreenRectangle bounds;

    public GuiItemRenderState(String $$0, Matrix3x2f $$1, TrackingItemStackRenderState $$2, int $$3, int $$4, @Nullable ScreenRectangle $$5) {
        this.name = $$0;
        this.pose = $$1;
        this.itemStackRenderState = $$2;
        this.x = $$3;
        this.y = $$4;
        this.scissorArea = $$5;
        this.oversizedItemBounds = this.itemStackRenderState().isOversizedInGui() ? this.calculateOversizedItemBounds() : null;
        this.bounds = this.calculateBounds(this.oversizedItemBounds != null ? this.oversizedItemBounds : new ScreenRectangle(this.x, this.y, 16, 16));
    }

    @Nullable
    private ScreenRectangle calculateOversizedItemBounds() {
        AABB $$0 = this.itemStackRenderState.getModelBoundingBox();
        int $$1 = Mth.ceil($$0.getXsize() * 16.0);
        int $$2 = Mth.ceil($$0.getYsize() * 16.0);
        if ($$1 > 16 || $$2 > 16) {
            float $$3 = (float)($$0.minX * 16.0);
            float $$4 = (float)($$0.maxY * 16.0);
            int $$5 = Mth.floor($$3);
            int $$6 = Mth.floor($$4);
            int $$7 = this.x + $$5 + 8;
            int $$8 = this.y - $$6 + 8;
            return new ScreenRectangle($$7, $$8, $$1, $$2);
        }
        return null;
    }

    @Nullable
    private ScreenRectangle calculateBounds(ScreenRectangle $$0) {
        ScreenRectangle $$1 = $$0.transformMaxBounds(this.pose);
        return this.scissorArea != null ? this.scissorArea.intersection($$1) : $$1;
    }

    public String name() {
        return this.name;
    }

    public Matrix3x2f pose() {
        return this.pose;
    }

    public TrackingItemStackRenderState itemStackRenderState() {
        return this.itemStackRenderState;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    @Nullable
    public ScreenRectangle scissorArea() {
        return this.scissorArea;
    }

    @Nullable
    public ScreenRectangle oversizedItemBounds() {
        return this.oversizedItemBounds;
    }

    @Override
    @Nullable
    public ScreenRectangle bounds() {
        return this.bounds;
    }
}

