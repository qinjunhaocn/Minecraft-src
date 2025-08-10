/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.RabbitRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitRenderer
extends AgeableMobRenderer<Rabbit, RabbitRenderState, RabbitModel> {
    private static final ResourceLocation RABBIT_BROWN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/brown.png");
    private static final ResourceLocation RABBIT_WHITE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/white.png");
    private static final ResourceLocation RABBIT_BLACK_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/black.png");
    private static final ResourceLocation RABBIT_GOLD_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/gold.png");
    private static final ResourceLocation RABBIT_SALT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/salt.png");
    private static final ResourceLocation RABBIT_WHITE_SPLOTCHED_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/white_splotched.png");
    private static final ResourceLocation RABBIT_TOAST_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/toast.png");
    private static final ResourceLocation RABBIT_EVIL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/rabbit/caerbannog.png");

    public RabbitRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new RabbitModel($$0.bakeLayer(ModelLayers.RABBIT)), new RabbitModel($$0.bakeLayer(ModelLayers.RABBIT_BABY)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(RabbitRenderState $$0) {
        if ($$0.isToast) {
            return RABBIT_TOAST_LOCATION;
        }
        return switch ($$0.variant) {
            default -> throw new MatchException(null, null);
            case Rabbit.Variant.BROWN -> RABBIT_BROWN_LOCATION;
            case Rabbit.Variant.WHITE -> RABBIT_WHITE_LOCATION;
            case Rabbit.Variant.BLACK -> RABBIT_BLACK_LOCATION;
            case Rabbit.Variant.GOLD -> RABBIT_GOLD_LOCATION;
            case Rabbit.Variant.SALT -> RABBIT_SALT_LOCATION;
            case Rabbit.Variant.WHITE_SPLOTCHED -> RABBIT_WHITE_SPLOTCHED_LOCATION;
            case Rabbit.Variant.EVIL -> RABBIT_EVIL_LOCATION;
        };
    }

    @Override
    public RabbitRenderState createRenderState() {
        return new RabbitRenderState();
    }

    @Override
    public void extractRenderState(Rabbit $$0, RabbitRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.jumpCompletion = $$0.getJumpCompletion($$2);
        $$1.isToast = "Toast".equals(ChatFormatting.stripFormatting($$0.getName().getString()));
        $$1.variant = $$0.getVariant();
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
        return this.getTextureLocation((RabbitRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

