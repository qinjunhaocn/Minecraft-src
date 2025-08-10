/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;

public class WhiteAshParticle
extends BaseAshSmokeParticle {
    private static final int COLOR_RGB24 = 12235202;

    protected WhiteAshParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, float $$7, SpriteSet $$8) {
        super($$0, $$1, $$2, $$3, 0.1f, -0.1f, 0.1f, $$4, $$5, $$6, $$7, $$8, 0.0f, 20, 0.0125f, false);
        this.rCol = (float)ARGB.red(12235202) / 255.0f;
        this.gCol = (float)ARGB.green(12235202) / 255.0f;
        this.bCol = (float)ARGB.blue(12235202) / 255.0f;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            RandomSource $$8 = $$1.random;
            double $$9 = (double)$$8.nextFloat() * -1.9 * (double)$$8.nextFloat() * 0.1;
            double $$10 = (double)$$8.nextFloat() * -0.5 * (double)$$8.nextFloat() * 0.1 * 5.0;
            double $$11 = (double)$$8.nextFloat() * -1.9 * (double)$$8.nextFloat() * 0.1;
            return new WhiteAshParticle($$1, $$2, $$3, $$4, $$9, $$10, $$11, 1.0f, this.sprites);
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

