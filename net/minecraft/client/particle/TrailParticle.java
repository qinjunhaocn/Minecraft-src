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
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class TrailParticle
extends TextureSheetParticle {
    private final Vec3 target;

    TrailParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, Vec3 $$7, int $$8) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        $$8 = ARGB.scaleRGB($$8, 0.875f + this.random.nextFloat() * 0.25f, 0.875f + this.random.nextFloat() * 0.25f, 0.875f + this.random.nextFloat() * 0.25f);
        this.rCol = (float)ARGB.red($$8) / 255.0f;
        this.gCol = (float)ARGB.green($$8) / 255.0f;
        this.bCol = (float)ARGB.blue($$8) / 255.0f;
        this.quadSize = 0.26f;
        this.target = $$7;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
        int $$0 = this.lifetime - this.age;
        double $$1 = 1.0 / (double)$$0;
        this.x = Mth.lerp($$1, this.x, this.target.x());
        this.y = Mth.lerp($$1, this.y, this.target.y());
        this.z = Mth.lerp($$1, this.z, this.target.z());
    }

    @Override
    public int getLightColor(float $$0) {
        return 0xF000F0;
    }

    public static class Provider
    implements ParticleProvider<TrailParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(TrailParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            TrailParticle $$8 = new TrailParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, $$0.target(), $$0.color());
            $$8.pickSprite(this.sprite);
            $$8.setLifetime($$0.duration());
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((TrailParticleOption)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

