/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.level.gameevent;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;

public class BlockPositionSource
implements PositionSource {
    public static final MapCodec<BlockPositionSource> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BlockPos.CODEC.fieldOf("pos").forGetter($$0 -> $$0.pos)).apply((Applicative)$$02, BlockPositionSource::new));
    public static final StreamCodec<ByteBuf, BlockPositionSource> STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, $$0 -> $$0.pos, BlockPositionSource::new);
    private final BlockPos pos;

    public BlockPositionSource(BlockPos $$0) {
        this.pos = $$0;
    }

    @Override
    public Optional<Vec3> getPosition(Level $$0) {
        return Optional.of(Vec3.atCenterOf(this.pos));
    }

    public PositionSourceType<BlockPositionSource> getType() {
        return PositionSourceType.BLOCK;
    }

    public static class Type
    implements PositionSourceType<BlockPositionSource> {
        @Override
        public MapCodec<BlockPositionSource> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<ByteBuf, BlockPositionSource> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

