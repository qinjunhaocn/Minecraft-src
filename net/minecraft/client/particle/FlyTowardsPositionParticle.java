/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class FlyTowardsPositionParticle
extends TextureSheetParticle {
    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final boolean isGlowing;
    private final Particle.LifetimeAlpha lifetimeAlpha;

    FlyTowardsPositionParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, false, Particle.LifetimeAlpha.ALWAYS_OPAQUE);
    }

    FlyTowardsPositionParticle(ClientLevel $$0, double $$1, double $$2, double $$3, double $$4, double $$5, double $$6, boolean $$7, Particle.LifetimeAlpha $$8) {
        super($$0, $$1, $$2, $$3);
        this.isGlowing = $$7;
        this.lifetimeAlpha = $$8;
        this.setAlpha($$8.startAlpha());
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
        float $$9 = this.random.nextFloat() * 0.6f + 0.4f;
        this.rCol = 0.9f * $$9;
        this.gCol = 0.9f * $$9;
        this.bCol = $$9;
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 10.0) + 30;
    }

    @Override
    public ParticleRenderType getRenderType() {
        if (this.lifetimeAlpha.isOpaque()) {
            return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
        }
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void move(double $$0, double $$1, double $$2) {
        this.setBoundingBox(this.getBoundingBox().move($$0, $$1, $$2));
        this.setLocationFromBoundingbox();
    }

    @Override
    public int getLightColor(float $$0) {
        if (this.isGlowing) {
            return 240;
        }
        int $$1 = super.getLightColor($$0);
        float $$2 = (float)this.age / (float)this.lifetime;
        $$2 *= $$2;
        $$2 *= $$2;
        int $$3 = $$1 & 0xFF;
        int $$4 = $$1 >> 16 & 0xFF;
        if (($$4 += (int)($$2 * 15.0f * 16.0f)) > 240) {
            $$4 = 240;
        }
        return $$3 | $$4 << 16;
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
        $$0 = 1.0f - $$0;
        float $$1 = 1.0f - $$0;
        $$1 *= $$1;
        $$1 *= $$1;
        this.x = this.xStart + this.xd * (double)$$0;
        this.y = this.yStart + this.yd * (double)$$0 - (double)($$1 * 1.2f);
        this.z = this.zStart + this.zd * (double)$$0;
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
        this.setAlpha(this.lifetimeAlpha.currentAlphaForAge(this.age, this.lifetime, $$2));
        super.render($$0, $$1, $$2);
    }

    public static class VaultConnectionProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public VaultConnectionProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FlyTowardsPositionParticle $$8 = new FlyTowardsPositionParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7, true, new Particle.LifetimeAlpha(0.0f, 0.6f, 0.25f, 1.0f));
            $$8.scale(1.5f);
            $$8.pickSprite(this.sprite);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class NautilusProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public NautilusProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FlyTowardsPositionParticle $$8 = new FlyTowardsPositionParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7);
            $$8.pickSprite(this.sprite);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }

    public static class EnchantProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public EnchantProvider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            FlyTowardsPositionParticle $$8 = new FlyTowardsPositionParticle($$1, $$2, $$3, $$4, $$5, $$6, $$7);
            $$8.pickSprite(this.sprite);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

