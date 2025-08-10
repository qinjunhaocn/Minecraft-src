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
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector3f;

public class StandingSignSpecialRenderer
implements NoDataSpecialModelRenderer {
    private final Model model;
    private final Material material;

    public StandingSignSpecialRenderer(Model $$0, Material $$1) {
        this.model = $$0;
        this.material = $$1;
    }

    @Override
    public void render(ItemDisplayContext $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, boolean $$5) {
        SignRenderer.renderInHand($$1, $$2, $$3, $$4, this.model, this.material);
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        SignRenderer.applyInHandTransforms($$1);
        this.model.root().getExtentsForGui($$1, $$0);
    }

    public record Unbaked(WoodType woodType, Optional<ResourceLocation> texture) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(Unbaked::woodType), (App)ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(Unbaked::texture)).apply((Applicative)$$0, Unbaked::new));

        public Unbaked(WoodType $$0) {
            this($$0, Optional.empty());
        }

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            Model $$1 = SignRenderer.createSignModel($$0, this.woodType, true);
            Material $$2 = this.texture.map(Sheets.SIGN_MAPPER::apply).orElseGet(() -> Sheets.getSignMaterial(this.woodType));
            return new StandingSignSpecialRenderer($$1, $$2);
        }
    }
}

