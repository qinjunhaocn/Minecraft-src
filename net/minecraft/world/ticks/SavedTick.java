/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.Hash$Strategy
 */
package net.minecraft.world.ticks;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Hash;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public record SavedTick<T>(T type, BlockPos pos, int delay, TickPriority priority) {
    public static final Hash.Strategy<SavedTick<?>> UNIQUE_TICK_HASH = new Hash.Strategy<SavedTick<?>>(){

        public int hashCode(SavedTick<?> $$0) {
            return 31 * $$0.pos().hashCode() + $$0.type().hashCode();
        }

        public boolean equals(@Nullable SavedTick<?> $$0, @Nullable SavedTick<?> $$1) {
            if ($$0 == $$1) {
                return true;
            }
            if ($$0 == null || $$1 == null) {
                return false;
            }
            return $$0.type() == $$1.type() && $$0.pos().equals($$1.pos());
        }

        public /* synthetic */ boolean equals(@Nullable Object object, @Nullable Object object2) {
            return this.equals((SavedTick)((Object)object), (SavedTick)((Object)object2));
        }

        public /* synthetic */ int hashCode(Object object) {
            return this.hashCode((SavedTick)((Object)object));
        }
    };

    public static <T> Codec<SavedTick<T>> codec(Codec<T> $$02) {
        MapCodec $$1 = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)Codec.INT.fieldOf("x").forGetter(Vec3i::getX), (App)Codec.INT.fieldOf("y").forGetter(Vec3i::getY), (App)Codec.INT.fieldOf("z").forGetter(Vec3i::getZ)).apply((Applicative)$$0, BlockPos::new));
        return RecordCodecBuilder.create($$2 -> $$2.group((App)$$02.fieldOf("i").forGetter(SavedTick::type), (App)$$1.forGetter(SavedTick::pos), (App)Codec.INT.fieldOf("t").forGetter(SavedTick::delay), (App)TickPriority.CODEC.fieldOf("p").forGetter(SavedTick::priority)).apply((Applicative)$$2, SavedTick::new));
    }

    public static <T> List<SavedTick<T>> filterTickListForChunk(List<SavedTick<T>> $$0, ChunkPos $$12) {
        long $$2 = $$12.toLong();
        return $$0.stream().filter($$1 -> ChunkPos.asLong($$1.pos()) == $$2).toList();
    }

    public ScheduledTick<T> unpack(long $$0, long $$1) {
        return new ScheduledTick<T>(this.type, this.pos, $$0 + (long)this.delay, this.priority, $$1);
    }

    public static <T> SavedTick<T> probe(T $$0, BlockPos $$1) {
        return new SavedTick<T>($$0, $$1, 0, TickPriority.NORMAL);
    }
}

