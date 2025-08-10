/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client.renderer.block.model;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RandomSource;

public class SingleVariant
implements BlockStateModel {
    private final BlockModelPart model;

    public SingleVariant(BlockModelPart $$0) {
        this.model = $$0;
    }

    @Override
    public void collectParts(RandomSource $$0, List<BlockModelPart> $$1) {
        $$1.add(this.model);
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return this.model.particleIcon();
    }

    public record Unbaked(Variant variant) implements BlockStateModel.Unbaked
    {
        public static final Codec<Unbaked> CODEC = Variant.CODEC.xmap(Unbaked::new, Unbaked::variant);

        @Override
        public BlockStateModel bake(ModelBaker $$0) {
            return new SingleVariant(this.variant.bake($$0));
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            this.variant.resolveDependencies($$0);
        }
    }
}

