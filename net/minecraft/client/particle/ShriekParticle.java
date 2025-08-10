/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
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
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class ShriekParticle
extends TextureSheetParticle {
    private static final float MAGICAL_X_ROT = 1.0472f;
    private int delay;

    ShriekParticle(ClientLevel $$0, double $$1, double $$2, double $$3, int $$4) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.quadSize = 0.85f;
        this.delay = $$4;
        this.lifetime = 30;
        this.gravity = 0.0f;
        this.xd = 0.0;
        this.yd = 0.1;
        this.zd = 0.0;
    }

    @Override
    public float getQuadSize(float $$0) {
        return this.quadSize * Mth.clamp(((float)this.age + $$0) / (float)this.lifetime * 0.75f, 0.0f, 1.0f);
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
        if (this.delay > 0) {
            return;
        }
        this.alpha = 1.0f - Mth.clamp(((float)this.age + $$2) / (float)this.lifetime, 0.0f, 1.0f);
        Quaternionf $$3 = new Quaternionf();
        $$3.rotationX(-1.0472f);
        this.renderRotatedQuad($$0, $$1, $$3, $$2);
        $$3.rotationYXZ((float)(-Math.PI), 1.0472f, 0.0f);
        this.renderRotatedQuad($$0, $$1, $$3, $$2);
    }

    @Override
    public int getLightColor(float $$0) {
        return 240;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        super.tick();
    }

    public static class Provider
    implements ParticleProvider<ShriekParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(ShriekParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            ShriekParticle $$8 = new ShriekParticle($$1, $$2, $$3, $$4, $$0.getDelay());
            $$8.pickSprite(this.sprite);
            $$8.setAlpha(1.0f);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((ShriekParticleOption)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

