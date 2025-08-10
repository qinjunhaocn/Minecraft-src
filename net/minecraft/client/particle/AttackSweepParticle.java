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

public class AttackSweepParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;

    AttackSweepParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, SpriteSet $$5) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        float $$6;
        this.sprites = $$5;
        this.lifetime = 4;
        this.rCol = $$6 = this.random.nextFloat() * 0.6f + 0.4f;
        this.gCol = $$6;
        this.bCol = $$6;
        this.quadSize = 1.0f - (float)$$4 * 0.5f;
        this.setSpriteFromAge($$5);
    }

    @Override
    public int getLightColor(float $$0) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new AttackSweepParticle($$1, $$2, $$3, $$4, $$5, this.sprites);
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

