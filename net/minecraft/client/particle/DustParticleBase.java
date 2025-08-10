/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.util.Mth;

public class DustParticleBase<T extends ScalableParticleOptionsBase>
extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected DustParticleBase(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, T $$7, SpriteSet $$8) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.friction = 0.96f;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = $$8;
        this.xd *= (double)0.1f;
        this.yd *= (double)0.1f;
        this.zd *= (double)0.1f;
        this.quadSize *= 0.75f * ((ScalableParticleOptionsBase)$$7).getScale();
        int $$9 = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
        this.lifetime = (int)Math.max((float)$$9 * ((ScalableParticleOptionsBase)$$7).getScale(), 1.0f);
        this.setSpriteFromAge($$8);
    }

    protected float randomizeColor(float $$0, float $$1) {
        return (this.random.nextFloat() * 0.2f + 0.8f) * $$0 * $$1;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float $$0) {
        return this.quadSize * Mth.clamp(((float)this.age + $$0) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }
}

