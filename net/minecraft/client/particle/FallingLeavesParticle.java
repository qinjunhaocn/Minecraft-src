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
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class FallingLeavesParticle
extends TextureSheetParticle {
    private static final float ACCELERATION_SCALE = 0.0025f;
    private static final int INITIAL_LIFETIME = 300;
    private static final int CURVE_ENDPOINT_TIME = 300;
    private float rotSpeed;
    private final float particleRandom;
    private final float spinAcceleration;
    private final float windBig;
    private boolean swirl;
    private boolean flowAway;
    private double xaFlowScale;
    private double zaFlowScale;
    private double swirlPeriod;

    protected FallingLeavesParticle(ClientLevel $$0, double $$1, double $$2, double $$3, SpriteSet $$4, float $$5, float $$6, boolean $$7, boolean $$8, float $$9, float $$10) {
        super($$0, $$1, $$2, $$3);
        float $$11;
        this.setSprite($$4.get(this.random.nextInt(12), 12));
        this.rotSpeed = (float)Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.particleRandom = this.random.nextFloat();
        this.spinAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
        this.windBig = $$6;
        this.swirl = $$7;
        this.flowAway = $$8;
        this.lifetime = 300;
        this.gravity = $$5 * 1.2f * 0.0025f;
        this.quadSize = $$11 = $$9 * (this.random.nextBoolean() ? 0.05f : 0.075f);
        this.setSize($$11, $$11);
        this.friction = 1.0f;
        this.yd = -$$10;
        this.xaFlowScale = Math.cos(Math.toRadians(this.particleRandom * 60.0f)) * (double)this.windBig;
        this.zaFlowScale = Math.sin(Math.toRadians(this.particleRandom * 60.0f)) * (double)this.windBig;
        this.swirlPeriod = Math.toRadians(1000.0f + this.particleRandom * 3000.0f);
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
        if (this.lifetime-- <= 0) {
            this.remove();
        }
        if (this.removed) {
            return;
        }
        float $$0 = 300 - this.lifetime;
        float $$1 = Math.min($$0 / 300.0f, 1.0f);
        double $$2 = 0.0;
        double $$3 = 0.0;
        if (this.flowAway) {
            $$2 += this.xaFlowScale * Math.pow($$1, 1.25);
            $$3 += this.zaFlowScale * Math.pow($$1, 1.25);
        }
        if (this.swirl) {
            $$2 += (double)$$1 * Math.cos((double)$$1 * this.swirlPeriod) * (double)this.windBig;
            $$3 += (double)$$1 * Math.sin((double)$$1 * this.swirlPeriod) * (double)this.windBig;
        }
        this.xd += $$2 * (double)0.0025f;
        this.zd += $$3 * (double)0.0025f;
        this.yd -= (double)this.gravity;
        this.rotSpeed += this.spinAcceleration / 20.0f;
        this.oRoll = this.roll;
        this.roll += this.rotSpeed / 20.0f;
        this.move(this.xd, this.yd, this.zd);
        if (this.onGround || this.lifetime < 299 && (this.xd == 0.0 || this.zd == 0.0)) {
            this.remove();
        }
        if (this.removed) {
            return;
        }
        this.xd *= (double)this.friction;
        this.yd *= (double)this.friction;
        this.zd *= (double)this.friction;
    }

    public static class TintedLeavesProvider
    implements ParticleProvider<ColorParticleOption> {
        private final SpriteSet sprites;

        public TintedLeavesProvider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(ColorParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FallingLeavesParticle $$8 = new FallingLeavesParticle($$1, $$2, $$3, $$4, this.sprites, 0.07f, 10.0f, true, false, 2.0f, 0.021f);
            $$8.setColor($$0.getRed(), $$0.getGreen(), $$0.getBlue());
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((ColorParticleOption)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class PaleOakProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public PaleOakProvider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new FallingLeavesParticle($$1, $$2, $$3, $$4, this.sprites, 0.07f, 10.0f, true, false, 2.0f, 0.021f);
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class CherryProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public CherryProvider(SpriteSet $$0) {
            this.sprites = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new FallingLeavesParticle($$1, $$2, $$3, $$4, this.sprites, 0.25f, 2.0f, false, true, 1.0f, 0.0f);
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

