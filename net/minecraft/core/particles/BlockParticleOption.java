/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption
implements ParticleOptions {
    private static final Codec<BlockState> BLOCK_STATE_CODEC = Codec.withAlternative(BlockState.CODEC, BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState);
    private final ParticleType<BlockParticleOption> type;
    private final BlockState state;

    public static MapCodec<BlockParticleOption> codec(ParticleType<BlockParticleOption> $$02) {
        return BLOCK_STATE_CODEC.xmap($$1 -> new BlockParticleOption($$02, (BlockState)$$1), $$0 -> $$0.state).fieldOf("block_state");
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOption> streamCodec(ParticleType<BlockParticleOption> $$02) {
        return ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY).map($$1 -> new BlockParticleOption($$02, (BlockState)$$1), $$0 -> $$0.state);
    }

    public BlockParticleOption(ParticleType<BlockParticleOption> $$0, BlockState $$1) {
        this.type = $$0;
        this.state = $$1;
    }

    public ParticleType<BlockParticleOption> getType() {
        return this.type;
    }

    public BlockState getState() {
        return this.state;
    }
}

