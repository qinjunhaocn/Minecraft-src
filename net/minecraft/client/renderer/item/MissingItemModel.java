/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.item;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class MissingItemModel
implements ItemModel {
    private final List<BakedQuad> quads;
    private final Supplier<Vector3f[]> extents;
    private final ModelRenderProperties properties;

    public MissingItemModel(List<BakedQuad> $$0, ModelRenderProperties $$1) {
        this.quads = $$0;
        this.properties = $$1;
        this.extents = Suppliers.memoize(() -> BlockModelWrapper.a(this.quads));
    }

    @Override
    public void update(ItemStackRenderState $$0, ItemStack $$1, ItemModelResolver $$2, ItemDisplayContext $$3, @Nullable ClientLevel $$4, @Nullable LivingEntity $$5, int $$6) {
        $$0.appendModelIdentityElement(this);
        ItemStackRenderState.LayerRenderState $$7 = $$0.newLayer();
        $$7.setRenderType(Sheets.cutoutBlockSheet());
        this.properties.applyToLayer($$7, $$3);
        $$7.setExtents(this.extents);
        $$7.prepareQuadList().addAll(this.quads);
    }
}

