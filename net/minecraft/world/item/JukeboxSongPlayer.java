/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class JukeboxSongPlayer {
    public static final int PLAY_EVENT_INTERVAL_TICKS = 20;
    private long ticksSinceSongStarted;
    @Nullable
    private Holder<JukeboxSong> song;
    private final BlockPos blockPos;
    private final OnSongChanged onSongChanged;

    public JukeboxSongPlayer(OnSongChanged $$0, BlockPos $$1) {
        this.onSongChanged = $$0;
        this.blockPos = $$1;
    }

    public boolean isPlaying() {
        return this.song != null;
    }

    @Nullable
    public JukeboxSong getSong() {
        if (this.song == null) {
            return null;
        }
        return this.song.value();
    }

    public long getTicksSinceSongStarted() {
        return this.ticksSinceSongStarted;
    }

    public void setSongWithoutPlaying(Holder<JukeboxSong> $$0, long $$1) {
        if ($$0.value().hasFinished($$1)) {
            return;
        }
        this.song = $$0;
        this.ticksSinceSongStarted = $$1;
    }

    public void play(LevelAccessor $$0, Holder<JukeboxSong> $$1) {
        this.song = $$1;
        this.ticksSinceSongStarted = 0L;
        int $$2 = $$0.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).getId(this.song.value());
        $$0.levelEvent(null, 1010, this.blockPos, $$2);
        this.onSongChanged.notifyChange();
    }

    public void stop(LevelAccessor $$0, @Nullable BlockState $$1) {
        if (this.song == null) {
            return;
        }
        this.song = null;
        this.ticksSinceSongStarted = 0L;
        $$0.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.blockPos, GameEvent.Context.of($$1));
        $$0.levelEvent(1011, this.blockPos, 0);
        this.onSongChanged.notifyChange();
    }

    public void tick(LevelAccessor $$0, @Nullable BlockState $$1) {
        if (this.song == null) {
            return;
        }
        if (this.song.value().hasFinished(this.ticksSinceSongStarted)) {
            this.stop($$0, $$1);
            return;
        }
        if (this.shouldEmitJukeboxPlayingEvent()) {
            $$0.gameEvent(GameEvent.JUKEBOX_PLAY, this.blockPos, GameEvent.Context.of($$1));
            JukeboxSongPlayer.spawnMusicParticles($$0, this.blockPos);
        }
        ++this.ticksSinceSongStarted;
    }

    private boolean shouldEmitJukeboxPlayingEvent() {
        return this.ticksSinceSongStarted % 20L == 0L;
    }

    private static void spawnMusicParticles(LevelAccessor $$0, BlockPos $$1) {
        if ($$0 instanceof ServerLevel) {
            ServerLevel $$2 = (ServerLevel)$$0;
            Vec3 $$3 = Vec3.atBottomCenterOf($$1).add(0.0, 1.2f, 0.0);
            float $$4 = (float)$$0.getRandom().nextInt(4) / 24.0f;
            $$2.sendParticles(ParticleTypes.NOTE, $$3.x(), $$3.y(), $$3.z(), 0, $$4, 0.0, 0.0, 1.0);
        }
    }

    @FunctionalInterface
    public static interface OnSongChanged {
        public void notifyChange();
    }
}

