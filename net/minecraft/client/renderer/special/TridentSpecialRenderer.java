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
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class TridentSpecialRenderer
implements NoDataSpecialModelRenderer {
    private final TridentModel model;

    public TridentSpecialRenderer(TridentModel $$0) {
        this.model = $$0;
    }

    @Override
    public void render(ItemDisplayContext $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, boolean $$5) {
        $$1.pushPose();
        $$1.scale(1.0f, -1.0f, -1.0f);
        VertexConsumer $$6 = ItemRenderer.getFoilBuffer($$2, this.model.renderType(TridentModel.TEXTURE), false, $$5);
        this.model.renderToBuffer($$1, $$6, $$3, $$4);
        $$1.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        $$1.scale(1.0f, -1.0f, -1.0f);
        this.model.root().getExtentsForGui($$1, $$0);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit((Object)new Unbaked());

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            return new TridentSpecialRenderer(new TridentModel($$0.bakeLayer(ModelLayers.TRIDENT)));
        }
    }
}

