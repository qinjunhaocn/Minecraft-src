/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.slf4j.Logger;

public class DimensionDataStorage
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final SavedData.Context context;
    private final Map<SavedDataType<?>, Optional<SavedData>> cache = new HashMap();
    private final DataFixer fixerUpper;
    private final HolderLookup.Provider registries;
    private final Path dataFolder;
    private CompletableFuture<?> pendingWriteFuture = CompletableFuture.completedFuture(null);

    public DimensionDataStorage(SavedData.Context $$0, Path $$1, DataFixer $$2, HolderLookup.Provider $$3) {
        this.context = $$0;
        this.fixerUpper = $$2;
        this.dataFolder = $$1;
        this.registries = $$3;
    }

    private Path getDataFile(String $$0) {
        return this.dataFolder.resolve($$0 + ".dat");
    }

    public <T extends SavedData> T computeIfAbsent(SavedDataType<T> $$0) {
        T $$1 = this.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        SavedData $$2 = (SavedData)$$0.constructor().apply(this.context);
        this.set($$0, $$2);
        return (T)$$2;
    }

    @Nullable
    public <T extends SavedData> T get(SavedDataType<T> $$0) {
        Optional<SavedData> $$1 = this.cache.get($$0);
        if ($$1 == null) {
            $$1 = Optional.ofNullable(this.readSavedData($$0));
            this.cache.put($$0, $$1);
        }
        return (T)((SavedData)$$1.orElse(null));
    }

    @Nullable
    private <T extends SavedData> T readSavedData(SavedDataType<T> $$0) {
        try {
            Path $$12 = this.getDataFile($$0.id());
            if (Files.exists($$12, new LinkOption[0])) {
                CompoundTag $$2 = this.readTagFromDisk($$0.id(), $$0.dataFixType(), SharedConstants.getCurrentVersion().dataVersion().version());
                RegistryOps<Tag> $$3 = this.registries.createSerializationContext(NbtOps.INSTANCE);
                return (T)((SavedData)$$0.codec().apply(this.context).parse($$3, (Object)$$2.get("data")).resultOrPartial($$1 -> LOGGER.error("Failed to parse saved data for '{}': {}", (Object)$$0, $$1)).orElse(null));
            }
        } catch (Exception $$4) {
            LOGGER.error("Error loading saved data: {}", (Object)$$0, (Object)$$4);
        }
        return null;
    }

    public <T extends SavedData> void set(SavedDataType<T> $$0, T $$1) {
        this.cache.put($$0, Optional.of($$1));
        $$1.setDirty();
    }

    /*
     * WARNING - void declaration
     */
    public CompoundTag readTagFromDisk(String $$0, DataFixTypes $$1, int $$2) throws IOException {
        try (InputStream $$3 = Files.newInputStream(this.getDataFile($$0), new OpenOption[0]);){
            CompoundTag compoundTag;
            try (PushbackInputStream $$4 = new PushbackInputStream(new FastBufferedInputStream($$3), 2);){
                void $$8;
                if (this.isGzip($$4)) {
                    CompoundTag $$5 = NbtIo.readCompressed($$4, NbtAccounter.unlimitedHeap());
                } else {
                    try (DataInputStream $$6 = new DataInputStream($$4);){
                        CompoundTag $$7 = NbtIo.read($$6);
                    }
                }
                int $$9 = NbtUtils.getDataVersion((CompoundTag)$$8, 1343);
                compoundTag = $$1.update(this.fixerUpper, (CompoundTag)$$8, $$9, $$2);
            }
            return compoundTag;
        }
    }

    private boolean isGzip(PushbackInputStream $$0) throws IOException {
        int $$4;
        byte[] $$1 = new byte[2];
        boolean $$2 = false;
        int $$3 = $$0.read($$1, 0, 2);
        if ($$3 == 2 && ($$4 = ($$1[1] & 0xFF) << 8 | $$1[0] & 0xFF) == 35615) {
            $$2 = true;
        }
        if ($$3 != 0) {
            $$0.unread($$1, 0, $$3);
        }
        return $$2;
    }

    public CompletableFuture<?> scheduleSave() {
        Map<SavedDataType<?>, CompoundTag> $$0 = this.collectDirtyTagsToSave();
        if ($$0.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        int $$12 = Util.maxAllowedExecutorThreads();
        int $$2 = $$0.size();
        this.pendingWriteFuture = $$2 > $$12 ? this.pendingWriteFuture.thenCompose($$3 -> {
            ArrayList $$4 = new ArrayList($$12);
            int $$5 = Mth.positiveCeilDiv($$2, $$12);
            for (List $$6 : Iterables.partition($$0.entrySet(), $$5)) {
                $$4.add(CompletableFuture.runAsync(() -> {
                    for (Map.Entry $$1 : $$6) {
                        this.tryWrite((SavedDataType)((Object)((Object)((Object)$$1.getKey()))), (CompoundTag)$$1.getValue());
                    }
                }, Util.ioPool()));
            }
            return CompletableFuture.allOf((CompletableFuture[])$$4.toArray(CompletableFuture[]::new));
        }) : this.pendingWriteFuture.thenCompose($$1 -> CompletableFuture.allOf((CompletableFuture[])$$0.entrySet().stream().map($$0 -> CompletableFuture.runAsync(() -> this.tryWrite((SavedDataType)((Object)((Object)((Object)((Object)$$0.getKey())))), (CompoundTag)$$0.getValue()), Util.ioPool())).toArray(CompletableFuture[]::new)));
        return this.pendingWriteFuture;
    }

    private Map<SavedDataType<?>, CompoundTag> collectDirtyTagsToSave() {
        Object2ObjectArrayMap $$0 = new Object2ObjectArrayMap();
        RegistryOps<Tag> $$1 = this.registries.createSerializationContext(NbtOps.INSTANCE);
        this.cache.forEach((arg_0, arg_1) -> this.lambda$collectDirtyTagsToSave$9((Map)$$0, $$1, arg_0, arg_1));
        return $$0;
    }

    private <T extends SavedData> CompoundTag encodeUnchecked(SavedDataType<T> $$0, SavedData $$1, RegistryOps<Tag> $$2) {
        Codec<T> $$3 = $$0.codec().apply(this.context);
        CompoundTag $$4 = new CompoundTag();
        $$4.put("data", (Tag)$$3.encodeStart($$2, (Object)$$1).getOrThrow());
        NbtUtils.addCurrentDataVersion($$4);
        return $$4;
    }

    private void tryWrite(SavedDataType<?> $$0, CompoundTag $$1) {
        Path $$2 = this.getDataFile($$0.id());
        try {
            NbtIo.writeCompressed($$1, $$2);
        } catch (IOException $$3) {
            LOGGER.error("Could not save data to {}", (Object)$$2.getFileName(), (Object)$$3);
        }
    }

    public void saveAndJoin() {
        this.scheduleSave().join();
    }

    @Override
    public void close() {
        this.saveAndJoin();
    }

    private /* synthetic */ void lambda$collectDirtyTagsToSave$9(Map $$0, RegistryOps $$1, SavedDataType $$2, Optional $$32) {
        $$32.filter(SavedData::isDirty).ifPresent($$3 -> {
            $$0.put($$2, this.encodeUnchecked($$2, (SavedData)$$3, $$1));
            $$3.setDirty(false);
        });
    }
}

