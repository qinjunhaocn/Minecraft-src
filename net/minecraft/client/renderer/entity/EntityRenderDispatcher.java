/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.entity.state.ServerHitboxesRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class EntityRenderDispatcher
implements ResourceManagerReloadListener {
    private static final RenderType SHADOW_RENDER_TYPE = RenderType.entityShadow(ResourceLocation.withDefaultNamespace("textures/misc/shadow.png"));
    private static final float MAX_SHADOW_RADIUS = 32.0f;
    private static final float SHADOW_POWER_FALLOFF_Y = 0.5f;
    private Map<EntityType<?>, EntityRenderer<?, ?>> renderers = ImmutableMap.of();
    private Map<PlayerSkin.Model, EntityRenderer<? extends Player, ?>> playerRenderers = Map.of();
    public final TextureManager textureManager;
    private Level level;
    public Camera camera;
    private Quaternionf cameraOrientation;
    public Entity crosshairPickEntity;
    private final ItemModelResolver itemModelResolver;
    private final MapRenderer mapRenderer;
    private final BlockRenderDispatcher blockRenderDispatcher;
    private final ItemInHandRenderer itemInHandRenderer;
    private final Font font;
    public final Options options;
    private final Supplier<EntityModelSet> entityModels;
    private final EquipmentAssetManager equipmentAssets;
    private boolean shouldRenderShadow = true;
    private boolean renderHitBoxes;

    public <E extends Entity> int getPackedLightCoords(E $$0, float $$1) {
        return this.getRenderer((EntityRenderState)((Object)$$0)).getPackedLightCoords($$0, $$1);
    }

    public EntityRenderDispatcher(Minecraft $$0, TextureManager $$1, ItemModelResolver $$2, ItemRenderer $$3, MapRenderer $$4, BlockRenderDispatcher $$5, Font $$6, Options $$7, Supplier<EntityModelSet> $$8, EquipmentAssetManager $$9) {
        this.textureManager = $$1;
        this.itemModelResolver = $$2;
        this.mapRenderer = $$4;
        this.itemInHandRenderer = new ItemInHandRenderer($$0, this, $$3, $$2);
        this.blockRenderDispatcher = $$5;
        this.font = $$6;
        this.options = $$7;
        this.entityModels = $$8;
        this.equipmentAssets = $$9;
    }

    public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T $$0) {
        if ($$0 instanceof AbstractClientPlayer) {
            AbstractClientPlayer $$1 = (AbstractClientPlayer)$$0;
            PlayerSkin.Model $$2 = $$1.getSkin().model();
            EntityRenderer<? extends Player, ?> $$3 = this.playerRenderers.get((Object)$$2);
            if ($$3 != null) {
                return $$3;
            }
            return this.playerRenderers.get((Object)PlayerSkin.Model.WIDE);
        }
        return this.renderers.get($$0.getType());
    }

    public <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S $$0) {
        if ($$0 instanceof PlayerRenderState) {
            PlayerRenderState $$1 = (PlayerRenderState)$$0;
            PlayerSkin.Model $$2 = $$1.skin.model();
            EntityRenderer<? extends Player, ?> $$3 = this.playerRenderers.get((Object)$$2);
            if ($$3 != null) {
                return $$3;
            }
            return this.playerRenderers.get((Object)PlayerSkin.Model.WIDE);
        }
        return this.renderers.get($$0.entityType);
    }

    public void prepare(Level $$0, Camera $$1, Entity $$2) {
        this.level = $$0;
        this.camera = $$1;
        this.cameraOrientation = $$1.rotation();
        this.crosshairPickEntity = $$2;
    }

    public void overrideCameraOrientation(Quaternionf $$0) {
        this.cameraOrientation = $$0;
    }

    public void setRenderShadow(boolean $$0) {
        this.shouldRenderShadow = $$0;
    }

    public void setRenderHitBoxes(boolean $$0) {
        this.renderHitBoxes = $$0;
    }

    public boolean shouldRenderHitBoxes() {
        return this.renderHitBoxes;
    }

    public <E extends Entity> boolean shouldRender(E $$0, Frustum $$1, double $$2, double $$3, double $$4) {
        EntityRenderer<?, E> $$5 = this.getRenderer((EntityRenderState)((Object)$$0));
        return $$5.shouldRender($$0, $$1, $$2, $$3, $$4);
    }

    public <E extends Entity> void render(E $$0, double $$1, double $$2, double $$3, float $$4, PoseStack $$5, MultiBufferSource $$6, int $$7) {
        EntityRenderer<?, E> $$8 = this.getRenderer((EntityRenderState)((Object)$$0));
        this.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8);
    }

    /*
     * WARNING - void declaration
     */
    private <E extends Entity, S extends EntityRenderState> void render(E $$0, double $$1, double $$2, double $$3, float $$4, PoseStack $$5, MultiBufferSource $$6, int $$7, EntityRenderer<? super E, S> $$8) {
        try {
            S $$9 = $$8.createRenderState($$0, $$4);
        } catch (Throwable $$10) {
            CrashReport $$11 = CrashReport.forThrowable($$10, "Extracting render state for an entity in world");
            CrashReportCategory $$12 = $$11.addCategory("Entity being extracted");
            $$0.fillCrashReportCategory($$12);
            CrashReportCategory $$13 = this.fillRendererDetails($$1, $$2, $$3, $$8, $$11);
            $$13.setDetail("Delta", Float.valueOf($$4));
            throw new ReportedException($$11);
        }
        try {
            void $$14;
            this.render($$14, $$1, $$2, $$3, $$5, $$6, $$7, $$8);
        } catch (Throwable $$15) {
            CrashReport $$16 = CrashReport.forThrowable($$15, "Rendering entity in world");
            CrashReportCategory $$17 = $$16.addCategory("Entity being rendered");
            $$0.fillCrashReportCategory($$17);
            throw new ReportedException($$16);
        }
    }

    public <S extends EntityRenderState> void render(S $$0, double $$1, double $$2, double $$3, PoseStack $$4, MultiBufferSource $$5, int $$6) {
        EntityRenderer<?, S> $$7 = this.getRenderer($$0);
        this.render($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    private <S extends EntityRenderState> void render(S $$0, double $$1, double $$2, double $$3, PoseStack $$4, MultiBufferSource $$5, int $$6, EntityRenderer<?, S> $$7) {
        try {
            double $$13;
            float $$14;
            float $$12;
            Vec3 $$8 = $$7.getRenderOffset($$0);
            double $$9 = $$1 + $$8.x();
            double $$10 = $$2 + $$8.y();
            double $$11 = $$3 + $$8.z();
            $$4.pushPose();
            $$4.translate($$9, $$10, $$11);
            $$7.render($$0, $$4, $$5, $$6);
            if ($$0.displayFireAnimation) {
                this.renderFlame($$4, $$5, $$0, Mth.rotationAroundAxis(Mth.Y_AXIS, this.cameraOrientation, new Quaternionf()));
            }
            if ($$0 instanceof PlayerRenderState) {
                $$4.translate(-$$8.x(), -$$8.y(), -$$8.z());
            }
            if (this.options.entityShadows().get().booleanValue() && this.shouldRenderShadow && !$$0.isInvisible && ($$12 = $$7.getShadowRadius($$0)) > 0.0f && ($$14 = (float)((1.0 - ($$13 = $$0.distanceToCameraSq) / 256.0) * (double)$$7.getShadowStrength($$0))) > 0.0f) {
                EntityRenderDispatcher.renderShadow($$4, $$5, $$0, $$14, this.level, Math.min($$12, 32.0f));
            }
            if (!($$0 instanceof PlayerRenderState)) {
                $$4.translate(-$$8.x(), -$$8.y(), -$$8.z());
            }
            if ($$0.hitboxesRenderState != null) {
                this.renderHitboxes($$4, $$0, $$0.hitboxesRenderState, $$5);
            }
            $$4.popPose();
        } catch (Throwable $$15) {
            CrashReport $$16 = CrashReport.forThrowable($$15, "Rendering entity in world");
            CrashReportCategory $$17 = $$16.addCategory("EntityRenderState being rendered");
            $$0.fillCrashReportCategory($$17);
            this.fillRendererDetails($$1, $$2, $$3, $$7, $$16);
            throw new ReportedException($$16);
        }
    }

    private <S extends EntityRenderState> CrashReportCategory fillRendererDetails(double $$0, double $$1, double $$2, EntityRenderer<?, S> $$3, CrashReport $$4) {
        CrashReportCategory $$5 = $$4.addCategory("Renderer details");
        $$5.setDetail("Assigned renderer", $$3);
        $$5.setDetail("Location", CrashReportCategory.formatLocation((LevelHeightAccessor)this.level, $$0, $$1, $$2));
        return $$5;
    }

    private void renderHitboxes(PoseStack $$0, EntityRenderState $$1, HitboxesRenderState $$2, MultiBufferSource $$3) {
        VertexConsumer $$4 = $$3.getBuffer(RenderType.lines());
        EntityRenderDispatcher.renderHitboxesAndViewVector($$0, $$2, $$4, $$1.eyeHeight);
        ServerHitboxesRenderState $$5 = $$1.serverHitboxesRenderState;
        if ($$5 != null) {
            if ($$5.missing()) {
                HitboxRenderState $$6 = (HitboxRenderState)((Object)$$2.hitboxes().getFirst());
                DebugRenderer.renderFloatingText($$0, $$3, "Missing", $$1.x, $$6.y1() + 1.5, $$1.z, -65536);
            } else if ($$5.hitboxes() != null) {
                $$0.pushPose();
                $$0.translate($$5.serverEntityX() - $$1.x, $$5.serverEntityY() - $$1.y, $$5.serverEntityZ() - $$1.z);
                EntityRenderDispatcher.renderHitboxesAndViewVector($$0, $$5.hitboxes(), $$4, $$5.eyeHeight());
                Vec3 $$7 = new Vec3($$5.deltaMovementX(), $$5.deltaMovementY(), $$5.deltaMovementZ());
                ShapeRenderer.renderVector($$0, $$4, new Vector3f(), $$7, -256);
                $$0.popPose();
            }
        }
    }

    private static void renderHitboxesAndViewVector(PoseStack $$0, HitboxesRenderState $$1, VertexConsumer $$2, float $$3) {
        for (HitboxRenderState $$4 : $$1.hitboxes()) {
            EntityRenderDispatcher.renderHitbox($$0, $$2, $$4);
        }
        Vec3 $$5 = new Vec3($$1.viewX(), $$1.viewY(), $$1.viewZ());
        ShapeRenderer.renderVector($$0, $$2, new Vector3f(0.0f, $$3, 0.0f), $$5.scale(2.0), -16776961);
    }

    private static void renderHitbox(PoseStack $$0, VertexConsumer $$1, HitboxRenderState $$2) {
        $$0.pushPose();
        $$0.translate($$2.offsetX(), $$2.offsetY(), $$2.offsetZ());
        ShapeRenderer.renderLineBox($$0, $$1, $$2.x0(), $$2.y0(), $$2.z0(), $$2.x1(), $$2.y1(), $$2.z1(), $$2.red(), $$2.green(), $$2.blue(), 1.0f);
        $$0.popPose();
    }

    private void renderFlame(PoseStack $$0, MultiBufferSource $$1, EntityRenderState $$2, Quaternionf $$3) {
        TextureAtlasSprite $$4 = ModelBakery.FIRE_0.sprite();
        TextureAtlasSprite $$5 = ModelBakery.FIRE_1.sprite();
        $$0.pushPose();
        float $$6 = $$2.boundingBoxWidth * 1.4f;
        $$0.scale($$6, $$6, $$6);
        float $$7 = 0.5f;
        float $$8 = 0.0f;
        float $$9 = $$2.boundingBoxHeight / $$6;
        float $$10 = 0.0f;
        $$0.mulPose((Quaternionfc)$$3);
        $$0.translate(0.0f, 0.0f, 0.3f - (float)((int)$$9) * 0.02f);
        float $$11 = 0.0f;
        int $$12 = 0;
        VertexConsumer $$13 = $$1.getBuffer(Sheets.cutoutBlockSheet());
        PoseStack.Pose $$14 = $$0.last();
        while ($$9 > 0.0f) {
            TextureAtlasSprite $$15 = $$12 % 2 == 0 ? $$4 : $$5;
            float $$16 = $$15.getU0();
            float $$17 = $$15.getV0();
            float $$18 = $$15.getU1();
            float $$19 = $$15.getV1();
            if ($$12 / 2 % 2 == 0) {
                float $$20 = $$18;
                $$18 = $$16;
                $$16 = $$20;
            }
            EntityRenderDispatcher.fireVertex($$14, $$13, -$$7 - 0.0f, 0.0f - $$10, $$11, $$18, $$19);
            EntityRenderDispatcher.fireVertex($$14, $$13, $$7 - 0.0f, 0.0f - $$10, $$11, $$16, $$19);
            EntityRenderDispatcher.fireVertex($$14, $$13, $$7 - 0.0f, 1.4f - $$10, $$11, $$16, $$17);
            EntityRenderDispatcher.fireVertex($$14, $$13, -$$7 - 0.0f, 1.4f - $$10, $$11, $$18, $$17);
            $$9 -= 0.45f;
            $$10 -= 0.45f;
            $$7 *= 0.9f;
            $$11 -= 0.03f;
            ++$$12;
        }
        $$0.popPose();
    }

    private static void fireVertex(PoseStack.Pose $$0, VertexConsumer $$1, float $$2, float $$3, float $$4, float $$5, float $$6) {
        $$1.addVertex($$0, $$2, $$3, $$4).setColor(-1).setUv($$5, $$6).setUv1(0, 10).setLight(240).setNormal($$0, 0.0f, 1.0f, 0.0f);
    }

    private static void renderShadow(PoseStack $$0, MultiBufferSource $$1, EntityRenderState $$2, float $$3, LevelReader $$4, float $$5) {
        float $$6 = Math.min($$3 / 0.5f, $$5);
        int $$7 = Mth.floor($$2.x - (double)$$5);
        int $$8 = Mth.floor($$2.x + (double)$$5);
        int $$9 = Mth.floor($$2.y - (double)$$6);
        int $$10 = Mth.floor($$2.y);
        int $$11 = Mth.floor($$2.z - (double)$$5);
        int $$12 = Mth.floor($$2.z + (double)$$5);
        PoseStack.Pose $$13 = $$0.last();
        VertexConsumer $$14 = $$1.getBuffer(SHADOW_RENDER_TYPE);
        BlockPos.MutableBlockPos $$15 = new BlockPos.MutableBlockPos();
        for (int $$16 = $$11; $$16 <= $$12; ++$$16) {
            for (int $$17 = $$7; $$17 <= $$8; ++$$17) {
                $$15.set($$17, 0, $$16);
                ChunkAccess $$18 = $$4.getChunk($$15);
                for (int $$19 = $$9; $$19 <= $$10; ++$$19) {
                    $$15.setY($$19);
                    float $$20 = $$3 - (float)($$2.y - (double)$$15.getY()) * 0.5f;
                    EntityRenderDispatcher.renderBlockShadow($$13, $$14, $$18, $$4, $$15, $$2.x, $$2.y, $$2.z, $$5, $$20);
                }
            }
        }
    }

    private static void renderBlockShadow(PoseStack.Pose $$0, VertexConsumer $$1, ChunkAccess $$2, LevelReader $$3, BlockPos $$4, double $$5, double $$6, double $$7, float $$8, float $$9) {
        BlockPos $$10 = $$4.below();
        BlockState $$11 = $$2.getBlockState($$10);
        if ($$11.getRenderShape() == RenderShape.INVISIBLE || $$3.getMaxLocalRawBrightness($$4) <= 3) {
            return;
        }
        if (!$$11.isCollisionShapeFullBlock($$2, $$10)) {
            return;
        }
        VoxelShape $$12 = $$11.getShape($$2, $$10);
        if ($$12.isEmpty()) {
            return;
        }
        float $$13 = LightTexture.getBrightness($$3.dimensionType(), $$3.getMaxLocalRawBrightness($$4));
        float $$14 = $$9 * 0.5f * $$13;
        if ($$14 >= 0.0f) {
            if ($$14 > 1.0f) {
                $$14 = 1.0f;
            }
            int $$15 = ARGB.color(Mth.floor($$14 * 255.0f), 255, 255, 255);
            AABB $$16 = $$12.bounds();
            double $$17 = (double)$$4.getX() + $$16.minX;
            double $$18 = (double)$$4.getX() + $$16.maxX;
            double $$19 = (double)$$4.getY() + $$16.minY;
            double $$20 = (double)$$4.getZ() + $$16.minZ;
            double $$21 = (double)$$4.getZ() + $$16.maxZ;
            float $$22 = (float)($$17 - $$5);
            float $$23 = (float)($$18 - $$5);
            float $$24 = (float)($$19 - $$6);
            float $$25 = (float)($$20 - $$7);
            float $$26 = (float)($$21 - $$7);
            float $$27 = -$$22 / 2.0f / $$8 + 0.5f;
            float $$28 = -$$23 / 2.0f / $$8 + 0.5f;
            float $$29 = -$$25 / 2.0f / $$8 + 0.5f;
            float $$30 = -$$26 / 2.0f / $$8 + 0.5f;
            EntityRenderDispatcher.shadowVertex($$0, $$1, $$15, $$22, $$24, $$25, $$27, $$29);
            EntityRenderDispatcher.shadowVertex($$0, $$1, $$15, $$22, $$24, $$26, $$27, $$30);
            EntityRenderDispatcher.shadowVertex($$0, $$1, $$15, $$23, $$24, $$26, $$28, $$30);
            EntityRenderDispatcher.shadowVertex($$0, $$1, $$15, $$23, $$24, $$25, $$28, $$29);
        }
    }

    private static void shadowVertex(PoseStack.Pose $$0, VertexConsumer $$1, int $$2, float $$3, float $$4, float $$5, float $$6, float $$7) {
        Vector3f $$8 = $$0.pose().transformPosition($$3, $$4, $$5, new Vector3f());
        $$1.addVertex($$8.x(), $$8.y(), $$8.z(), $$2, $$6, $$7, OverlayTexture.NO_OVERLAY, 0xF000F0, 0.0f, 1.0f, 0.0f);
    }

    public void setLevel(@Nullable Level $$0) {
        this.level = $$0;
        if ($$0 == null) {
            this.camera = null;
        }
    }

    public double distanceToSqr(Entity $$0) {
        return this.camera.getPosition().distanceToSqr($$0.position());
    }

    public double distanceToSqr(double $$0, double $$1, double $$2) {
        return this.camera.getPosition().distanceToSqr($$0, $$1, $$2);
    }

    public Quaternionf cameraOrientation() {
        return this.cameraOrientation;
    }

    public ItemInHandRenderer getItemInHandRenderer() {
        return this.itemInHandRenderer;
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        EntityRendererProvider.Context $$1 = new EntityRendererProvider.Context(this, this.itemModelResolver, this.mapRenderer, this.blockRenderDispatcher, $$0, this.entityModels.get(), this.equipmentAssets, this.font);
        this.renderers = EntityRenderers.createEntityRenderers($$1);
        this.playerRenderers = EntityRenderers.createPlayerRenderers($$1);
    }
}

