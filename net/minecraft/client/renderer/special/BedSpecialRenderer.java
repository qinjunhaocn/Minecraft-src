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
import java.util.Set;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class BedSpecialRenderer
implements NoDataSpecialModelRenderer {
    private final BedRenderer bedRenderer;
    private final Material material;

    public BedSpecialRenderer(BedRenderer $$0, Material $$1) {
        this.bedRenderer = $$0;
        this.material = $$1;
    }

    @Override
    public void render(ItemDisplayContext $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, boolean $$5) {
        this.bedRenderer.renderInHand($$1, $$2, $$3, $$4, this.material);
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        this.bedRenderer.getExtents($$0);
    }

    public record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture)).apply((Applicative)$$0, Unbaked::new));

        public Unbaked(DyeColor $$0) {
            this(Sheets.colorToResourceMaterial($$0));
        }

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            return new BedSpecialRenderer(new BedRenderer($$0), Sheets.BED_MAPPER.apply(this.texture));
        }
    }
}

