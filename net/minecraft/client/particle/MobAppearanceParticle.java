/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Quaternionfc;

public class MobAppearanceParticle
extends Particle {
    private final Model model;
    private final RenderType renderType = RenderType.entityTranslucent(ElderGuardianRenderer.GUARDIAN_ELDER_LOCATION);

    MobAppearanceParticle(ClientLevel $$0, double $$1, double $$2, double $$3) {
        super($$0, $$1, $$2, $$3);
        this.model = new GuardianModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELDER_GUARDIAN));
        this.gravity = 0.0f;
        this.lifetime = 30;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void renderCustom(PoseStack $$0, MultiBufferSource $$1, Camera $$2, float $$3) {
        float $$4 = ((float)this.age + $$3) / (float)this.lifetime;
        float $$5 = 0.05f + 0.5f * Mth.sin($$4 * (float)Math.PI);
        int $$6 = ARGB.colorFromFloat($$5, 1.0f, 1.0f, 1.0f);
        $$0.pushPose();
        $$0.mulPose((Quaternionfc)$$2.rotation());
        $$0.mulPose((Quaternionfc)Axis.XP.rotationDegrees(60.0f - 150.0f * $$4));
        float $$7 = 0.42553192f;
        $$0.scale(0.42553192f, -0.42553192f, -0.42553192f);
        $$0.translate(0.0f, -0.56f, 3.5f);
        VertexConsumer $$8 = $$1.getBuffer(this.renderType);
        this.model.renderToBuffer($$0, $$8, 0xF000F0, OverlayTexture.NO_OVERLAY, $$6);
        $$0.popPose();
    }

    @Override
    public void render(VertexConsumer $$0, Camera $$1, float $$2) {
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType $$0, ClientLevel $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7) {
            return new MobAppearanceParticle($$1, $$2, $$3, $$4);
        }

        @Override
        public /* synthetic */ Particle createParticle(ParticleOptions particleOptions, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return this.createParticle((SimpleParticleType)particleOptions, clientLevel, d, d2, d3, d4, d5, d6);
        }
    }
}

