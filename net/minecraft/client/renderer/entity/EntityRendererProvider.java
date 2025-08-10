/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface EntityRendererProvider<T extends Entity> {
    public EntityRenderer<T, ?> create(Context var1);

    public static class Context {
        private final EntityRenderDispatcher entityRenderDispatcher;
        private final ItemModelResolver itemModelResolver;
        private final MapRenderer mapRenderer;
        private final BlockRenderDispatcher blockRenderDispatcher;
        private final ResourceManager resourceManager;
        private final EntityModelSet modelSet;
        private final EquipmentAssetManager equipmentAssets;
        private final Font font;
        private final EquipmentLayerRenderer equipmentRenderer;

        public Context(EntityRenderDispatcher $$0, ItemModelResolver $$1, MapRenderer $$2, BlockRenderDispatcher $$3, ResourceManager $$4, EntityModelSet $$5, EquipmentAssetManager $$6, Font $$7) {
            this.entityRenderDispatcher = $$0;
            this.itemModelResolver = $$1;
            this.mapRenderer = $$2;
            this.blockRenderDispatcher = $$3;
            this.resourceManager = $$4;
            this.modelSet = $$5;
            this.equipmentAssets = $$6;
            this.font = $$7;
            this.equipmentRenderer = new EquipmentLayerRenderer($$6, this.getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET));
        }

        public EntityRenderDispatcher getEntityRenderDispatcher() {
            return this.entityRenderDispatcher;
        }

        public ItemModelResolver getItemModelResolver() {
            return this.itemModelResolver;
        }

        public MapRenderer getMapRenderer() {
            return this.mapRenderer;
        }

        public BlockRenderDispatcher getBlockRenderDispatcher() {
            return this.blockRenderDispatcher;
        }

        public ResourceManager getResourceManager() {
            return this.resourceManager;
        }

        public EntityModelSet getModelSet() {
            return this.modelSet;
        }

        public EquipmentAssetManager getEquipmentAssets() {
            return this.equipmentAssets;
        }

        public EquipmentLayerRenderer getEquipmentRenderer() {
            return this.equipmentRenderer;
        }

        public ModelManager getModelManager() {
            return this.blockRenderDispatcher.getBlockModelShaper().getModelManager();
        }

        public ModelPart bakeLayer(ModelLayerLocation $$0) {
            return this.modelSet.bakeLayer($$0);
        }

        public Font getFont() {
            return this.font;
        }
    }
}

