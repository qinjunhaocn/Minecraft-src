/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingRenderState
extends EntityRenderState {
    public Direction direction = Direction.NORTH;
    @Nullable
    public PaintingVariant variant;
    public int[] lightCoords = new int[0];
}

