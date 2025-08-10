/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.block;

import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

public class BlockModelShaper {
    private Map<BlockState, BlockStateModel> modelByStateCache = Map.of();
    private final ModelManager modelManager;

    public BlockModelShaper(ModelManager $$0) {
        this.modelManager = $$0;
    }

    public TextureAtlasSprite getParticleIcon(BlockState $$0) {
        return this.getBlockModel($$0).particleIcon();
    }

    public BlockStateModel getBlockModel(BlockState $$0) {
        BlockStateModel $$1 = this.modelByStateCache.get($$0);
        if ($$1 == null) {
            $$1 = this.modelManager.getMissingBlockStateModel();
        }
        return $$1;
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public void replaceCache(Map<BlockState, BlockStateModel> $$0) {
        this.modelByStateCache = $$0;
    }
}

