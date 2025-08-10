/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class LivingEntityEmissiveLayer<S extends LivingEntityRenderState, M extends EntityModel<S>>
extends RenderLayer<S, M> {
    private final ResourceLocation texture;
    private final AlphaFunction<S> alphaFunction;
    private final DrawSelector<S, M> drawSelector;
    private final Function<ResourceLocation, RenderType> bufferProvider;
    private final boolean alwaysVisible;

    public LivingEntityEmissiveLayer(RenderLayerParent<S, M> $$0, ResourceLocation $$1, AlphaFunction<S> $$2, DrawSelector<S, M> $$3, Function<ResourceLocation, RenderType> $$4, boolean $$5) {
        super($$0);
        this.texture = $$1;
        this.alphaFunction = $$2;
        this.drawSelector = $$3;
        this.bufferProvider = $$4;
        this.alwaysVisible = $$5;
    }

    @Override
    public void render(PoseStack $$0, MultiBufferSource $$1, int $$2, S $$3, float $$4, float $$5) {
        if (((LivingEntityRenderState)$$3).isInvisible && !this.alwaysVisible) {
            return;
        }
        if (!this.onlyDrawSelectedParts($$3)) {
            return;
        }
        VertexConsumer $$6 = $$1.getBuffer(this.bufferProvider.apply(this.texture));
        float $$7 = this.alphaFunction.apply($$3, ((LivingEntityRenderState)$$3).ageInTicks);
        int $$8 = ARGB.color(Mth.floor($$7 * 255.0f), 255, 255, 255);
        ((Model)this.getParentModel()).renderToBuffer($$0, $$6, $$2, LivingEntityRenderer.getOverlayCoords($$3, 0.0f), $$8);
        this.resetDrawForAllParts();
    }

    private boolean onlyDrawSelectedParts(S $$02) {
        List<ModelPart> $$1 = this.drawSelector.getPartsToDraw(this.getParentModel(), $$02);
        if ($$1.isEmpty()) {
            return false;
        }
        ((Model)this.getParentModel()).allParts().forEach($$0 -> {
            $$0.skipDraw = true;
        });
        $$1.forEach($$0 -> {
            $$0.skipDraw = false;
        });
        return true;
    }

    private void resetDrawForAllParts() {
        ((Model)this.getParentModel()).allParts().forEach($$0 -> {
            $$0.skipDraw = false;
        });
    }

    public static interface AlphaFunction<S extends LivingEntityRenderState> {
        public float apply(S var1, float var2);
    }

    public static interface DrawSelector<S extends LivingEntityRenderState, M extends EntityModel<S>> {
        public List<ModelPart> getPartsToDraw(M var1, S var2);
    }
}

