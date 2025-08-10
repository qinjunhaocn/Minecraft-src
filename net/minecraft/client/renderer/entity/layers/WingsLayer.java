/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class WingsLayer<S extends HumanoidRenderState, M extends EntityModel<S>>
extends RenderLayer<S, M> {
    private final ElytraModel elytraModel;
    private final ElytraModel elytraBabyModel;
    private final EquipmentLayerRenderer equipmentRenderer;

    public WingsLayer(RenderLayerParent<S, M> $$0, EntityModelSet $$1, EquipmentLayerRenderer $$2) {
        super($$0);
        this.elytraModel = new ElytraModel($$1.bakeLayer(ModelLayers.ELYTRA));
        this.elytraBabyModel = new ElytraModel($$1.bakeLayer(ModelLayers.ELYTRA_BABY));
        this.equipmentRenderer = $$2;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        ItemStack $$6 = ((HumanoidRenderState)$$3).chestEquipment;
        Equippable $$7 = $$6.get(DataComponents.EQUIPPABLE);
        if ($$7 == null || $$7.assetId().isEmpty()) {
            return;
        }
        ResourceLocation $$8 = WingsLayer.getPlayerElytraTexture($$3);
        ElytraModel $$9 = ((HumanoidRenderState)$$3).isBaby ? this.elytraBabyModel : this.elytraModel;
        $$0.pushPose();
        $$0.translate(0.0f, 0.0f, 0.125f);
        $$9.setupAnim((HumanoidRenderState)$$3);
        this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.WINGS, $$7.assetId().get(), $$9, $$6, $$0, $$1, $$2, $$8);
        $$0.popPose();
    }

    @Nullable
    private static ResourceLocation getPlayerElytraTexture(HumanoidRenderState $$0) {
        if ($$0 instanceof PlayerRenderState) {
            PlayerRenderState $$1 = (PlayerRenderState)$$0;
            PlayerSkin $$2 = $$1.skin;
            if ($$2.elytraTexture() != null) {
                return $$2.elytraTexture();
            }
            if ($$2.capeTexture() != null && $$1.showCape) {
                return $$2.capeTexture();
            }
        }
        return null;
    }
}

