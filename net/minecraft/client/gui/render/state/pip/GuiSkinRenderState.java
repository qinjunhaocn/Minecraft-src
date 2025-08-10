/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.resources.ResourceLocation;

public record GuiSkinRenderState(PlayerModel playerModel, ResourceLocation texture, float rotationX, float rotationY, float pivotY, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState
{
    public GuiSkinRenderState(PlayerModel $$0, ResourceLocation $$1, float $$2, float $$3, float $$4, int $$5, int $$6, int $$7, int $$8, float $$9, @Nullable ScreenRectangle $$10) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, PictureInPictureRenderState.getBounds($$5, $$6, $$7, $$8, $$10));
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

