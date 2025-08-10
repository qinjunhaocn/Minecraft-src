/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d.platform;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.lang3.ArrayUtils;

public final class IconSet
extends Enum<IconSet> {
    public static final /* enum */ IconSet RELEASE = new IconSet("icons");
    public static final /* enum */ IconSet SNAPSHOT = new IconSet("icons", "snapshot");
    private final String[] path;
    private static final /* synthetic */ IconSet[] $VALUES;

    public static IconSet[] values() {
        return (IconSet[])$VALUES.clone();
    }

    public static IconSet valueOf(String $$0) {
        return Enum.valueOf(IconSet.class, $$0);
    }

    private IconSet(String ... $$0) {
        this.path = $$0;
    }

    public List<IoSupplier<InputStream>> getStandardIcons(PackResources $$0) throws IOException {
        return List.of(this.getFile($$0, "icon_16x16.png"), this.getFile($$0, "icon_32x32.png"), this.getFile($$0, "icon_48x48.png"), this.getFile($$0, "icon_128x128.png"), this.getFile($$0, "icon_256x256.png"));
    }

    public IoSupplier<InputStream> getMacIcon(PackResources $$0) throws IOException {
        return this.getFile($$0, "minecraft.icns");
    }

    private IoSupplier<InputStream> getFile(PackResources $$0, String $$1) throws IOException {
        CharSequence[] $$2 = ArrayUtils.add(this.path, $$1);
        IoSupplier<InputStream> $$3 = $$0.a((String[])$$2);
        if ($$3 == null) {
            throw new FileNotFoundException(String.join((CharSequence)"/", $$2));
        }
        return $$3;
    }

    private static /* synthetic */ IconSet[] a() {
        return new IconSet[]{RELEASE, SNAPSHOT};
    }

    static {
        $VALUES = IconSet.a();
    }
}

