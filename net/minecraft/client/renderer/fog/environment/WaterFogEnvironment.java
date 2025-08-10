/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

public class WaterFogEnvironment
extends FogEnvironment {
    private static final int WATER_FOG_DISTANCE = 96;
    private static final float BIOME_FOG_TRANSITION_TIME = 5000.0f;
    private static int targetBiomeFog = -1;
    private static int previousBiomeFog = -1;
    private static long biomeChangedTime = -1L;

    @Override
    public void setupFog(FogData $$0, Entity $$1, BlockPos $$2, ClientLevel $$3, float $$4, DeltaTracker $$5) {
        $$0.environmentalStart = -8.0f;
        $$0.environmentalEnd = 96.0f;
        if ($$1 instanceof LocalPlayer) {
            LocalPlayer $$6 = (LocalPlayer)$$1;
            $$0.environmentalEnd *= Math.max(0.25f, $$6.getWaterVision());
            if ($$3.getBiome($$2).is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                $$0.environmentalEnd *= 0.85f;
            }
        }
        $$0.skyEnd = $$0.environmentalEnd;
        $$0.cloudEnd = $$0.environmentalEnd;
    }

    @Override
    public boolean isApplicable(@Nullable FogType $$0, Entity $$1) {
        return $$0 == FogType.WATER;
    }

    @Override
    public int getBaseColor(ClientLevel $$0, Camera $$1, int $$2, float $$3) {
        long $$4 = Util.getMillis();
        int $$5 = $$0.getBiome($$1.getBlockPosition()).value().getWaterFogColor();
        if (biomeChangedTime < 0L) {
            targetBiomeFog = $$5;
            previousBiomeFog = $$5;
            biomeChangedTime = $$4;
        }
        float $$6 = Mth.clamp((float)($$4 - biomeChangedTime) / 5000.0f, 0.0f, 1.0f);
        int $$7 = ARGB.lerp($$6, previousBiomeFog, targetBiomeFog);
        if (targetBiomeFog != $$5) {
            targetBiomeFog = $$5;
            previousBiomeFog = $$7;
            biomeChangedTime = $$4;
        }
        return $$7;
    }

    @Override
    public void onNotApplicable() {
        biomeChangedTime = -1L;
    }
}

