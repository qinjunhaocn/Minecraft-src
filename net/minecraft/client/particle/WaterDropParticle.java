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
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class WaterDropParticle
extends TextureSheetParticle {
    protected WaterDropParticle(ClientLevel $$0, double $$1, double $$2, double $$3) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.xd *= (double)0.3f;
        this.yd = Math.random() * (double)0.2f + (double)0.1f;
        this.zd *= (double)0.3f;
        this.setSize(0.01f, 0.01f);
        this.gravity = 0.06f;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        BlockPos $$0;
        double $$1;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }
        this.yd -= (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= (double)0.98f;
        this.yd *= (double)0.98f;
        this.zd *= (double)0.98f;
        if (this.onGround) {
            if (Math.random() < 0.5) {
                this.remove();
            }
            this.xd *= (double)0.7f;
            this.zd *= (double)0.7f;
        }
        if (($$1 = Math.max(this.level.getBlockState($$0 = BlockPos.containing(this.x, this.y, this.z)).getCollisionShape(this.level, $$0).max(Direction.Axis.Y, this.x - (double)$$0.getX(), this.z - (double)$$0.getZ()), (double)this.level.getFluidState($$0).getHeight(this.level, $$0))) > 0.0 && this.y < (double)$$0.getY() + $$1) {
            this.remove();
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
            WaterDropParticle $$8 = new WaterDropParticle($$1, $$2, $$3, $$4);
            $$8.pickSprite(this.sprite);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

