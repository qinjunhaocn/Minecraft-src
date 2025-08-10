/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public class NoopRenderer<T extends Entity>
extends EntityRenderer<T, EntityRenderState> {
    public NoopRenderer(EntityRendererProvider.Context $$0) {
        super($$0);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}

