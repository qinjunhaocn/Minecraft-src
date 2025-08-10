/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class ShulkerRenderer
extends MobRenderer<Shulker, ShulkerRenderState, ShulkerModel> {
    private static final ResourceLocation DEFAULT_TEXTURE_LOCATION = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().withPath($$0 -> "textures/" + $$0 + ".png");
    private static final ResourceLocation[] TEXTURE_LOCATION = (ResourceLocation[])Sheets.SHULKER_TEXTURE_LOCATION.stream().map($$02 -> $$02.texture().withPath($$0 -> "textures/" + $$0 + ".png")).toArray(ResourceLocation[]::new);

    public ShulkerRenderer(EntityRendererProvider.Context $$0) {
        super($$0, new ShulkerModel($$0.bakeLayer(ModelLayers.SHULKER)), 0.0f);
    }

    @Override
    public Vec3 getRenderOffset(ShulkerRenderState $$0) {
        return $$0.renderOffset;
    }

    @Override
    public boolean shouldRender(Shulker $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        if (super.shouldRender($$0, $$1, $$2, $$3, $$4)) {
            return true;
        }
        Vec3 $$5 = $$0.getRenderPosition(0.0f);
        if ($$5 == null) {
            return false;
        }
        EntityType<?> $$6 = $$0.getType();
        float $$7 = $$6.getHeight() / 2.0f;
        float $$8 = $$6.getWidth() / 2.0f;
        Vec3 $$9 = Vec3.atBottomCenterOf($$0.blockPosition());
        return $$1.isVisible(new AABB($$5.x, $$5.y + (double)$$7, $$5.z, $$9.x, $$9.y + (double)$$7, $$9.z).inflate($$8, $$7, $$8));
    }

    @Override
    public ResourceLocation getTextureLocation(ShulkerRenderState $$0) {
        return ShulkerRenderer.getTextureLocation($$0.color);
    }

    @Override
    public ShulkerRenderState createRenderState() {
        return new ShulkerRenderState();
    }

    @Override
    public void extractRenderState(Shulker $$0, ShulkerRenderState $$1, float $$2) {
        super.extractRenderState($$0, $$1, $$2);
        $$1.renderOffset = (Vec3)Objects.requireNonNullElse((Object)$$0.getRenderPosition($$2), (Object)Vec3.ZERO);
        $$1.color = $$0.getColor();
        $$1.peekAmount = $$0.getClientPeekAmount($$2);
        $$1.yHeadRot = $$0.yHeadRot;
        $$1.yBodyRot = $$0.yBodyRot;
        $$1.attachFace = $$0.getAttachFace();
    }

    public static ResourceLocation getTextureLocation(@Nullable DyeColor $$0) {
        if ($$0 == null) {
            return DEFAULT_TEXTURE_LOCATION;
        }
        return TEXTURE_LOCATION[$$0.getId()];
    }

    @Override
    protected void setupRotations(ShulkerRenderState $$0, PoseStack $$1, float $$2, float $$3) {
        super.setupRotations($$0, $$1, $$2 + 180.0f, $$3);
        $$1.rotateAround((Quaternionfc)$$0.attachFace.getOpposite().getRotation(), 0.0f, 0.5f, 0.0f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

