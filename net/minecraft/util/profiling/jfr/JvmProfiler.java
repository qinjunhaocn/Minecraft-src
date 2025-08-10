/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JfrProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;

public interface JvmProfiler {
    public static final JvmProfiler INSTANCE = Runtime.class.getModule().getLayer().findModule("jdk.jfr").isPresent() ? JfrProfiler.getInstance() : new NoOpProfiler();

    public boolean start(Environment var1);

    public Path stop();

    public boolean isRunning();

    public boolean isAvailable();

    public void onServerTick(float var1);

    public void onPacketReceived(ConnectionProtocol var1, PacketType<?> var2, SocketAddress var3, int var4);

    public void onPacketSent(ConnectionProtocol var1, PacketType<?> var2, SocketAddress var3, int var4);

    public void onRegionFileRead(RegionStorageInfo var1, ChunkPos var2, RegionFileVersion var3, int var4);

    public void onRegionFileWrite(RegionStorageInfo var1, ChunkPos var2, RegionFileVersion var3, int var4);

    @Nullable
    public ProfiledDuration onWorldLoadedStarted();

    @Nullable
    public ProfiledDuration onChunkGenerate(ChunkPos var1, ResourceKey<Level> var2, String var3);

    @Nullable
    public ProfiledDuration onStructureGenerate(ChunkPos var1, ResourceKey<Level> var2, Holder<Structure> var3);

    public static class NoOpProfiler
    implements JvmProfiler {
        private static final Logger LOGGER = LogUtils.getLogger();
        static final ProfiledDuration noOpCommit = $$0 -> {};

        @Override
        public boolean start(Environment $$0) {
            LOGGER.warn("Attempted to start Flight Recorder, but it's not supported on this JVM");
            return false;
        }

        @Override
        public Path stop() {
            throw new IllegalStateException("Attempted to stop Flight Recorder, but it's not supported on this JVM");
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public void onPacketReceived(ConnectionProtocol $$0, PacketType<?> $$1, SocketAddress $$2, int $$3) {
        }

        @Override
        public void onPacketSent(ConnectionProtocol $$0, PacketType<?> $$1, SocketAddress $$2, int $$3) {
        }

        @Override
        public void onRegionFileRead(RegionStorageInfo $$0, ChunkPos $$1, RegionFileVersion $$2, int $$3) {
        }

        @Override
        public void onRegionFileWrite(RegionStorageInfo $$0, ChunkPos $$1, RegionFileVersion $$2, int $$3) {
        }

        @Override
        public void onServerTick(float $$0) {
        }

        @Override
        public ProfiledDuration onWorldLoadedStarted() {
            return noOpCommit;
        }

        @Override
        @Nullable
        public ProfiledDuration onChunkGenerate(ChunkPos $$0, ResourceKey<Level> $$1, String $$2) {
            return null;
        }

        @Override
        public ProfiledDuration onStructureGenerate(ChunkPos $$0, ResourceKey<Level> $$1, Holder<Structure> $$2) {
            return noOpCommit;
        }
    }
}

