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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class HeartParticle
extends TextureSheetParticle {
    HeartParticle(ClientLevel $$0, double $$1, double $$2, double $$3) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.speedUpWhenYMotionIsBlocked = true;
        this.friction = 0.86f;
        this.xd *= (double)0.01f;
        this.yd *= (double)0.01f;
        this.zd *= (double)0.01f;
        this.yd += 0.1;
        this.quadSize *= 1.5f;
        this.lifetime = 16;
        this.hasPhysics = false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float $$0) {
        return this.quadSize * Mth.clamp(((float)this.age + $$0) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
    }

    public static class AngryVillagerProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public AngryVillagerProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            HeartParticle $$8 = new HeartParticle($$1, $$2, $$3 + 0.5, $$4);
            $$8.pickSprite(this.sprite);
            $$8.setColor(1.0f, 1.0f, 1.0f);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            HeartParticle $$8 = new HeartParticle($$1, $$2, $$3, $$4);
            $$8.pickSprite(this.sprite);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

