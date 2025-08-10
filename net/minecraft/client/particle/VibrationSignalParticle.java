/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class VibrationSignalParticle
extends TextureSheetParticle {
    private final PositionSource target;
    private float rot;
    private float rotO;
    private float pitch;
    private float pitchO;

    VibrationSignalParticle(ClientLevel $$0, double $$1, double $$2, double $$3, PositionSource $$4, int $$5) {
        super($$0, $$1, $$2, $$3, 0.0, 0.0, 0.0);
        this.quadSize = 0.3f;
        this.target = $$4;
        this.lifetime = $$5;
        Optional<Vec3> $$6 = $$4.getPosition($$0);
        if ($$6.isPresent()) {
            Vec3 $$7 = $$6.get();
            double $$8 = $$1 - $$7.x();
            double $$9 = $$2 - $$7.y();
            double $$10 = $$3 - $$7.z();
            this.rotO = this.rot = (float)Mth.atan2($$8, $$10);
            this.pitchO = this.pitch = (float)Mth.atan2($$9, Math.sqrt($$8 * $$8 + $$10 * $$10));
        }
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
        float $$3 = Mth.sin(((float)this.age + $$2 - (float)Math.PI * 2) * 0.05f) * 2.0f;
        float $$4 = Mth.lerp($$2, this.rotO, this.rot);
        float $$5 = Mth.lerp($$2, this.pitchO, this.pitch) + 1.5707964f;
        Quaternionf $$6 = new Quaternionf();
        $$6.rotationY($$4).rotateX(-$$5).rotateY($$3);
        this.renderRotatedQuad($$0, $$1, $$6, $$2);
        $$6.rotationY((float)(-Math.PI) + $$4).rotateX($$5).rotateY($$3);
        this.renderRotatedQuad($$0, $$1, $$6, $$2);
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
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        Optional<Vec3> $$0 = this.target.getPosition(this.level);
        if ($$0.isEmpty()) {
            this.remove();
            return;
        }
        int $$1 = this.lifetime - this.age;
        double $$2 = 1.0 / (double)$$1;
        Vec3 $$3 = $$0.get();
        this.x = Mth.lerp($$2, this.x, $$3.x());
        this.y = Mth.lerp($$2, this.y, $$3.y());
        this.z = Mth.lerp($$2, this.z, $$3.z());
        double $$4 = this.x - $$3.x();
        double $$5 = this.y - $$3.y();
        double $$6 = this.z - $$3.z();
        this.rotO = this.rot;
        this.rot = (float)Mth.atan2($$4, $$6);
        this.pitchO = this.pitch;
        this.pitch = (float)Mth.atan2($$5, Math.sqrt($$4 * $$4 + $$6 * $$6));
    }

    public static class Provider
    implements ParticleProvider<VibrationParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet $$0) {
            this.sprite = $$0;
        }

        @Override
        public Particle createParticle(VibrationParticleOption $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            VibrationSignalParticle $$8 = new VibrationSignalParticle($$1, $$2, $$3, $$4, $$0.getDestination(), $$0.getArrivalInTicks());
            $$8.pickSprite(this.sprite);
            $$8.setAlpha(1.0f);
            return $$8;
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((VibrationParticleOption)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

