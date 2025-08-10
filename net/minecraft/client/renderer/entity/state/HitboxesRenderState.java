/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;

public record HitboxesRenderState(double viewX, double viewY, double viewZ, ImmutableList<HitboxRenderState> hitboxes) {
}

