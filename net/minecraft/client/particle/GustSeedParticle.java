/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class GustSeedParticle
extends NoRenderParticle {
    private final double scale;
    private final int tickDelayInBetween;

    GustSeedParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, int $$5, int $$6) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.scale = $$4;
        this.lifetime = $$5;
        this.tickDelayInBetween = $$6;
    }

    @Override
    public void tick() {
        if (this.age % (this.tickDelayInBetween + 1) == 0) {
            for (int $$0 = 0; $$0 < 3; ++$$0) {
                double $$1 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;
                double $$2 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;
                double $$3 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * this.scale;
                this.level.addParticle(ParticleTypes.GUST, $$1, $$2, $$3, (float)this.age / (float)this.lifetime, 0.0, 0.0);
            }
        }
        if (this.age++ == this.lifetime) {
            this.remove();
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final double scale;
        private final int lifetime;
        private final int tickDelayInBetween;

        public Provider(double $$0, int $$1, int $$2) {
            this.scale = $$0;
            this.lifetime = $$1;
            this.tickDelayInBetween = $$2;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new GustSeedParticle($$1, $$2, $$3, $$4, this.scale, this.lifetime, this.tickDelayInBetween);
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

