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
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class FlyStraightTowardsParticle
extends TextureSheetParticle {
    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final int startColor;
    private final int endColor;

    FlyStraightTowardsParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, int $$7, int $$8) {
        super($$0, $$1, $$2, $$3);
        this.xd = $$4;
        this.yd = $$5;
        this.zd = $$6;
        this.xStart = $$1;
        this.yStart = $$2;
        this.zStart = $$3;
        this.xo = $$1 + $$4;
        this.yo = $$2 + $$5;
        this.zo = $$3 + $$6;
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.2f);
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 5.0) + 25;
        this.startColor = $$7;
        this.endColor = $$8;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double $$0, double $$1, double $$2) {
    }

    @Override
    public int getLightColor(float $$0) {
        return 240;
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
        float $$0 = (float)this.age / (float)this.lifetime;
        float $$1 = 1.0f - $$0;
        this.x = this.xStart + this.xd * (double)$$1;
        this.y = this.yStart + this.yd * (double)$$1;
        this.z = this.zStart + this.zd * (double)$$1;
        int $$2 = ARGB.lerp($$0, this.startColor, this.endColor);
        this.setColor((float)ARGB.red($$2) / 255.0f, (float)ARGB.green($$2) / 255.0f, (float)ARGB.blue($$2) / 255.0f);
        this.setAlpha((float)ARGB.alpha($$2) / 255.0f);
    }

    public static class OminousSpawnProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public OminousSpawnProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FlyStraightTowardsParticle $$8 = new FlyStraightTowardsParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, -12210434, -1);
            $$8.scale(Mth.randomBetween($$1.getRandom(), 3.0f, 5.0f));
            $$8.pickSprite(this.sprite);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

