/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.model;

import java.util.List;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public class WeightedVariants
implements BlockStateModel {
    private final WeightedList<BlockStateModel> list;
    private final TextureAtlasSprite particleIcon;

    public WeightedVariants(WeightedList<BlockStateModel> $$0) {
        this.list = $$0;
        BlockStateModel $$1 = (BlockStateModel)((Weighted)((Object)$$0.unwrap().getFirst())).value();
        this.particleIcon = $$1.particleIcon();
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return this.particleIcon;
    }

    @Override
    public void collectParts(RandomSource $$0, List<BlockModelPart> $$1) {
        this.list.getRandomOrThrow($$0).collectParts($$0, $$1);
    }

    public record Unbaked(WeightedList<BlockStateModel.Unbaked> entries) implements BlockStateModel.Unbaked
    {
        @Override
        public BlockStateModel bake(ModelBaker $$0) {
            return new WeightedVariants(this.entries.map($$1 -> $$1.bake($$0)));
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver $$0) {
            this.entries.unwrap().forEach($$1 -> ((BlockStateModel.Unbaked)$$1.value()).resolveDependencies($$0));
        }
    }
}

