/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.joml.Vector3f
 */
package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public class ShulkerBoxSpecialRenderer
implements NoDataSpecialModelRenderer {
    private final ShulkerBoxRenderer shulkerBoxRenderer;
    private final float openness;
    private final Direction orientation;
    private final Material material;

    public ShulkerBoxSpecialRenderer(ShulkerBoxRenderer $$0, float $$1, Direction $$2, Material $$3) {
        this.shulkerBoxRenderer = $$0;
        this.openness = $$1;
        this.orientation = $$2;
        this.material = $$3;
    }

    @Override
    public void render(ItemDisplayContext $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, boolean $$5) {
        this.shulkerBoxRenderer.render($$1, $$2, $$3, $$4, this.orientation, this.openness, this.material);
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        this.shulkerBoxRenderer.getExtents(this.orientation, this.openness, $$0);
    }

    public record Unbaked(ResourceLocation texture, float openness, Direction orientation) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture), (App)Codec.FLOAT.optionalFieldOf("openness", (Object)Float.valueOf(0.0f)).forGetter(Unbaked::openness), (App)Direction.CODEC.optionalFieldOf("orientation", Direction.UP).forGetter(Unbaked::orientation)).apply((Applicative)$$0, Unbaked::new));

        public Unbaked() {
            this(ResourceLocation.withDefaultNamespace("shulker"), 0.0f, Direction.UP);
        }

        public Unbaked(DyeColor $$0) {
            this(Sheets.colorToShulkerMaterial($$0), 0.0f, Direction.UP);
        }

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            return new ShulkerBoxSpecialRenderer(new ShulkerBoxRenderer($$0), this.openness, this.orientation, Sheets.SHULKER_MAPPER.apply(this.texture));
        }
    }
}

