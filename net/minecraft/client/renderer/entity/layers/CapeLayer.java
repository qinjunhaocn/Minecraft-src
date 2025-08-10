/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class CapeLayer
extends RenderLayer<PlayerRenderState, PlayerModel> {
    private final HumanoidModel<PlayerRenderState> model;
    private final EquipmentAssetManager equipmentAssets;

    public CapeLayer(RenderLayerParent<PlayerRenderState, PlayerModel> $$0, EntityModelSet $$1, EquipmentAssetManager $$2) {
        super($$0);
        this.model = new PlayerCapeModel<PlayerRenderState>($$1.bakeLayer(ModelLayers.PLAYER_CAPE));
        this.equipmentAssets = $$2;
    }

    private boolean hasLayer(ItemStack $$0, EquipmentClientInfo.LayerType $$1) {
        Equippable $$2 = $$0.get(DataComponents.EQUIPPABLE);
        if ($$2 == null || $$2.assetId().isEmpty()) {
            return false;
        }
        EquipmentClientInfo $$3 = this.equipmentAssets.get($$2.assetId().get());
        return !$$3.getLayers($$1).isEmpty();
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, PlayerRenderState $$3, float $$4, float $$5) {
        if ($$3.isInvisible || !$$3.showCape) {
            return;
        }
        PlayerSkin $$6 = $$3.skin;
        if ($$6.capeTexture() == null) {
            return;
        }
        if (this.hasLayer($$3.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
            return;
        }
        $$0.pushPose();
        if (this.hasLayer($$3.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
            $$0.translate(0.0f, -0.053125f, 0.06875f);
        }
        VertexConsumer $$7 = $$1.getBuffer(RenderType.entitySolid($$6.capeTexture()));
        ((PlayerModel)this.getParentModel()).copyPropertiesTo(this.model);
        this.model.setupAnim($$3);
        this.model.renderToBuffer($$0, $$7, $$2, OverlayTexture.NO_OVERLAY);
        $$0.popPose();
    }
}

