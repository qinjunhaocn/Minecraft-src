/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;

public class EndCrystalRenderState
extends EntityRenderState {
    public boolean showsBottom = true;
    @Nullable
    public Vec3 beamOffset;
}

