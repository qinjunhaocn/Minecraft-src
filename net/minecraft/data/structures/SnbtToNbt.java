/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class SnbtToNbt
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackOutput output;
    private final Iterable<Path> inputFolders;
    private final List<Filter> filters = Lists.newArrayList();

    public SnbtToNbt(PackOutput $$0, Iterable<Path> $$1) {
        this.output = $$0;
        this.inputFolders = $$1;
    }

    public SnbtToNbt addFilter(Filter $$0) {
        this.filters.add($$0);
        return this;
    }

    private CompoundTag applyFilters(String $$0, CompoundTag $$1) {
        CompoundTag $$2 = $$1;
        for (Filter $$3 : this.filters) {
            $$2 = $$3.apply($$0, $$2);
        }
        return $$2;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$02) {
        Path $$1 = this.output.getOutputFolder();
        ArrayList<CompletionStage> $$2 = Lists.newArrayList();
        for (Path $$3 : this.inputFolders) {
            $$2.add(CompletableFuture.supplyAsync(() -> {
                CompletableFuture<Void> completableFuture;
                block8: {
                    Stream<Path> $$32 = Files.walk($$3, new FileVisitOption[0]);
                    try {
                        completableFuture = CompletableFuture.allOf((CompletableFuture[])$$32.filter($$0 -> $$0.toString().endsWith(".snbt")).map($$3 -> CompletableFuture.runAsync(() -> {
                            TaskResult $$4 = this.readStructure((Path)$$3, this.getName($$3, (Path)$$3));
                            this.storeStructureIfChanged($$02, $$4, $$1);
                        }, Util.backgroundExecutor().forName("SnbtToNbt"))).toArray(CompletableFuture[]::new));
                        if ($$32 == null) break block8;
                    } catch (Throwable throwable) {
                        try {
                            if ($$32 != null) {
                                try {
                                    $$32.close();
                                } catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        } catch (Exception $$4) {
                            throw new RuntimeException("Failed to read structure input directory, aborting", $$4);
                        }
                    }
                    $$32.close();
                }
                return completableFuture;
            }, Util.backgroundExecutor().forName("SnbtToNbt")).thenCompose($$0 -> $$0));
        }
        return Util.sequenceFailFast($$2);
    }

    @Override
    public final String getName() {
        return "SNBT -> NBT";
    }

    private String getName(Path $$0, Path $$1) {
        String $$2 = $$0.relativize($$1).toString().replaceAll("\\\\", "/");
        return $$2.substring(0, $$2.length() - ".snbt".length());
    }

    private TaskResult readStructure(Path $$0, String $$1) {
        TaskResult taskResult;
        block8: {
            BufferedReader $$2 = Files.newBufferedReader($$0);
            try {
                String $$3 = IOUtils.toString((Reader)$$2);
                CompoundTag $$4 = this.applyFilters($$1, NbtUtils.snbtToStructure($$3));
                ByteArrayOutputStream $$5 = new ByteArrayOutputStream();
                HashingOutputStream $$6 = new HashingOutputStream(Hashing.sha1(), $$5);
                NbtIo.writeCompressed($$4, $$6);
                byte[] $$7 = $$5.toByteArray();
                HashCode $$8 = $$6.hash();
                taskResult = new TaskResult($$1, $$7, $$8);
                if ($$2 == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if ($$2 != null) {
                        try {
                            $$2.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (Throwable $$9) {
                    throw new StructureConversionException($$0, $$9);
                }
            }
            $$2.close();
        }
        return taskResult;
    }

    private void storeStructureIfChanged(CachedOutput $$0, TaskResult $$1, Path $$2) {
        Path $$3 = $$2.resolve($$1.name + ".nbt");
        try {
            $$0.writeIfNeeded($$3, $$1.payload, $$1.hash);
        } catch (IOException $$4) {
            LOGGER.error("Couldn't write structure {} at {}", $$1.name, $$3, $$4);
        }
    }

    @FunctionalInterface
    public static interface Filter {
        public CompoundTag apply(String var1, CompoundTag var2);
    }

    static final class TaskResult
    extends Record {
        final String name;
        final byte[] payload;
        final HashCode hash;

        TaskResult(String $$0, byte[] $$1, HashCode $$2) {
            this.name = $$0;
            this.payload = $$1;
            this.hash = $$2;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TaskResult.class, "name;payload;hash", "name", "payload", "hash"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TaskResult.class, "name;payload;hash", "name", "payload", "hash"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TaskResult.class, "name;payload;hash", "name", "payload", "hash"}, this, $$0);
        }

        public String name() {
            return this.name;
        }

        public byte[] b() {
            return this.payload;
        }

        public HashCode hash() {
            return this.hash;
        }
    }

    static class StructureConversionException
    extends RuntimeException {
        public StructureConversionException(Path $$0, Throwable $$1) {
            super($$0.toAbsolutePath().toString(), $$1);
        }
    }
}

