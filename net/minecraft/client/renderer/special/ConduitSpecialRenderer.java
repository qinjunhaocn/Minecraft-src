/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class ConduitSpecialRenderer
implements NoDataSpecialModelRenderer {
    private final ModelPart model;

    public ConduitSpecialRenderer(ModelPart $$0) {
        this.model = $$0;
    }

    @Override
    public void render(ItemDisplayContext $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, boolean $$5) {
        VertexConsumer $$6 = ConduitRenderer.SHELL_TEXTURE.buffer($$2, RenderType::entitySolid);
        $$1.pushPose();
        $$1.translate(0.5f, 0.5f, 0.5f);
        this.model.render($$1, $$6, $$3, $$4);
        $$1.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        $$1.translate(0.5f, 0.5f, 0.5f);
        this.model.getExtentsForGui($$1, $$0);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit((Object)new Unbaked());

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            return new ConduitSpecialRenderer($$0.bakeLayer(ModelLayers.CONDUIT_SHELL));
        }
    }
}

