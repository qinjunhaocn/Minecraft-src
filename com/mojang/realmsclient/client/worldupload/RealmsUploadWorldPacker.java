/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
 */
package com.mojang.realmsclient.client.worldupload;

import com.mojang.realmsclient.client.worldupload.RealmsUploadCanceledException;
import com.mojang.realmsclient.client.worldupload.RealmsUploadTooLargeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class RealmsUploadWorldPacker {
    private static final long SIZE_LIMIT = 0x140000000L;
    private static final String WORLD_FOLDER_NAME = "world";
    private final BooleanSupplier isCanceled;
    private final Path directoryToPack;

    public static File pack(Path $$0, BooleanSupplier $$1) throws IOException {
        return new RealmsUploadWorldPacker($$0, $$1).tarGzipArchive();
    }

    private RealmsUploadWorldPacker(Path $$0, BooleanSupplier $$1) {
        this.isCanceled = $$1;
        this.directoryToPack = $$0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File tarGzipArchive() throws IOException {
        try (TarArchiveOutputStream $$0 = null;){
            File $$1 = File.createTempFile("realms-upload-file", ".tar.gz");
            $$0 = new TarArchiveOutputStream((OutputStream)new GZIPOutputStream(new FileOutputStream($$1)));
            $$0.setLongFileMode(3);
            this.addFileToTarGz($$0, this.directoryToPack, WORLD_FOLDER_NAME, true);
            if (this.isCanceled.getAsBoolean()) {
                throw new RealmsUploadCanceledException();
            }
            $$0.finish();
            this.verifyBelowSizeLimit($$1.length());
            File file = $$1;
            return file;
        }
    }

    private void addFileToTarGz(TarArchiveOutputStream $$0, Path $$1, String $$2, boolean $$3) throws IOException {
        if (this.isCanceled.getAsBoolean()) {
            throw new RealmsUploadCanceledException();
        }
        this.verifyBelowSizeLimit($$0.getBytesWritten());
        File $$4 = $$1.toFile();
        String $$5 = $$3 ? $$2 : $$2 + $$4.getName();
        TarArchiveEntry $$6 = new TarArchiveEntry($$4, $$5);
        $$0.putArchiveEntry($$6);
        if ($$4.isFile()) {
            try (FileInputStream $$7 = new FileInputStream($$4);){
                $$7.transferTo((OutputStream)$$0);
            }
            $$0.closeArchiveEntry();
        } else {
            $$0.closeArchiveEntry();
            File[] $$8 = $$4.listFiles();
            if ($$8 != null) {
                for (File $$9 : $$8) {
                    this.addFileToTarGz($$0, $$9.toPath(), $$5 + "/", false);
                }
            }
        }
    }

    private void verifyBelowSizeLimit(long $$0) {
        if ($$0 > 0x140000000L) {
            throw new RealmsUploadTooLargeException(0x140000000L);
        }
    }
}

