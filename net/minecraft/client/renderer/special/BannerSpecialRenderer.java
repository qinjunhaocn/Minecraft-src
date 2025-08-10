/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.joml.Vector3f;

public class BannerSpecialRenderer
implements SpecialModelRenderer<BannerPatternLayers> {
    private final BannerRenderer bannerRenderer;
    private final DyeColor baseColor;

    public BannerSpecialRenderer(DyeColor $$0, BannerRenderer $$1) {
        this.bannerRenderer = $$1;
        this.baseColor = $$0;
    }

    @Override
    @Nullable
    public BannerPatternLayers extractArgument(ItemStack $$0) {
        return $$0.get(DataComponents.BANNER_PATTERNS);
    }

    @Override
    public void render(@Nullable BannerPatternLayers $$0, ItemDisplayContext $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5, boolean $$6) {
        this.bannerRenderer.renderInHand($$2, $$3, $$4, $$5, this.baseColor, (BannerPatternLayers)Objects.requireNonNullElse((Object)$$0, (Object)BannerPatternLayers.EMPTY));
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        this.bannerRenderer.getExtents($$0);
    }

    @Override
    @Nullable
    public /* synthetic */ Object extractArgument(ItemStack itemStack) {
        return this.extractArgument(itemStack);
    }

    public record Unbaked(DyeColor baseColor) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DyeColor.CODEC.fieldOf("color").forGetter(Unbaked::baseColor)).apply((Applicative)$$0, Unbaked::new));

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            return new BannerSpecialRenderer(this.baseColor, new BannerRenderer($$0));
        }
    }
}

