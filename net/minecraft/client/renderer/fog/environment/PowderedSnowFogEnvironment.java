/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

public class PowderedSnowFogEnvironment
extends FogEnvironment {
    private static final int COLOR = -6308916;

    @Override
    public int getBaseColor(ClientLevel $$0, Camera $$1, int $$2, float $$3) {
        return -6308916;
    }

    @Override
    public void setupFog(FogData $$0, Entity $$1, BlockPos $$2, ClientLevel $$3, float $$4, DeltaTracker $$5) {
        if ($$1.isSpectator()) {
            $$0.environmentalStart = -8.0f;
            $$0.environmentalEnd = $$4 * 0.5f;
        } else {
            $$0.environmentalStart = 0.0f;
            $$0.environmentalEnd = 2.0f;
        }
        $$0.skyEnd = $$0.environmentalEnd;
        $$0.cloudEnd = $$0.environmentalEnd;
    }

    @Override
    public boolean isApplicable(@Nullable FogType $$0, Entity $$1) {
        return $$0 == FogType.POWDER_SNOW;
    }
}

