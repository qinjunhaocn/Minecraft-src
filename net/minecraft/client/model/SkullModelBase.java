/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public abstract class SkullModelBase
extends Model {
    public SkullModelBase(ModelPart $$0) {
        super($$0, RenderType::entityTranslucent);
    }

    public abstract void setupAnim(float var1, float var2, float var3);
}

