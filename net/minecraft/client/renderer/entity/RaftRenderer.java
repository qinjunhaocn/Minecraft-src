/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;

public class RaftRenderer
extends AbstractBoatRenderer {
    private final EntityModel<BoatRenderState> model;
    private final ResourceLocation texture;

    public RaftRenderer(EntityRendererProvider.Context $$02, ModelLayerLocation $$1) {
        super($$02);
        this.texture = $$1.model().withPath($$0 -> "textures/entity/" + $$0 + ".png");
        this.model = new RaftModel($$02.bakeLayer($$1));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return this.model.renderType(this.texture);
    }
}

