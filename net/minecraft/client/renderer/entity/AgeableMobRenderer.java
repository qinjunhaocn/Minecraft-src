/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Mob;

@Deprecated
public abstract class AgeableMobRenderer<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends MobRenderer<T, S, M> {
    private final M adultModel;
    private final M babyModel;

    public AgeableMobRenderer(EntityRendererProvider.Context $$0, M $$1, M $$2, float $$3) {
        super($$0, $$1, $$3);
        this.adultModel = $$1;
        this.babyModel = $$2;
    }

    @Override
    public void render(S $$0, PoseStack $$1, MultiBufferSource $$2, int $$3) {
        this.model = ((LivingEntityRenderState)$$0).isBaby ? this.babyModel : this.adultModel;
        super.render($$0, $$1, $$2, $$3);
    }
}

