/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;

public abstract class ScalableParticleOptionsBase
implements ParticleOptions {
    public static final float MIN_SCALE = 0.01f;
    public static final float MAX_SCALE = 4.0f;
    protected static final Codec<Float> SCALE = Codec.FLOAT.validate($$0 -> $$0.floatValue() >= 0.01f && $$0.floatValue() <= 4.0f ? DataResult.success((Object)$$0) : DataResult.error(() -> "Value must be within range [0.01;4.0]: " + $$0));
    private final float scale;

    public ScalableParticleOptionsBase(float $$0) {
        this.scale = Mth.clamp($$0, 0.01f, 4.0f);
    }

    public float getScale() {
        return this.scale;
    }
}

