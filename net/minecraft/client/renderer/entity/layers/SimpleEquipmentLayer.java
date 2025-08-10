/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class SimpleEquipmentLayer<S extends LivingEntityRenderState, RM extends EntityModel<? super S>, EM extends EntityModel<? super S>>
extends RenderLayer<S, RM> {
    private final EquipmentLayerRenderer equipmentRenderer;
    private final EquipmentClientInfo.LayerType layer;
    private final Function<S, ItemStack> itemGetter;
    private final EM adultModel;
    private final EM babyModel;

    public SimpleEquipmentLayer(RenderLayerParent<S, RM> $$0, EquipmentLayerRenderer $$1, EquipmentClientInfo.LayerType $$2, Function<S, ItemStack> $$3, EM $$4, EM $$5) {
        super($$0);
        this.equipmentRenderer = $$1;
        this.layer = $$2;
        this.itemGetter = $$3;
        this.adultModel = $$4;
        this.babyModel = $$5;
    }

    public SimpleEquipmentLayer(RenderLayerParent<S, RM> $$0, EquipmentLayerRenderer $$1, EM $$2, EquipmentClientInfo.LayerType $$3, Function<S, ItemStack> $$4) {
        this($$0, $$1, $$3, $$4, $$2, $$2);
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        ItemStack $$6 = this.itemGetter.apply($$3);
        Equippable $$7 = $$6.get(DataComponents.EQUIPPABLE);
        if ($$7 == null || $$7.assetId().isEmpty()) {
            return;
        }
        EM $$8 = ((LivingEntityRenderState)$$3).isBaby ? this.babyModel : this.adultModel;
        ((EntityModel)$$8).setupAnim($$3);
        this.equipmentRenderer.renderLayers(this.layer, $$7.assetId().get(), (Model)$$8, $$6, $$0, $$1, $$2);
    }
}

