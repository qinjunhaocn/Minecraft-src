/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.blockentity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BlockEntityRendererProvider<T extends BlockEntity> {
    public BlockEntityRenderer<T> create(Context var1);

    public static class Context {
        private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
        private final BlockRenderDispatcher blockRenderDispatcher;
        private final ItemModelResolver itemModelResolver;
        private final ItemRenderer itemRenderer;
        private final EntityRenderDispatcher entityRenderer;
        private final EntityModelSet modelSet;
        private final Font font;

        public Context(BlockEntityRenderDispatcher $$0, BlockRenderDispatcher $$1, ItemModelResolver $$2, ItemRenderer $$3, EntityRenderDispatcher $$4, EntityModelSet $$5, Font $$6) {
            this.blockEntityRenderDispatcher = $$0;
            this.blockRenderDispatcher = $$1;
            this.itemModelResolver = $$2;
            this.itemRenderer = $$3;
            this.entityRenderer = $$4;
            this.modelSet = $$5;
            this.font = $$6;
        }

        public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
            return this.blockEntityRenderDispatcher;
        }

        public BlockRenderDispatcher getBlockRenderDispatcher() {
            return this.blockRenderDispatcher;
        }

        public EntityRenderDispatcher getEntityRenderer() {
            return this.entityRenderer;
        }

        public ItemModelResolver getItemModelResolver() {
            return this.itemModelResolver;
        }

        public ItemRenderer getItemRenderer() {
            return this.itemRenderer;
        }

        public EntityModelSet getModelSet() {
            return this.modelSet;
        }

        public ModelPart bakeLayer(ModelLayerLocation $$0) {
            return this.modelSet.bakeLayer($$0);
        }

        public Font getFont() {
            return this.font;
        }
    }
}

