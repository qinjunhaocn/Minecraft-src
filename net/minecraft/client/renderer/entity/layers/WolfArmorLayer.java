/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class WolfArmorLayer
extends RenderLayer<WolfRenderState, WolfModel> {
    private final WolfModel adultModel;
    private final WolfModel babyModel;
    private final EquipmentLayerRenderer equipmentRenderer;
    private static final Map<Crackiness.Level, ResourceLocation> ARMOR_CRACK_LOCATIONS = Map.of((Object)((Object)Crackiness.Level.LOW), (Object)ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png"), (Object)((Object)Crackiness.Level.MEDIUM), (Object)ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png"), (Object)((Object)Crackiness.Level.HIGH), (Object)ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png"));

    public WolfArmorLayer(RenderLayerParent<WolfRenderState, WolfModel> $$0, EntityModelSet $$1, EquipmentLayerRenderer $$2) {
        super($$0);
        this.adultModel = new WolfModel($$1.bakeLayer(ModelLayers.WOLF_ARMOR));
        this.babyModel = new WolfModel($$1.bakeLayer(ModelLayers.WOLF_BABY_ARMOR));
        this.equipmentRenderer = $$2;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, WolfRenderState $$3, float $$4, float $$5) {
        ItemStack $$6 = $$3.bodyArmorItem;
        Equippable $$7 = $$6.get(DataComponents.EQUIPPABLE);
        if ($$7 == null || $$7.assetId().isEmpty()) {
            return;
        }
        WolfModel $$8 = $$3.isBaby ? this.babyModel : this.adultModel;
        $$8.setupAnim($$3);
        this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.WOLF_BODY, $$7.assetId().get(), $$8, $$6, $$0, $$1, $$2);
        this.maybeRenderCracks($$0, $$1, $$2, $$6, $$8);
    }

    private void maybeRenderCracks(PoseStack $$0, MultiBufferSource $$1, int $$2, ItemStack $$3, Model $$4) {
        Crackiness.Level $$5 = Crackiness.WOLF_ARMOR.byDamage($$3);
        if ($$5 == Crackiness.Level.NONE) {
            return;
        }
        ResourceLocation $$6 = ARMOR_CRACK_LOCATIONS.get((Object)$$5);
        VertexConsumer $$7 = $$1.getBuffer(RenderType.armorTranslucent($$6));
        $$4.renderToBuffer($$0, $$7, $$2, OverlayTexture.NO_OVERLAY);
    }
}

