/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemStack;

public class CamelRenderState
extends LivingEntityRenderState {
    public ItemStack saddle = ItemStack.EMPTY;
    public boolean isRidden;
    public float jumpCooldown;
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sitPoseAnimationState = new AnimationState();
    public final AnimationState sitUpAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState dashAnimationState = new AnimationState();
}

