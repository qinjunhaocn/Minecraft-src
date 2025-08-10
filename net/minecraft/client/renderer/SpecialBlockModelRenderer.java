/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;

public class SpecialBlockModelRenderer {
    public static final SpecialBlockModelRenderer EMPTY = new SpecialBlockModelRenderer(Map.of());
    private final Map<Block, SpecialModelRenderer<?>> renderers;

    public SpecialBlockModelRenderer(Map<Block, SpecialModelRenderer<?>> $$0) {
        this.renderers = $$0;
    }

    public static SpecialBlockModelRenderer vanilla(EntityModelSet $$0) {
        return new SpecialBlockModelRenderer(SpecialModelRenderers.createBlockRenderers($$0));
    }

    public void renderByBlock(Block $$0, ItemDisplayContext $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        SpecialModelRenderer<?> $$6 = this.renderers.get($$0);
        if ($$6 != null) {
            $$6.render(null, $$1, $$2, $$3, $$4, $$5, false);
        }
    }
}

