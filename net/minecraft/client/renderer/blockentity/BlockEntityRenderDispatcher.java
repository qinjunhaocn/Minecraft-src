/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlockEntityRenderDispatcher
implements ResourceManagerReloadListener {
    private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = ImmutableMap.of();
    private final Font font;
    private final Supplier<EntityModelSet> entityModelSet;
    public Level level;
    public Camera camera;
    public HitResult cameraHitResult;
    private final BlockRenderDispatcher blockRenderDispatcher;
    private final ItemModelResolver itemModelResolver;
    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderer;

    public BlockEntityRenderDispatcher(Font $$0, Supplier<EntityModelSet> $$1, BlockRenderDispatcher $$2, ItemModelResolver $$3, ItemRenderer $$4, EntityRenderDispatcher $$5) {
        this.itemRenderer = $$4;
        this.itemModelResolver = $$3;
        this.entityRenderer = $$5;
        this.font = $$0;
        this.entityModelSet = $$1;
        this.blockRenderDispatcher = $$2;
    }

    @Nullable
    public <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E $$0) {
        return this.renderers.get($$0.getType());
    }

    public void prepare(Level $$0, Camera $$1, HitResult $$2) {
        if (this.level != $$0) {
            this.setLevel($$0);
        }
        this.camera = $$1;
        this.cameraHitResult = $$2;
    }

    public <E extends BlockEntity> void render(E $$0, float $$1, PoseStack $$2, MultiBufferSource $$3) {
        BlockEntityRenderer<E> $$4 = this.getRenderer($$0);
        if ($$4 == null) {
            return;
        }
        if (!$$0.hasLevel() || !$$0.getType().isValid($$0.getBlockState())) {
            return;
        }
        if (!$$4.shouldRender($$0, this.camera.getPosition())) {
            return;
        }
        try {
            BlockEntityRenderDispatcher.setupAndRender($$4, $$0, $$1, $$2, $$3, this.camera.getPosition());
        } catch (Throwable $$5) {
            CrashReport $$6 = CrashReport.forThrowable($$5, "Rendering Block Entity");
            CrashReportCategory $$7 = $$6.addCategory("Block Entity Details");
            $$0.fillCrashReportCategory($$7);
            throw new ReportedException($$6);
        }
    }

    private static <T extends BlockEntity> void setupAndRender(BlockEntityRenderer<T> $$0, T $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, Vec3 $$5) {
        int $$8;
        Level $$6 = $$1.getLevel();
        if ($$6 != null) {
            int $$7 = LevelRenderer.getLightColor($$6, $$1.getBlockPos());
        } else {
            $$8 = 0xF000F0;
        }
        $$0.render($$1, $$2, $$3, $$4, $$8, OverlayTexture.NO_OVERLAY, $$5);
    }

    public void setLevel(@Nullable Level $$0) {
        this.level = $$0;
        if ($$0 == null) {
            this.camera = null;
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        BlockEntityRendererProvider.Context $$1 = new BlockEntityRendererProvider.Context(this, this.blockRenderDispatcher, this.itemModelResolver, this.itemRenderer, this.entityRenderer, this.entityModelSet.get(), this.font);
        this.renderers = BlockEntityRenderers.createEntityRenderers($$1);
    }
}

