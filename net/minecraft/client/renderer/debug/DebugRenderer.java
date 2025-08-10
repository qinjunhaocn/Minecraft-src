/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.BreezeDebugRenderer;
import net.minecraft.client.renderer.debug.ChunkBorderRenderer;
import net.minecraft.client.renderer.debug.ChunkCullingDebugRenderer;
import net.minecraft.client.renderer.debug.ChunkDebugRenderer;
import net.minecraft.client.renderer.debug.CollisionBoxRenderer;
import net.minecraft.client.renderer.debug.GameEventListenerRenderer;
import net.minecraft.client.renderer.debug.GameTestDebugRenderer;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.HeightMapRenderer;
import net.minecraft.client.renderer.debug.LightDebugRenderer;
import net.minecraft.client.renderer.debug.LightSectionDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.OctreeDebugRenderer;
import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.client.renderer.debug.RaidDebugRenderer;
import net.minecraft.client.renderer.debug.RedstoneWireOrientationsRenderer;
import net.minecraft.client.renderer.debug.SolidFaceRenderer;
import net.minecraft.client.renderer.debug.StructureRenderer;
import net.minecraft.client.renderer.debug.SupportBlockRenderer;
import net.minecraft.client.renderer.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.renderer.debug.WaterDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionfc;

public class DebugRenderer {
    public final PathfindingRenderer pathfindingRenderer = new PathfindingRenderer();
    public final SimpleDebugRenderer waterDebugRenderer;
    public final SimpleDebugRenderer chunkBorderRenderer;
    public final SimpleDebugRenderer heightMapRenderer;
    public final SimpleDebugRenderer collisionBoxRenderer;
    public final SimpleDebugRenderer supportBlockRenderer;
    public final NeighborsUpdateRenderer neighborsUpdateRenderer;
    public final RedstoneWireOrientationsRenderer redstoneWireOrientationsRenderer;
    public final StructureRenderer structureRenderer;
    public final SimpleDebugRenderer lightDebugRenderer;
    public final SimpleDebugRenderer worldGenAttemptRenderer;
    public final SimpleDebugRenderer solidFaceRenderer;
    public final SimpleDebugRenderer chunkRenderer;
    public final BrainDebugRenderer brainDebugRenderer;
    public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
    public final BeeDebugRenderer beeDebugRenderer;
    public final RaidDebugRenderer raidDebugRenderer;
    public final GoalSelectorDebugRenderer goalSelectorRenderer;
    public final GameTestDebugRenderer gameTestDebugRenderer;
    public final GameEventListenerRenderer gameEventListenerRenderer;
    public final LightSectionDebugRenderer skyLightSectionDebugRenderer;
    public final BreezeDebugRenderer breezeDebugRenderer;
    public final ChunkCullingDebugRenderer chunkCullingDebugRenderer;
    public final OctreeDebugRenderer octreeDebugRenderer;
    private boolean renderChunkborder;
    private boolean renderOctree;

    public DebugRenderer(Minecraft $$0) {
        this.waterDebugRenderer = new WaterDebugRenderer($$0);
        this.chunkBorderRenderer = new ChunkBorderRenderer($$0);
        this.heightMapRenderer = new HeightMapRenderer($$0);
        this.collisionBoxRenderer = new CollisionBoxRenderer($$0);
        this.supportBlockRenderer = new SupportBlockRenderer($$0);
        this.neighborsUpdateRenderer = new NeighborsUpdateRenderer($$0);
        this.redstoneWireOrientationsRenderer = new RedstoneWireOrientationsRenderer($$0);
        this.structureRenderer = new StructureRenderer($$0);
        this.lightDebugRenderer = new LightDebugRenderer($$0);
        this.worldGenAttemptRenderer = new WorldGenAttemptRenderer();
        this.solidFaceRenderer = new SolidFaceRenderer($$0);
        this.chunkRenderer = new ChunkDebugRenderer($$0);
        this.brainDebugRenderer = new BrainDebugRenderer($$0);
        this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
        this.beeDebugRenderer = new BeeDebugRenderer($$0);
        this.raidDebugRenderer = new RaidDebugRenderer($$0);
        this.goalSelectorRenderer = new GoalSelectorDebugRenderer($$0);
        this.gameTestDebugRenderer = new GameTestDebugRenderer();
        this.gameEventListenerRenderer = new GameEventListenerRenderer($$0);
        this.skyLightSectionDebugRenderer = new LightSectionDebugRenderer($$0, LightLayer.SKY);
        this.breezeDebugRenderer = new BreezeDebugRenderer($$0);
        this.chunkCullingDebugRenderer = new ChunkCullingDebugRenderer($$0);
        this.octreeDebugRenderer = new OctreeDebugRenderer($$0);
    }

    public void clear() {
        this.pathfindingRenderer.clear();
        this.waterDebugRenderer.clear();
        this.chunkBorderRenderer.clear();
        this.heightMapRenderer.clear();
        this.collisionBoxRenderer.clear();
        this.supportBlockRenderer.clear();
        this.neighborsUpdateRenderer.clear();
        this.structureRenderer.clear();
        this.lightDebugRenderer.clear();
        this.worldGenAttemptRenderer.clear();
        this.solidFaceRenderer.clear();
        this.chunkRenderer.clear();
        this.brainDebugRenderer.clear();
        this.villageSectionsDebugRenderer.clear();
        this.beeDebugRenderer.clear();
        this.raidDebugRenderer.clear();
        this.goalSelectorRenderer.clear();
        this.gameTestDebugRenderer.clear();
        this.gameEventListenerRenderer.clear();
        this.skyLightSectionDebugRenderer.clear();
        this.breezeDebugRenderer.clear();
        this.chunkCullingDebugRenderer.clear();
    }

    public boolean switchRenderChunkborder() {
        this.renderChunkborder = !this.renderChunkborder;
        return this.renderChunkborder;
    }

    public boolean toggleRenderOctree() {
        this.renderOctree = !this.renderOctree;
        return this.renderOctree;
    }

    public void render(PoseStack $$0, Frustum $$1, MultiBufferSource.BufferSource $$2, double $$3, double $$4, double $$5) {
        if (this.renderChunkborder && !Minecraft.getInstance().showOnlyReducedInfo()) {
            this.chunkBorderRenderer.render($$0, $$2, $$3, $$4, $$5);
        }
        if (this.renderOctree) {
            this.octreeDebugRenderer.render($$0, $$1, $$2, $$3, $$4, $$5);
        }
        this.gameTestDebugRenderer.render($$0, $$2, $$3, $$4, $$5);
    }

    public void renderAfterTranslucents(PoseStack $$0, MultiBufferSource.BufferSource $$1, double $$2, double $$3, double $$4) {
        this.chunkCullingDebugRenderer.render($$0, $$1, $$2, $$3, $$4);
    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity $$0, int $$1) {
        int $$6;
        AABB $$5;
        Vec3 $$3;
        Vec3 $$4;
        if ($$0 == null) {
            return Optional.empty();
        }
        Vec3 $$2 = $$0.getEyePosition();
        EntityHitResult $$7 = ProjectileUtil.getEntityHitResult($$0, $$2, $$4 = $$2.add($$3 = $$0.getViewVector(1.0f).scale($$1)), $$5 = $$0.getBoundingBox().expandTowards($$3).inflate(1.0), EntitySelector.CAN_BE_PICKED, $$6 = $$1 * $$1);
        if ($$7 == null) {
            return Optional.empty();
        }
        if ($$2.distanceToSqr($$7.getLocation()) > (double)$$6) {
            return Optional.empty();
        }
        return Optional.of($$7.getEntity());
    }

    public static void renderFilledUnitCube(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2, float $$3, float $$4, float $$5, float $$6) {
        DebugRenderer.renderFilledBox($$0, $$1, $$2, $$2.offset(1, 1, 1), $$3, $$4, $$5, $$6);
    }

    public static void renderFilledBox(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2, BlockPos $$3, float $$4, float $$5, float $$6, float $$7) {
        Camera $$8 = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!$$8.isInitialized()) {
            return;
        }
        Vec3 $$9 = $$8.getPosition().reverse();
        AABB $$10 = AABB.encapsulatingFullBlocks($$2, $$3).move($$9);
        DebugRenderer.renderFilledBox($$0, $$1, $$10, $$4, $$5, $$6, $$7);
    }

    public static void renderFilledBox(PoseStack $$0, MultiBufferSource $$1, BlockPos $$2, float $$3, float $$4, float $$5, float $$6, float $$7) {
        Camera $$8 = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!$$8.isInitialized()) {
            return;
        }
        Vec3 $$9 = $$8.getPosition().reverse();
        AABB $$10 = new AABB($$2).move($$9).inflate($$3);
        DebugRenderer.renderFilledBox($$0, $$1, $$10, $$4, $$5, $$6, $$7);
    }

    public static void renderFilledBox(PoseStack $$0, MultiBufferSource $$1, AABB $$2, float $$3, float $$4, float $$5, float $$6) {
        DebugRenderer.renderFilledBox($$0, $$1, $$2.minX, $$2.minY, $$2.minZ, $$2.maxX, $$2.maxY, $$2.maxZ, $$3, $$4, $$5, $$6);
    }

    public static void renderFilledBox(PoseStack $$0, MultiBufferSource $$1, double $$2, double $$3, double $$4, double $$5, double $$6, double $$7, float $$8, float $$9, float $$10, float $$11) {
        VertexConsumer $$12 = $$1.getBuffer(RenderType.debugFilledBox());
        ShapeRenderer.addChainedFilledBoxVertices($$0, $$12, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, $$11);
    }

    public static void renderFloatingText(PoseStack $$0, MultiBufferSource $$1, String $$2, int $$3, int $$4, int $$5, int $$6) {
        DebugRenderer.renderFloatingText($$0, $$1, $$2, (double)$$3 + 0.5, (double)$$4 + 0.5, (double)$$5 + 0.5, $$6);
    }

    public static void renderFloatingText(PoseStack $$0, MultiBufferSource $$1, String $$2, double $$3, double $$4, double $$5, int $$6) {
        DebugRenderer.renderFloatingText($$0, $$1, $$2, $$3, $$4, $$5, $$6, 0.02f);
    }

    public static void renderFloatingText(PoseStack $$0, MultiBufferSource $$1, String $$2, double $$3, double $$4, double $$5, int $$6, float $$7) {
        DebugRenderer.renderFloatingText($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, true, 0.0f, false);
    }

    public static void renderFloatingText(PoseStack $$0, MultiBufferSource $$1, String $$2, double $$3, double $$4, double $$5, int $$6, float $$7, boolean $$8, float $$9, boolean $$10) {
        Minecraft $$11 = Minecraft.getInstance();
        Camera $$12 = $$11.gameRenderer.getMainCamera();
        if (!$$12.isInitialized() || $$11.getEntityRenderDispatcher().options == null) {
            return;
        }
        Font $$13 = $$11.font;
        double $$14 = $$12.getPosition().x;
        double $$15 = $$12.getPosition().y;
        double $$16 = $$12.getPosition().z;
        $$0.pushPose();
        $$0.translate((float)($$3 - $$14), (float)($$4 - $$15) + 0.07f, (float)($$5 - $$16));
        $$0.mulPose((Quaternionfc)$$12.rotation());
        $$0.scale($$7, -$$7, $$7);
        float $$17 = $$8 ? (float)(-$$13.width($$2)) / 2.0f : 0.0f;
        $$13.drawInBatch($$2, $$17 -= $$9 / $$7, 0.0f, $$6, false, $$0.last().pose(), $$1, $$10 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, 0xF000F0);
        $$0.popPose();
    }

    private static Vec3 mixColor(float $$0) {
        float $$1 = 5.99999f;
        int $$2 = (int)(Mth.clamp($$0, 0.0f, 1.0f) * 5.99999f);
        float $$3 = $$0 * 5.99999f - (float)$$2;
        return switch ($$2) {
            case 0 -> new Vec3(1.0, $$3, 0.0);
            case 1 -> new Vec3(1.0f - $$3, 1.0, 0.0);
            case 2 -> new Vec3(0.0, 1.0, $$3);
            case 3 -> new Vec3(0.0, 1.0 - (double)$$3, 1.0);
            case 4 -> new Vec3($$3, 0.0, 1.0);
            case 5 -> new Vec3(1.0, 0.0, 1.0 - (double)$$3);
            default -> throw new IllegalStateException("Unexpected value: " + $$2);
        };
    }

    private static Vec3 shiftHue(float $$0, float $$1, float $$2, float $$3) {
        Vec3 $$4 = DebugRenderer.mixColor($$3).scale($$0);
        Vec3 $$5 = DebugRenderer.mixColor(($$3 + 0.33333334f) % 1.0f).scale($$1);
        Vec3 $$6 = DebugRenderer.mixColor(($$3 + 0.6666667f) % 1.0f).scale($$2);
        Vec3 $$7 = $$4.add($$5).add($$6);
        double $$8 = Math.max(Math.max(1.0, $$7.x), Math.max($$7.y, $$7.z));
        return new Vec3($$7.x / $$8, $$7.y / $$8, $$7.z / $$8);
    }

    public static void renderVoxelShape(PoseStack $$0, VertexConsumer $$1, VoxelShape $$2, double $$3, double $$4, double $$5, float $$6, float $$7, float $$8, float $$9, boolean $$10) {
        List<AABB> $$11 = $$2.toAabbs();
        if ($$11.isEmpty()) {
            return;
        }
        int $$12 = $$10 ? $$11.size() : $$11.size() * 8;
        ShapeRenderer.renderShape($$0, $$1, Shapes.create($$11.get(0)), $$3, $$4, $$5, ARGB.colorFromFloat($$9, $$6, $$7, $$8));
        for (int $$13 = 1; $$13 < $$11.size(); ++$$13) {
            AABB $$14 = $$11.get($$13);
            float $$15 = (float)$$13 / (float)$$12;
            Vec3 $$16 = DebugRenderer.shiftHue($$6, $$7, $$8, $$15);
            ShapeRenderer.renderShape($$0, $$1, Shapes.create($$14), $$3, $$4, $$5, ARGB.colorFromFloat($$9, (float)$$16.x, (float)$$16.y, (float)$$16.z));
        }
    }

    public static interface SimpleDebugRenderer {
        public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7);

        default public void clear() {
        }
    }
}

