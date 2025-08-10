/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BlockStateDefinitions {
    private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = BlockStateDefinitions.createItemFrameFakeState();
    private static final StateDefinition<Block, BlockState> GLOW_ITEM_FRAME_FAKE_DEFINITION = BlockStateDefinitions.createItemFrameFakeState();
    private static final ResourceLocation GLOW_ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("glow_item_frame");
    private static final ResourceLocation ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("item_frame");
    private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = Map.of((Object)ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, (Object)GLOW_ITEM_FRAME_LOCATION, GLOW_ITEM_FRAME_FAKE_DEFINITION);

    private static StateDefinition<Block, BlockState> createItemFrameFakeState() {
        return new StateDefinition.Builder(Blocks.AIR).a(BlockStateProperties.MAP).create(Block::defaultBlockState, BlockState::new);
    }

    public static BlockState getItemFrameFakeState(boolean $$0, boolean $$1) {
        return (BlockState)($$0 ? GLOW_ITEM_FRAME_FAKE_DEFINITION : ITEM_FRAME_FAKE_DEFINITION).any().setValue(BlockStateProperties.MAP, $$1);
    }

    static Function<ResourceLocation, StateDefinition<Block, BlockState>> definitionLocationToBlockStateMapper() {
        HashMap<ResourceLocation, StateDefinition<Block, BlockState>> $$0 = new HashMap<ResourceLocation, StateDefinition<Block, BlockState>>(STATIC_DEFINITIONS);
        for (Block $$1 : BuiltInRegistries.BLOCK) {
            $$0.put($$1.builtInRegistryHolder().key().location(), $$1.getStateDefinition());
        }
        return $$0::get;
    }
}

