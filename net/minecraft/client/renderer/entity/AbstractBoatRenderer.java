/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public abstract class AbstractBoatRenderer
extends EntityRenderer<AbstractBoat, BoatRenderState> {
    public AbstractBoatRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(BoatRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        $$1.pushPose();
        $$1.translate(0.0f, 0.375f, 0.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0f - $$0.yRot));
        float $$4 = $$0.hurtTime;
        if ($$4 > 0.0f) {
            $$1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.sin($$4) * $$4 * $$0.damageTime / 10.0f * (float)$$0.hurtDir));
        }
        if (!$$0.isUnderWater && !Mth.equal($$0.bubbleAngle, 0.0f)) {
            $$1.mulPose((Quaternionfc)new Quaternionf().setAngleAxis($$0.bubbleAngle * ((float)Math.PI / 180), 1.0f, 0.0f, 1.0f));
        }
        $$1.scale(-1.0f, -1.0f, 1.0f);
        $$1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0f));
        EntityModel<BoatRenderState> $$5 = this.model();
        $$5.setupAnim($$0);
        VertexConsumer $$6 = $$2.getBuffer(this.renderType());
        $$5.renderToBuffer($$1, $$6, $$3, OverlayTexture.NO_OVERLAY);
        this.renderTypeAdditions($$0, $$1, $$2, $$3);
        $$1.popPose();
        super.render($$0, $$1, $$2, $$3);
    }

    protected void renderTypeAdditions(BoatRenderState $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
    }

    protected abstract EntityModel<BoatRenderState> model();

    protected abstract RenderType renderType();

    @Override
    public BoatRenderState createRenderState() {
        return new BoatRenderState();
    }

    @Override
    public void extractRenderState(AbstractBoat $$0, BoatRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.yRot = $$0.getYRot($$2);
        $$1.hurtTime = (float)$$0.getHurtTime() - $$2;
        $$1.hurtDir = $$0.getHurtDir();
        $$1.damageTime = Math.max($$0.getDamage() - $$2, 0.0f);
        $$1.bubbleAngle = $$0.getBubbleAngle($$2);
        $$1.isUnderWater = $$0.isUnderWater();
        $$1.rowingTimeLeft = $$0.getRowingTime(0, $$2);
        $$1.rowingTimeRight = $$0.getRowingTime(1, $$2);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

