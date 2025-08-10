/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AirBasedFogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;

public class AtmosphericFogEnvironment
extends AirBasedFogEnvironment {
    private static final int MIN_RAIN_FOG_SKY_LIGHT = 8;
    private static final float RAIN_FOG_START_OFFSET = -160.0f;
    private static final float RAIN_FOG_END_OFFSET = -256.0f;
    private float rainFogMultiplier;

    @Override
    public void setupFog(FogData $$0, Entity $$1, BlockPos $$2, ClientLevel $$3, float $$4, DeltaTracker $$5) {
        Biome $$6 = $$3.getBiome($$2).value();
        float $$7 = $$5.getGameTimeDeltaTicks();
        boolean $$8 = $$6.hasPrecipitation();
        float $$9 = Mth.clamp(((float)$$3.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue($$2) - 8.0f) / 7.0f, 0.0f, 1.0f);
        float $$10 = $$3.getRainLevel($$5.getGameTimeDeltaPartialTick(false)) * $$9 * ($$8 ? 1.0f : 0.5f);
        this.rainFogMultiplier += ($$10 - this.rainFogMultiplier) * $$7 * 0.2f;
        $$0.environmentalStart = this.rainFogMultiplier * -160.0f;
        $$0.environmentalEnd = 1024.0f + -256.0f * this.rainFogMultiplier;
        $$0.skyEnd = $$4;
        $$0.cloudEnd = Minecraft.getInstance().options.cloudRange().get() * 16;
    }

    @Override
    public boolean isApplicable(@Nullable FogType $$0, Entity $$1) {
        return $$0 == FogType.ATMOSPHERIC;
    }
}

