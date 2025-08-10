/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

public class LlamaDecorLayer
extends RenderLayer<LlamaRenderState, LlamaModel> {
    private final LlamaModel adultModel;
    private final LlamaModel babyModel;
    private final EquipmentLayerRenderer equipmentRenderer;

    public LlamaDecorLayer(RenderLayerParent<LlamaRenderState, LlamaModel> $$0, EntityModelSet $$1, EquipmentLayerRenderer $$2) {
        super($$0);
        this.equipmentRenderer = $$2;
        this.adultModel = new LlamaModel($$1.bakeLayer(ModelLayers.LLAMA_DECOR));
        this.babyModel = new LlamaModel($$1.bakeLayer(ModelLayers.LLAMA_BABY_DECOR));
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, LlamaRenderState $$3, float $$4, float $$5) {
        ItemStack $$6 = $$3.bodyItem;
        Equippable $$7 = $$6.get(DataComponents.EQUIPPABLE);
        if ($$7 != null && $$7.assetId().isPresent()) {
            this.renderEquipment($$0, $$1, $$3, $$6, $$7.assetId().get(), $$2);
        } else if ($$3.isTraderLlama) {
            this.renderEquipment($$0, $$1, $$3, ItemStack.EMPTY, EquipmentAssets.TRADER_LLAMA, $$2);
        }
    }

    private void renderEquipment(PoseStack $$0, MultiBufferSource $$1, LlamaRenderState $$2, ItemStack $$3, ResourceKey<EquipmentAsset> $$4, int $$5) {
        LlamaModel $$6 = $$2.isBaby ? this.babyModel : this.adultModel;
        $$6.setupAnim($$2);
        this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, $$4, $$6, $$3, $$0, $$1, $$5);
    }
}

