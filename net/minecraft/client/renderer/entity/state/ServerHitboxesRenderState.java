/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;

public record ServerHitboxesRenderState(boolean missing, double serverEntityX, double serverEntityY, double serverEntityZ, double deltaMovementX, double deltaMovementY, double deltaMovementZ, float eyeHeight, @Nullable HitboxesRenderState hitboxes) {
    public ServerHitboxesRenderState(boolean $$0) {
        this($$0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0f, null);
    }

    @Nullable
    public HitboxesRenderState hitboxes() {
        return this.hitboxes;
    }
}

