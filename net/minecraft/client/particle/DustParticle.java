/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticleBase;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.joml.Vector3f;

public class DustParticle
extends DustParticleBase<DustParticleOptions> {
    protected DustParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, DustParticleOptions $$7, SpriteSet $$8) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
        float $$9 = this.random.nextFloat() * 0.4f + 0.6f;
        Vector3f $$10 = $$7.getColor();
        this.rCol = this.randomizeColor($$10.x(), $$9);
        this.gCol = this.randomizeColor($$10.y(), $$9);
        this.bCol = this.randomizeColor($$10.z(), $$9);
    }

    public static class Provider
    implements ParticleProvider<DustParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(DustParticleOptions $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new DustParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, $$0, this.sprites);
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((DustParticleOptions)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

