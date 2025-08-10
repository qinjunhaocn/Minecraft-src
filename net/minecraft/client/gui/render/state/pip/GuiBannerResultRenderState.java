/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public record GuiBannerResultRenderState(ModelPart flag, DyeColor baseColor, BannerPatternLayers resultBannerPatterns, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState
{
    public GuiBannerResultRenderState(ModelPart $$0, DyeColor $$1, BannerPatternLayers $$2, int $$3, int $$4, int $$5, int $$6, @Nullable ScreenRectangle $$7) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, PictureInPictureRenderState.getBounds($$3, $$4, $$5, $$6, $$7));
    }

    @Override
    public float scale() {
        return 16.0f;
    }

    @Override
    @Nullable
    public ScreenRectangle scissorArea() {
        return this.scissorArea;
    }

    @Override
    @Nullable
    public ScreenRectangle bounds() {
        return this.bounds;
    }
}

