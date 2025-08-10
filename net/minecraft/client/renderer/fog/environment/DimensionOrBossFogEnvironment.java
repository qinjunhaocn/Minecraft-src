/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AirBasedFogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

public class DimensionOrBossFogEnvironment
extends AirBasedFogEnvironment {
    @Override
    public void setupFog(FogData $$0, Entity $$1, BlockPos $$2, ClientLevel $$3, float $$4, DeltaTracker $$5) {
        $$0.environmentalStart = $$4 * 0.05f;
        $$0.skyEnd = $$0.environmentalEnd = Math.min($$4, 192.0f) * 0.5f;
        $$0.cloudEnd = $$0.environmentalEnd;
    }

    @Override
    public boolean isApplicable(@Nullable FogType $$0, Entity $$1) {
        return $$0 == FogType.DIMENSION_OR_BOSS;
    }
}

