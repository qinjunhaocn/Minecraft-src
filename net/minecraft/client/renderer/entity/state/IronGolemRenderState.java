/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Crackiness;

public class IronGolemRenderState
extends LivingEntityRenderState {
    public float attackTicksRemaining;
    public int offerFlowerTick;
    public Crackiness.Level crackiness = Crackiness.Level.NONE;
}

