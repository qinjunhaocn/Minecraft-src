/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.animal.axolotl.Axolotl;

public class AxolotlRenderState
extends LivingEntityRenderState {
    public Axolotl.Variant variant = Axolotl.Variant.DEFAULT;
    public float playingDeadFactor;
    public float movingFactor;
    public float inWaterFactor = 1.0f;
    public float onGroundFactor;
}

