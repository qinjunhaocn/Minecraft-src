/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class FireflyParticle
extends TextureSheetParticle {
    private static final float PARTICLE_FADE_OUT_LIGHT_TIME = 0.3f;
    private static final float PARTICLE_FADE_IN_LIGHT_TIME = 0.1f;
    private static final float PARTICLE_FADE_OUT_ALPHA_TIME = 0.5f;
    private static final float PARTICLE_FADE_IN_ALPHA_TIME = 0.3f;
    private static final int PARTICLE_MIN_LIFETIME = 200;
    private static final int PARTICLE_MAX_LIFETIME = 300;

    FireflyParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        this.speedUpWhenYMotionIsBlocked = true;
        this.friction = 0.96f;
        this.quadSize *= 0.75f;
        this.yd *= (double)0.8f;
        this.xd *= (double)0.8f;
        this.zd *= (double)0.8f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float $$0) {
        return (int)(255.0f * FireflyParticle.getFadeAmount(this.getLifetimeProgress((float)this.age + $$0), 0.1f, 0.3f));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
            this.remove();
            return;
        }
        this.setAlpha(FireflyParticle.getFadeAmount(this.getLifetimeProgress(this.age), 0.3f, 0.5f));
        if (Math.random() > 0.95 || this.age == 1) {
            this.setParticleSpeed((double)-0.05f + (double)0.1f * Math.random(), (double)-0.05f + (double)0.1f * Math.random(), (double)-0.05f + (double)0.1f * Math.random());
        }
    }

    private float getLifetimeProgress(float $$0) {
        return Mth.clamp($$0 / (float)this.lifetime, 0.0f, 1.0f);
    }

    private static float getFadeAmount(float $$0, float $$1, float $$2) {
        if ($$0 >= 1.0f - $$1) {
            return (1.0f - $$0) / $$1;
        }
        if ($$0 <= $$2) {
            return $$0 / $$2;
        }
        return 1.0f;
    }

    public static class FireflyProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public FireflyProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FireflyParticle $$8 = new FireflyParticle($$1, $$2, $$3, $$4, 0.5 - $$1.random.nextDouble(), $$1.random.nextBoolean() ? $$6 : -$$6, 0.5 - $$1.random.nextDouble());
            $$8.setLifetime($$1.random.nextIntBetweenInclusive(200, 300));
            $$8.scale(1.5f);
            $$8.pickSprite(this.sprite);
            $$8.setAlpha(0.0f);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

