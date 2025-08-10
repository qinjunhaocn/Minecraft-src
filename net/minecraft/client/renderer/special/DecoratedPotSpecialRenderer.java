/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.PotDecorations;
import org.joml.Vector3f;

public class DecoratedPotSpecialRenderer
implements SpecialModelRenderer<PotDecorations> {
    private final DecoratedPotRenderer decoratedPotRenderer;

    public DecoratedPotSpecialRenderer(DecoratedPotRenderer $$0) {
        this.decoratedPotRenderer = $$0;
    }

    @Override
    @Nullable
    public PotDecorations extractArgument(ItemStack $$0) {
        return $$0.get(DataComponents.POT_DECORATIONS);
    }

    @Override
    public void render(@Nullable PotDecorations $$0, ItemDisplayContext $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, boolean $$6) {
        this.decoratedPotRenderer.renderInHand($$2, $$3, $$4, $$5, (PotDecorations)Objects.requireNonNullElse((Object)$$0, (Object)PotDecorations.EMPTY));
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        this.decoratedPotRenderer.getExtents($$0);
    }

    @Override
    @Nullable
    public /* synthetic */ Object extractArgument(ItemStack itemStack) {
        return this.extractArgument(itemStack);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit((Object)new Unbaked());

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            return new DecoratedPotSpecialRenderer(new DecoratedPotRenderer($$0));
        }
    }
}

