/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;

public class SkullBlockRenderer
implements BlockEntityRenderer<SkullBlockEntity> {
    private final Function<SkullBlock.Type, SkullModelBase> modelByType;
    private static final Map<SkullBlock.Type, ResourceLocation> SKIN_BY_TYPE = Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put(SkullBlock.Types.SKELETON, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png"));
        $$0.put(SkullBlock.Types.WITHER_SKELETON, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png"));
        $$0.put(SkullBlock.Types.ZOMBIE, ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png"));
        $$0.put(SkullBlock.Types.CREEPER, ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png"));
        $$0.put(SkullBlock.Types.DRAGON, ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon.png"));
        $$0.put(SkullBlock.Types.PIGLIN, ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin.png"));
        $$0.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultTexture());
    });

    @Nullable
    public static SkullModelBase createModel(EntityModelSet $$0, SkullBlock.Type $$1) {
        if ($$1 instanceof SkullBlock.Types) {
            SkullBlock.Types $$2 = (SkullBlock.Types)$$1;
            return switch ($$2) {
                default -> throw new MatchException(null, null);
                case SkullBlock.Types.SKELETON -> new SkullModel($$0.bakeLayer(ModelLayers.SKELETON_SKULL));
                case SkullBlock.Types.WITHER_SKELETON -> new SkullModel($$0.bakeLayer(ModelLayers.WITHER_SKELETON_SKULL));
                case SkullBlock.Types.PLAYER -> new SkullModel($$0.bakeLayer(ModelLayers.PLAYER_HEAD));
                case SkullBlock.Types.ZOMBIE -> new SkullModel($$0.bakeLayer(ModelLayers.ZOMBIE_HEAD));
                case SkullBlock.Types.CREEPER -> new SkullModel($$0.bakeLayer(ModelLayers.CREEPER_HEAD));
                case SkullBlock.Types.DRAGON -> new DragonHeadModel($$0.bakeLayer(ModelLayers.DRAGON_SKULL));
                case SkullBlock.Types.PIGLIN -> new PiglinHeadModel($$0.bakeLayer(ModelLayers.PIGLIN_HEAD));
            };
        }
        return null;
    }

    public SkullBlockRenderer(BlockEntityRendererProvider.Context $$0) {
        EntityModelSet $$12 = $$0.getModelSet();
        this.modelByType = Util.memoize($$1 -> SkullBlockRenderer.createModel($$12, $$1));
    }

    @Override
    public void render(SkullBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, Vec3 $$6) {
        float $$7 = $$0.getAnimation($$1);
        BlockState $$8 = $$0.getBlockState();
        boolean $$9 = $$8.getBlock() instanceof WallSkullBlock;
        Direction $$10 = $$9 ? $$8.getValue(WallSkullBlock.FACING) : null;
        int $$11 = $$9 ? RotationSegment.convertToSegment($$10.getOpposite()) : $$8.getValue(SkullBlock.ROTATION);
        float $$12 = RotationSegment.convertToDegrees($$11);
        SkullBlock.Type $$13 = ((AbstractSkullBlock)$$8.getBlock()).getType();
        SkullModelBase $$14 = this.modelByType.apply($$13);
        RenderType $$15 = SkullBlockRenderer.getRenderType($$13, $$0.getOwnerProfile());
        SkullBlockRenderer.renderSkull($$10, $$12, $$7, $$2, $$3, $$4, $$14, $$15);
    }

    public static void renderSkull(@Nullable Direction $$0, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5, SkullModelBase $$6, RenderType $$7) {
        $$3.pushPose();
        if ($$0 == null) {
            $$3.translate(0.5f, 0.0f, 0.5f);
        } else {
            float $$8 = 0.25f;
            $$3.translate(0.5f - (float)$$0.getStepX() * 0.25f, 0.25f, 0.5f - (float)$$0.getStepZ() * 0.25f);
        }
        $$3.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer $$9 = $$4.getBuffer($$7);
        $$6.setupAnim($$2, $$1, 0.0f);
        $$6.renderToBuffer($$3, $$9, $$5, OverlayTexture.NO_OVERLAY);
        $$3.popPose();
    }

    public static RenderType getRenderType(SkullBlock.Type $$0, @Nullable ResolvableProfile $$1) {
        if ($$0 != SkullBlock.Types.PLAYER || $$1 == null) {
            return SkullBlockRenderer.getSkullRenderType($$0, null);
        }
        return SkullBlockRenderer.getPlayerSkinRenderType(Minecraft.getInstance().getSkinManager().getInsecureSkin($$1.gameProfile()).texture());
    }

    public static RenderType getSkullRenderType(SkullBlock.Type $$0, @Nullable ResourceLocation $$1) {
        return RenderType.entityCutoutNoCullZOffset($$1 != null ? $$1 : SKIN_BY_TYPE.get($$0));
    }

    public static RenderType getPlayerSkinRenderType(ResourceLocation $$0) {
        return RenderType.entityTranslucent($$0);
    }
}

