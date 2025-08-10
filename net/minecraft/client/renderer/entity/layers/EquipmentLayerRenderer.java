/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

public class EquipmentLayerRenderer {
    private static final int NO_LAYER_COLOR = 0;
    private final EquipmentAssetManager equipmentAssets;
    private final Function<LayerTextureKey, ResourceLocation> layerTextureLookup;
    private final Function<TrimSpriteKey, TextureAtlasSprite> trimSpriteLookup;

    public EquipmentLayerRenderer(EquipmentAssetManager $$02, TextureAtlas $$12) {
        this.equipmentAssets = $$02;
        this.layerTextureLookup = Util.memoize($$0 -> $$0.layer.getTextureLocation($$0.layerType));
        this.trimSpriteLookup = Util.memoize($$1 -> $$12.getSprite($$1.spriteId()));
    }

    public void renderLayers(EquipmentClientInfo.LayerType $$0, ResourceKey<EquipmentAsset> $$1, Model $$2, ItemStack $$3, PoseStack $$4, MultiBufferSource $$5, int $$6) {
        this.renderLayers($$0, $$1, $$2, $$3, $$4, $$5, $$6, null);
    }

    public void renderLayers(EquipmentClientInfo.LayerType $$0, ResourceKey<EquipmentAsset> $$1, Model $$2, ItemStack $$3, PoseStack $$4, MultiBufferSource $$5, int $$6, @Nullable ResourceLocation $$7) {
        List<EquipmentClientInfo.Layer> $$8 = this.equipmentAssets.get($$1).getLayers($$0);
        if ($$8.isEmpty()) {
            return;
        }
        int $$9 = DyedItemColor.getOrDefault($$3, 0);
        boolean $$10 = $$3.hasFoil();
        for (EquipmentClientInfo.Layer $$11 : $$8) {
            int $$12 = EquipmentLayerRenderer.getColorForLayer($$11, $$9);
            if ($$12 == 0) continue;
            ResourceLocation $$13 = $$11.usePlayerTexture() && $$7 != null ? $$7 : this.layerTextureLookup.apply(new LayerTextureKey($$0, $$11));
            VertexConsumer $$14 = ItemRenderer.getArmorFoilBuffer($$5, RenderType.armorCutoutNoCull($$13), $$10);
            $$2.renderToBuffer($$4, $$14, $$6, OverlayTexture.NO_OVERLAY, $$12);
            $$10 = false;
        }
        ArmorTrim $$15 = $$3.get(DataComponents.TRIM);
        if ($$15 != null) {
            TextureAtlasSprite $$16 = this.trimSpriteLookup.apply(new TrimSpriteKey($$15, $$0, $$1));
            VertexConsumer $$17 = $$16.wrap($$5.getBuffer(Sheets.armorTrimsSheet($$15.pattern().value().decal())));
            $$2.renderToBuffer($$4, $$17, $$6, OverlayTexture.NO_OVERLAY);
        }
    }

    private static int getColorForLayer(EquipmentClientInfo.Layer $$0, int $$1) {
        Optional<EquipmentClientInfo.Dyeable> $$2 = $$0.dyeable();
        if ($$2.isPresent()) {
            int $$3 = $$2.get().colorWhenUndyed().map(ARGB::opaque).orElse(0);
            return $$1 != 0 ? $$1 : $$3;
        }
        return -1;
    }

    static final class LayerTextureKey
    extends Record {
        final EquipmentClientInfo.LayerType layerType;
        final EquipmentClientInfo.Layer layer;

        LayerTextureKey(EquipmentClientInfo.LayerType $$0, EquipmentClientInfo.Layer $$1) {
            this.layerType = $$0;
            this.layer = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this, $$0);
        }

        public EquipmentClientInfo.LayerType layerType() {
            return this.layerType;
        }

        public EquipmentClientInfo.Layer layer() {
            return this.layer;
        }
    }

    record TrimSpriteKey(ArmorTrim trim, EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> equipmentAssetId) {
        public ResourceLocation spriteId() {
            return this.trim.layerAssetId(this.layerType.trimAssetPrefix(), this.equipmentAssetId);
        }
    }
}

