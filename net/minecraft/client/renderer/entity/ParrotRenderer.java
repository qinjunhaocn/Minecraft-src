/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotRenderer
extends MobRenderer<Parrot, ParrotRenderState, ParrotModel> {
    private static final ResourceLocation RED_BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_red_blue.png");
    private static final ResourceLocation BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_blue.png");
    private static final ResourceLocation GREEN = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_green.png");
    private static final ResourceLocation YELLOW_BLUE = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_yellow_blue.png");
    private static final ResourceLocation GREY = ResourceLocation.withDefaultNamespace("textures/entity/parrot/parrot_grey.png");

    public ParrotRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ParrotModel($$0.bakeLayer(ModelLayers.PARROT)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(ParrotRenderState $$0) {
        return ParrotRenderer.getVariantTexture($$0.variant);
    }

    @Override
    public ParrotRenderState createRenderState() {
        return new ParrotRenderState();
    }

    @Override
    public void extractRenderState(Parrot $$0, ParrotRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.variant = $$0.getVariant();
        float $$3 = Mth.lerp($$2, $$0.oFlap, $$0.flap);
        float $$4 = Mth.lerp($$2, $$0.oFlapSpeed, $$0.flapSpeed);
        $$1.flapAngle = (Mth.sin($$3) + 1.0f) * $$4;
        $$1.pose = ParrotModel.getPose($$0);
    }

    public static ResourceLocation getVariantTexture(Parrot.Variant $$0) {
        return switch ($$0) {
            default -> throw new MatchException(null, null);
            case Parrot.Variant.RED_BLUE -> RED_BLUE;
            case Parrot.Variant.BLUE -> BLUE;
            case Parrot.Variant.GREEN -> GREEN;
            case Parrot.Variant.YELLOW_BLUE -> YELLOW_BLUE;
            case Parrot.Variant.GRAY -> GREY;
        };
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((ParrotRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

