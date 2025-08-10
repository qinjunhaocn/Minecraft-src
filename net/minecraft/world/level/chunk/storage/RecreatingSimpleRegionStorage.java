/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  org.apache.commons.io.FileUtils
 */
package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.apache.commons.io.FileUtils;

public class RecreatingSimpleRegionStorage
extends SimpleRegionStorage {
    private final IOWorker writeWorker;
    private final Path writeFolder;

    public RecreatingSimpleRegionStorage(RegionStorageInfo $$0, Path $$1, RegionStorageInfo $$2, Path $$3, DataFixer $$4, boolean $$5, DataFixTypes $$6) {
        super($$0, $$1, $$4, $$5, $$6);
        this.writeFolder = $$3;
        this.writeWorker = new IOWorker($$2, $$3, $$5);
    }

    @Override
    public CompletableFuture<Void> write(ChunkPos $$0, @Nullable CompoundTag $$1) {
        return this.writeWorker.store($$0, $$1);
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.writeWorker.close();
        if (this.writeFolder.toFile().exists()) {
            FileUtils.deleteDirectory((File)this.writeFolder.toFile());
        }
    }
}

