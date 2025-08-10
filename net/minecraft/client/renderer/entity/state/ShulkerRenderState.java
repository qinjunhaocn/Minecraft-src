/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;

public class ShulkerRenderState
extends LivingEntityRenderState {
    public Vec3 renderOffset = Vec3.ZERO;
    @Nullable
    public DyeColor color;
    public float peekAmount;
    public float yHeadRot;
    public float yBodyRot;
    public Direction attachFace = Direction.DOWN;
}

