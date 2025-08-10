/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class HumanoidArmorLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>>
extends RenderLayer<S, M> {
    private final A innerModel;
    private final A outerModel;
    private final A innerModelBaby;
    private final A outerModelBaby;
    private final EquipmentLayerRenderer equipmentRenderer;

    public HumanoidArmorLayer(RenderLayerParent<S, M> $$0, A $$1, A $$2, EquipmentLayerRenderer $$3) {
        this($$0, $$1, $$2, $$1, $$2, $$3);
    }

    public HumanoidArmorLayer(RenderLayerParent<S, M> $$0, A $$1, A $$2, A $$3, A $$4, EquipmentLayerRenderer $$5) {
        super($$0);
        this.innerModel = $$1;
        this.outerModel = $$2;
        this.innerModelBaby = $$3;
        this.outerModelBaby = $$4;
        this.equipmentRenderer = $$5;
    }

    public static boolean shouldRender(ItemStack $$0, EquipmentSlot $$1) {
        Equippable $$2 = $$0.get(DataComponents.EQUIPPABLE);
        return $$2 != null && HumanoidArmorLayer.shouldRender($$2, $$1);
    }

    private static boolean shouldRender(Equippable $$0, EquipmentSlot $$1) {
        return $$0.assetId().isPresent() && $$0.slot() == $$1;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        this.renderArmorPiece($$0, $$1, ((HumanoidRenderState)$$3).chestEquipment, EquipmentSlot.CHEST, $$2, this.getArmorModel($$3, EquipmentSlot.CHEST));
        this.renderArmorPiece($$0, $$1, ((HumanoidRenderState)$$3).legsEquipment, EquipmentSlot.LEGS, $$2, this.getArmorModel($$3, EquipmentSlot.LEGS));
        this.renderArmorPiece($$0, $$1, ((HumanoidRenderState)$$3).feetEquipment, EquipmentSlot.FEET, $$2, this.getArmorModel($$3, EquipmentSlot.FEET));
        this.renderArmorPiece($$0, $$1, ((HumanoidRenderState)$$3).headEquipment, EquipmentSlot.HEAD, $$2, this.getArmorModel($$3, EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack $$0, MultiBufferSource $$1, ItemStack $$2, EquipmentSlot $$3, int $$4, A $$5) {
        Equippable $$6 = $$2.get(DataComponents.EQUIPPABLE);
        if ($$6 == null || !HumanoidArmorLayer.shouldRender($$6, $$3)) {
            return;
        }
        ((HumanoidModel)this.getParentModel()).copyPropertiesTo($$5);
        this.setPartVisibility($$5, $$3);
        EquipmentClientInfo.LayerType $$7 = this.usesInnerModel($$3) ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID;
        this.equipmentRenderer.renderLayers($$7, (ResourceKey)$$6.assetId().orElseThrow(), (Model)$$5, $$2, $$0, $$1, $$4);
    }

    protected void setPartVisibility(A $$0, EquipmentSlot $$1) {
        ((HumanoidModel)$$0).setAllVisible(false);
        switch ($$1) {
            case HEAD: {
                ((HumanoidModel)$$0).head.visible = true;
                ((HumanoidModel)$$0).hat.visible = true;
                break;
            }
            case CHEST: {
                ((HumanoidModel)$$0).body.visible = true;
                ((HumanoidModel)$$0).rightArm.visible = true;
                ((HumanoidModel)$$0).leftArm.visible = true;
                break;
            }
            case LEGS: {
                ((HumanoidModel)$$0).body.visible = true;
                ((HumanoidModel)$$0).rightLeg.visible = true;
                ((HumanoidModel)$$0).leftLeg.visible = true;
                break;
            }
            case FEET: {
                ((HumanoidModel)$$0).rightLeg.visible = true;
                ((HumanoidModel)$$0).leftLeg.visible = true;
            }
        }
    }

    private A getArmorModel(S $$0, EquipmentSlot $$1) {
        if (this.usesInnerModel($$1)) {
            return ((HumanoidRenderState)$$0).isBaby ? this.innerModelBaby : this.innerModel;
        }
        return ((HumanoidRenderState)$$0).isBaby ? this.outerModelBaby : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot $$0) {
        return $$0 == EquipmentSlot.LEGS;
    }
}

