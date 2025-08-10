/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package net.minecraft.world.level.chunk.storage;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.minecraft.util.FastBufferedInputStream;
import org.slf4j.Logger;

public class RegionFileVersion {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Int2ObjectMap<RegionFileVersion> VERSIONS = new Int2ObjectOpenHashMap();
    private static final Object2ObjectMap<String, RegionFileVersion> VERSIONS_BY_NAME = new Object2ObjectOpenHashMap();
    public static final RegionFileVersion VERSION_GZIP = RegionFileVersion.register(new RegionFileVersion(1, null, $$0 -> new FastBufferedInputStream(new GZIPInputStream((InputStream)$$0)), $$0 -> new BufferedOutputStream(new GZIPOutputStream((OutputStream)$$0))));
    public static final RegionFileVersion VERSION_DEFLATE = RegionFileVersion.register(new RegionFileVersion(2, "deflate", $$0 -> new FastBufferedInputStream(new InflaterInputStream((InputStream)$$0)), $$0 -> new BufferedOutputStream(new DeflaterOutputStream((OutputStream)$$0))));
    public static final RegionFileVersion VERSION_NONE = RegionFileVersion.register(new RegionFileVersion(3, "none", FastBufferedInputStream::new, BufferedOutputStream::new));
    public static final RegionFileVersion VERSION_LZ4 = RegionFileVersion.register(new RegionFileVersion(4, "lz4", $$0 -> new FastBufferedInputStream(new LZ4BlockInputStream((InputStream)$$0)), $$0 -> new BufferedOutputStream(new LZ4BlockOutputStream((OutputStream)$$0))));
    public static final RegionFileVersion VERSION_CUSTOM = RegionFileVersion.register(new RegionFileVersion(127, null, $$0 -> {
        throw new UnsupportedOperationException();
    }, $$0 -> {
        throw new UnsupportedOperationException();
    }));
    public static final RegionFileVersion DEFAULT;
    private static volatile RegionFileVersion selected;
    private final int id;
    @Nullable
    private final String optionName;
    private final StreamWrapper<InputStream> inputWrapper;
    private final StreamWrapper<OutputStream> outputWrapper;

    private RegionFileVersion(int $$0, @Nullable String $$1, StreamWrapper<InputStream> $$2, StreamWrapper<OutputStream> $$3) {
        this.id = $$0;
        this.optionName = $$1;
        this.inputWrapper = $$2;
        this.outputWrapper = $$3;
    }

    private static RegionFileVersion register(RegionFileVersion $$0) {
        VERSIONS.put($$0.id, (Object)$$0);
        if ($$0.optionName != null) {
            VERSIONS_BY_NAME.put((Object)$$0.optionName, (Object)$$0);
        }
        return $$0;
    }

    @Nullable
    public static RegionFileVersion fromId(int $$0) {
        return (RegionFileVersion)VERSIONS.get($$0);
    }

    public static void configure(String $$0) {
        RegionFileVersion $$1 = (RegionFileVersion)VERSIONS_BY_NAME.get((Object)$$0);
        if ($$1 != null) {
            selected = $$1;
        } else {
            LOGGER.error("Invalid `region-file-compression` value `{}` in server.properties. Please use one of: {}", (Object)$$0, (Object)String.join((CharSequence)", ", (Iterable<? extends CharSequence>)VERSIONS_BY_NAME.keySet()));
        }
    }

    public static RegionFileVersion getSelected() {
        return selected;
    }

    public static boolean isValidVersion(int $$0) {
        return VERSIONS.containsKey($$0);
    }

    public int getId() {
        return this.id;
    }

    public OutputStream wrap(OutputStream $$0) throws IOException {
        return this.outputWrapper.wrap($$0);
    }

    public InputStream wrap(InputStream $$0) throws IOException {
        return this.inputWrapper.wrap($$0);
    }

    static {
        selected = DEFAULT = VERSION_DEFLATE;
    }

    @FunctionalInterface
    static interface StreamWrapper<O> {
        public O wrap(O var1) throws IOException;
    }
}

