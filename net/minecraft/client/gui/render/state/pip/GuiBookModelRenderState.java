/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.model.BookModel;
import net.minecraft.resources.ResourceLocation;

public record GuiBookModelRenderState(BookModel bookModel, ResourceLocation texture, float open, float flip, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState
{
    public GuiBookModelRenderState(BookModel $$0, ResourceLocation $$1, float $$2, float $$3, int $$4, int $$5, int $$6, int $$7, float $$8, @Nullable ScreenRectangle $$9) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, PictureInPictureRenderState.getBounds($$4, $$5, $$6, $$7, $$9));
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

