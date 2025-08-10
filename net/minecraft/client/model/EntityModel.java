/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public abstract class EntityModel<T extends EntityRenderState>
extends Model {
    public static final float MODEL_Y_OFFSET = -1.501f;

    protected EntityModel(ModelPart $$0) {
        this($$0, RenderType::entityCutoutNoCull);
    }

    protected EntityModel(ModelPart $$0, Function<ResourceLocation, RenderType> $$1) {
        super($$0, $$1);
    }

    public void setupAnim(T $$0) {
        this.resetPose();
    }
}

