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
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.SkullBlock;
import org.joml.Vector3f;

public class SkullSpecialRenderer
implements NoDataSpecialModelRenderer {
    private final SkullModelBase model;
    private final float animation;
    private final RenderType renderType;

    public SkullSpecialRenderer(SkullModelBase $$0, float $$1, RenderType $$2) {
        this.model = $$0;
        this.animation = $$1;
        this.renderType = $$2;
    }

    @Override
    public void render(ItemDisplayContext $$0, PoseStack $$1, MultiBufferSource $$2, int $$3, int $$4, boolean $$5) {
        SkullBlockRenderer.renderSkull(null, 180.0f, this.animation, $$1, $$2, $$3, this.model, this.renderType);
    }

    @Override
    public void getExtents(Set<Vector3f> $$0) {
        PoseStack $$1 = new PoseStack();
        $$1.translate(0.5f, 0.0f, 0.5f);
        $$1.scale(-1.0f, -1.0f, 1.0f);
        this.model.setupAnim(this.animation, 180.0f, 0.0f);
        this.model.root().getExtentsForGui($$1, $$0);
    }

    public record Unbaked(SkullBlock.Type kind, Optional<ResourceLocation> textureOverride, float animation) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)SkullBlock.Type.CODEC.fieldOf("kind").forGetter(Unbaked::kind), (App)ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(Unbaked::textureOverride), (App)Codec.FLOAT.optionalFieldOf("animation", (Object)Float.valueOf(0.0f)).forGetter(Unbaked::animation)).apply((Applicative)$$0, Unbaked::new));

        public Unbaked(SkullBlock.Type $$0) {
            this($$0, Optional.empty(), 0.0f);
        }

        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nullable
        public SpecialModelRenderer<?> bake(EntityModelSet $$0) {
            SkullModelBase $$1 = SkullBlockRenderer.createModel($$0, this.kind);
            ResourceLocation $$2 = this.textureOverride.map($$02 -> $$02.withPath($$0 -> "textures/entity/" + $$0 + ".png")).orElse(null);
            if ($$1 == null) {
                return null;
            }
            RenderType $$3 = SkullBlockRenderer.getSkullRenderType(this.kind, $$2);
            return new SkullSpecialRenderer($$1, this.animation, $$3);
        }
    }
}

