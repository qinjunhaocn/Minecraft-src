/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class ArmadilloRenderState
extends LivingEntityRenderState {
    public boolean isHidingInShell;
    public final AnimationState rollOutAnimationState = new AnimationState();
    public final AnimationState rollUpAnimationState = new AnimationState();
    public final AnimationState peekAnimationState = new AnimationState();
}

