/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelTimeAccess;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public interface LevelAccessor
extends CommonLevelAccessor,
LevelTimeAccess,
ScheduledTickAccess {
    @Override
    default public long dayTime() {
        return this.getLevelData().getDayTime();
    }

    public long nextSubTickCount();

    @Override
    default public <T> ScheduledTick<T> createTick(BlockPos $$0, T $$1, int $$2, TickPriority $$3) {
        return new ScheduledTick<T>($$1, $$0, this.getLevelData().getGameTime() + (long)$$2, $$3, this.nextSubTickCount());
    }

    @Override
    default public <T> ScheduledTick<T> createTick(BlockPos $$0, T $$1, int $$2) {
        return new ScheduledTick<T>($$1, $$0, this.getLevelData().getGameTime() + (long)$$2, this.nextSubTickCount());
    }

    public LevelData getLevelData();

    public DifficultyInstance getCurrentDifficultyAt(BlockPos var1);

    @Nullable
    public MinecraftServer getServer();

    default public Difficulty getDifficulty() {
        return this.getLevelData().getDifficulty();
    }

    public ChunkSource getChunkSource();

    @Override
    default public boolean hasChunk(int $$0, int $$1) {
        return this.getChunkSource().hasChunk($$0, $$1);
    }

    public RandomSource getRandom();

    default public void updateNeighborsAt(BlockPos $$0, Block $$1) {
    }

    default public void neighborShapeChanged(Direction $$0, BlockPos $$1, BlockPos $$2, BlockState $$3, int $$4, int $$5) {
        NeighborUpdater.executeShapeUpdate(this, $$0, $$1, $$2, $$3, $$4, $$5 - 1);
    }

    default public void playSound(@Nullable Entity $$0, BlockPos $$1, SoundEvent $$2, SoundSource $$3) {
        this.playSound($$0, $$1, $$2, $$3, 1.0f, 1.0f);
    }

    public void playSound(@Nullable Entity var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6);

    public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public void levelEvent(@Nullable Entity var1, int var2, BlockPos var3, int var4);

    default public void levelEvent(int $$0, BlockPos $$1, int $$2) {
        this.levelEvent(null, $$0, $$1, $$2);
    }

    public void gameEvent(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3);

    default public void gameEvent(@Nullable Entity $$0, Holder<GameEvent> $$1, Vec3 $$2) {
        this.gameEvent($$1, $$2, new GameEvent.Context($$0, null));
    }

    default public void gameEvent(@Nullable Entity $$0, Holder<GameEvent> $$1, BlockPos $$2) {
        this.gameEvent($$1, $$2, new GameEvent.Context($$0, null));
    }

    default public void gameEvent(Holder<GameEvent> $$0, BlockPos $$1, GameEvent.Context $$2) {
        this.gameEvent($$0, Vec3.atCenterOf($$1), $$2);
    }

    default public void gameEvent(ResourceKey<GameEvent> $$0, BlockPos $$1, GameEvent.Context $$2) {
        this.gameEvent((Holder<GameEvent>)this.registryAccess().lookupOrThrow(Registries.GAME_EVENT).getOrThrow($$0), $$1, $$2);
    }
}

