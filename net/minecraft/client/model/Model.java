/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class Model {
    protected final ModelPart root;
    protected final Function<ResourceLocation, RenderType> renderType;
    private final List<ModelPart> allParts;

    public Model(ModelPart $$0, Function<ResourceLocation, RenderType> $$1) {
        this.root = $$0;
        this.renderType = $$1;
        this.allParts = $$0.getAllParts();
    }

    public final RenderType renderType(ResourceLocation $$0) {
        return this.renderType.apply($$0);
    }

    public final void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3, int $$4) {
        this.root().render($$0, $$1, $$2, $$3, $$4);
    }

    public final void renderToBuffer(PoseStack $$0, VertexConsumer $$1, int $$2, int $$3) {
        this.renderToBuffer($$0, $$1, $$2, $$3, -1);
    }

    public final ModelPart root() {
        return this.root;
    }

    public final List<ModelPart> allParts() {
        return this.allParts;
    }

    public final void resetPose() {
        for (ModelPart $$0 : this.allParts) {
            $$0.resetPose();
        }
    }

    public static class Simple
    extends Model {
        public Simple(ModelPart $$0, Function<ResourceLocation, RenderType> $$1) {
            super($$0, $$1);
        }
    }
}

