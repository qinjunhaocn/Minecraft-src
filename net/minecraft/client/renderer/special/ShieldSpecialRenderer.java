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
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.joml.Vector3f;

public class ShieldSpecialRenderer
implements SpecialModelRenderer<DataComponentMap> {
    private final ShieldModel model;

    public ShieldSpecialRenderer(ShieldModel $$0) {
        this.model = $$0;
    }

    @Override
    @Nullable
    public DataComponentMap extractArgument(ItemStack $$0) {
        return $$0.immutableComponents();
    }

    @Override
    public void render(@Nullable DataComponentMap $$0, ItemDisplayContext $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, boolean $$6) {
        BannerPatternLayers $$7 = $$0 != null ? $$0.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY) : BannerPatternLayers.EMPTY;
        DyeColor $$8 = $$0 != null ? $$0.get(DataComponents.BASE_COLOR) : null;
        boolean $$9 = !$$7.layers().isEmpty() || $$8 != null;
        $$2.pushPose();
        $$2.scale(1.0f, -1.0f, -1.0f);
        Material $$10 = $$9 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
        VertexConsumer $$11 = $$10.sprite().wrap(ItemRenderer.getFoilBuffer($$3, this.model.renderType($$10.atlasLocation()), $$1 == ItemDisplayContext.GUI, $$6));
        this.model.handle().render($$2, $$11, $$4, $$5);
        if ($$9) {
            BannerRenderer.renderPatterns($$2, $$3, $$4, $$5, this.model.plate(), $$10, false, (DyeColor)Objects.requireNonNullElse((Object)$$8, (Object)DyeColor.WHITE), $$7, $$6, false);
        } else {
            this.model.plate().render($$2, $$11, $$4, $$5);
        }
        $$2.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        $$1.scale(1.0f, -1.0f, -1.0f);
        this.model.root().getExtentsForGui($$1, $$0);
    }

    @Override
    @Nullable
    public /* synthetic */ Object extractArgument(ItemStack itemStack) {
        return this.extractArgument(itemStack);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit((Object)INSTANCE);

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            return new ShieldSpecialRenderer(new ShieldModel($$0.bakeLayer(ModelLayers.SHIELD)));
        }
    }
}

