/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.util.ARGB;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class AirBasedFogEnvironment
extends FogEnvironment {
    @Override
    public int getBaseColor(ClientLevel $$0, Camera $$12, int $$22, float $$32) {
        float $$23;
        float $$4 = Mth.clamp(Mth.cos($$0.getTimeOfDay($$32) * ((float)Math.PI * 2)) * 2.0f + 0.5f, 0.0f, 1.0f);
        BiomeManager $$5 = $$0.getBiomeManager();
        Vec3 $$6 = $$12.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
        Vec3 $$7 = $$0.effects().getBrightnessDependentFogColor(CubicSampler.gaussianSampleVec3($$6, ($$1, $$2, $$3) -> Vec3.fromRGB24($$5.getNoiseBiomeAtQuart($$1, $$2, $$3).value().getFogColor())), $$4);
        float $$8 = (float)$$7.x();
        float $$9 = (float)$$7.y();
        float $$10 = (float)$$7.z();
        if ($$22 >= 4) {
            float $$11 = Mth.sin($$0.getSunAngle($$32)) > 0.0f ? -1.0f : 1.0f;
            Vector3f $$122 = new Vector3f($$11, 0.0f, 0.0f);
            float $$13 = $$12.getLookVector().dot((Vector3fc)$$122);
            if ($$13 > 0.0f && $$0.effects().isSunriseOrSunset($$0.getTimeOfDay($$32))) {
                int $$14 = $$0.effects().getSunriseOrSunsetColor($$0.getTimeOfDay($$32));
                $$8 = Mth.lerp($$13 *= ARGB.alphaFloat($$14), $$8, ARGB.redFloat($$14));
                $$9 = Mth.lerp($$13, $$9, ARGB.greenFloat($$14));
                $$10 = Mth.lerp($$13, $$10, ARGB.blueFloat($$14));
            }
        }
        int $$15 = $$0.getSkyColor($$12.getPosition(), $$32);
        float $$16 = ARGB.redFloat($$15);
        float $$17 = ARGB.greenFloat($$15);
        float $$18 = ARGB.blueFloat($$15);
        float $$19 = 0.25f + 0.75f * (float)$$22 / 32.0f;
        $$19 = 1.0f - (float)Math.pow($$19, 0.25);
        $$8 += ($$16 - $$8) * $$19;
        $$9 += ($$17 - $$9) * $$19;
        $$10 += ($$18 - $$10) * $$19;
        float $$20 = $$0.getRainLevel($$32);
        if ($$20 > 0.0f) {
            float $$21 = 1.0f - $$20 * 0.5f;
            float $$222 = 1.0f - $$20 * 0.4f;
            $$8 *= $$21;
            $$9 *= $$21;
            $$10 *= $$222;
        }
        if (($$23 = $$0.getThunderLevel($$32)) > 0.0f) {
            float $$24 = 1.0f - $$23 * 0.5f;
            $$8 *= $$24;
            $$9 *= $$24;
            $$10 *= $$24;
        }
        return ARGB.colorFromFloat(1.0f, $$8, $$9, $$10);
    }
}

