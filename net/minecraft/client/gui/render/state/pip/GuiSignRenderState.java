/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.world.level.block.state.properties.WoodType;

public record GuiSignRenderState(Model signModel, WoodType woodType, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState
{
    public GuiSignRenderState(Model $$0, WoodType $$1, int $$2, int $$3, int $$4, int $$5, float $$6, @Nullable ScreenRectangle $$7) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, PictureInPictureRenderState.getBounds($$2, $$3, $$4, $$5, $$7));
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

