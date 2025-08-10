/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.world.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public record LodestoneTracker(Optional<GlobalPos> target, boolean tracked) {
    public static final Codec<LodestoneTracker> CODEC = RecordCodecBuilder.create($$0 -> $$0.group((App)GlobalPos.CODEC.optionalFieldOf("target").forGetter(LodestoneTracker::target), (App)Codec.BOOL.optionalFieldOf("tracked", (Object)true).forGetter(LodestoneTracker::tracked)).apply((Applicative)$$0, LodestoneTracker::new));
    public static final StreamCodec<ByteBuf, LodestoneTracker> STREAM_CODEC = StreamCodec.composite(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), LodestoneTracker::target, ByteBufCodecs.BOOL, LodestoneTracker::tracked, LodestoneTracker::new);

    public LodestoneTracker tick(ServerLevel $$0) {
        if (!this.tracked || this.target.isEmpty()) {
            return this;
        }
        if (this.target.get().dimension() != $$0.dimension()) {
            return this;
        }
        BlockPos $$1 = this.target.get().pos();
        if (!$$0.isInWorldBounds($$1) || !$$0.getPoiManager().existsAtPosition(PoiTypes.LODESTONE, $$1)) {
            return new LodestoneTracker(Optional.empty(), true);
        }
        return this;
    }
}

