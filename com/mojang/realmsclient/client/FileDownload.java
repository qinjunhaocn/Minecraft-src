/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveInputStream
 *  org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.CountingOutputStream
 */
package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ContentValidationException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;

public class FileDownload {
    static final Logger LOGGER = LogUtils.getLogger();
    volatile boolean cancelled;
    volatile boolean finished;
    volatile boolean error;
    volatile boolean extracting;
    @Nullable
    private volatile File tempFile;
    volatile File resourcePackPath;
    @Nullable
    private volatile HttpGet request;
    @Nullable
    private Thread currentThread;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long contentLength(String $$0) {
        Closeable $$1 = null;
        HttpGet $$2 = null;
        try {
            $$2 = new HttpGet($$0);
            $$1 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            CloseableHttpResponse $$3 = ((CloseableHttpClient)$$1).execute($$2);
            long l = Long.parseLong($$3.getFirstHeader("Content-Length").getValue());
            return l;
        } catch (Throwable $$5) {
            LOGGER.error("Unable to get content length for download");
            long l = 0L;
            return l;
        } finally {
            if ($$2 != null) {
                $$2.releaseConnection();
            }
            if ($$1 != null) {
                try {
                    $$1.close();
                } catch (IOException $$4) {
                    LOGGER.error("Could not close http client", $$4);
                }
            }
        }
    }

    public void download(WorldDownload $$0, String $$1, RealmsDownloadLatestWorldScreen.DownloadStatus $$2, LevelStorageSource $$3) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            Closeable $$4 = null;
            try {
                this.tempFile = File.createTempFile("backup", ".tar.gz");
                this.request = new HttpGet($$0.downloadLink);
                $$4 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                CloseableHttpResponse $$5 = ((CloseableHttpClient)$$4).execute(this.request);
                $$1.totalBytes = Long.parseLong($$5.getFirstHeader("Content-Length").getValue());
                if ($$5.getStatusLine().getStatusCode() != 200) {
                    this.error = true;
                    this.request.abort();
                    return;
                }
                FileOutputStream $$12 = new FileOutputStream(this.tempFile);
                ProgressListener $$13 = new ProgressListener($$1.trim(), this.tempFile, $$3, $$2);
                DownloadCountingOutputStream $$14 = new DownloadCountingOutputStream($$12);
                $$14.setListener($$13);
                IOUtils.copy((InputStream)$$5.getEntity().getContent(), (OutputStream)((Object)$$14));
                return;
            } catch (Exception $$21) {
                LOGGER.error("Caught exception while downloading: {}", (Object)$$21.getMessage());
                this.error = true;
                return;
            } finally {
                block40: {
                    block41: {
                        CloseableHttpResponse $$22;
                        this.request.releaseConnection();
                        if (this.tempFile != null) {
                            this.tempFile.delete();
                        }
                        if (this.error) break block40;
                        if ($$0.resourcePackUrl.isEmpty() || $$0.resourcePackHash.isEmpty()) break block41;
                        try {
                            this.tempFile = File.createTempFile("resources", ".tar.gz");
                            this.request = new HttpGet($$0.resourcePackUrl);
                            $$22 = ((CloseableHttpClient)$$4).execute(this.request);
                            $$1.totalBytes = Long.parseLong($$22.getFirstHeader("Content-Length").getValue());
                            if ($$22.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                this.request.abort();
                                return;
                            }
                        } catch (Exception $$26) {
                            LOGGER.error("Caught exception while downloading: {}", (Object)$$26.getMessage());
                            this.error = true;
                        }
                        FileOutputStream $$23 = new FileOutputStream(this.tempFile);
                        ResourcePackProgressListener $$24 = new ResourcePackProgressListener(this.tempFile, $$2, $$0);
                        DownloadCountingOutputStream $$25 = new DownloadCountingOutputStream($$23);
                        $$25.setListener($$24);
                        IOUtils.copy((InputStream)$$22.getEntity().getContent(), (OutputStream)((Object)$$25));
                        break block40;
                        finally {
                            this.request.releaseConnection();
                            if (this.tempFile != null) {
                                this.tempFile.delete();
                            }
                        }
                    }
                    this.finished = true;
                }
                if ($$4 != null) {
                    try {
                        $$4.close();
                    } catch (IOException $$27) {
                        LOGGER.error("Failed to close Realms download client");
                    }
                }
            }
        });
        this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        this.currentThread.start();
    }

    public void cancel() {
        if (this.request != null) {
            this.request.abort();
        }
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
        this.cancelled = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String $$0) {
        $$0 = ((String)$$0).replaceAll("[\\./\"]", "_");
        for (String $$1 : INVALID_FILE_NAMES) {
            if (!((String)$$0).equalsIgnoreCase($$1)) continue;
            $$0 = "_" + (String)$$0 + "_";
        }
        return $$0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void untarGzipArchive(String $$0, @Nullable File $$1, LevelStorageSource $$2) throws IOException {
        String $$13;
        Pattern $$3 = Pattern.compile(".*-([0-9]+)$");
        int $$4 = 1;
        for (char $$5 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
            $$0 = $$0.replace($$5, '_');
        }
        if (StringUtils.isEmpty($$0)) {
            $$0 = "Realm";
        }
        $$0 = FileDownload.findAvailableFolderName($$0);
        try {
            Object object = $$2.findLevelCandidates().iterator();
            while (object.hasNext()) {
                LevelStorageSource.LevelDirectory $$6 = (LevelStorageSource.LevelDirectory)((Object)object.next());
                String $$7 = $$6.directoryName();
                if (!$$7.toLowerCase(Locale.ROOT).startsWith($$0.toLowerCase(Locale.ROOT))) continue;
                Matcher $$8 = $$3.matcher($$7);
                if ($$8.matches()) {
                    int $$9 = Integer.parseInt($$8.group(1));
                    if ($$9 <= $$4) continue;
                    $$4 = $$9;
                    continue;
                }
                ++$$4;
            }
        } catch (Exception $$10) {
            LOGGER.error("Error getting level list", $$10);
            this.error = true;
            return;
        }
        if (!$$2.isNewLevelIdAcceptable($$0) || $$4 > 1) {
            String $$11 = $$0 + (String)($$4 == 1 ? "" : "-" + $$4);
            if (!$$2.isNewLevelIdAcceptable($$11)) {
                boolean $$12 = false;
                while (!$$12) {
                    if (!$$2.isNewLevelIdAcceptable($$11 = $$0 + (String)(++$$4 == 1 ? "" : "-" + $$4))) continue;
                    $$12 = true;
                }
            }
        } else {
            $$13 = $$0;
        }
        TarArchiveInputStream $$14 = null;
        File $$15 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");
        try {
            $$15.mkdir();
            $$14 = new TarArchiveInputStream((InputStream)new GzipCompressorInputStream((InputStream)new BufferedInputStream(new FileInputStream($$1))));
            TarArchiveEntry $$16 = $$14.getNextTarEntry();
            while ($$16 != null) {
                File $$17 = new File($$15, $$16.getName().replace("world", $$13));
                if ($$16.isDirectory()) {
                    $$17.mkdirs();
                } else {
                    $$17.createNewFile();
                    try (FileOutputStream $$18 = new FileOutputStream($$17);){
                        IOUtils.copy((InputStream)$$14, (OutputStream)$$18);
                    }
                }
                $$16 = $$14.getNextTarEntry();
            }
        } catch (Exception $$22) {
            LOGGER.error("Error extracting world", $$22);
            this.error = true;
        } finally {
            if ($$14 != null) {
                $$14.close();
            }
            if ($$1 != null) {
                $$1.delete();
            }
            try (LevelStorageSource.LevelStorageAccess $$23 = $$2.validateAndCreateAccess($$13);){
                $$23.renameAndDropPlayer($$13);
            } catch (IOException | NbtException | ReportedNbtException $$24) {
                LOGGER.error("Failed to modify unpacked realms level {}", (Object)$$13, (Object)$$24);
            } catch (ContentValidationException $$25) {
                LOGGER.warn("{}", (Object)$$25.getMessage());
            }
            this.resourcePackPath = new File($$15, $$13 + File.separator + "resources.zip");
        }
    }

    class ResourcePackProgressListener
    implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        ResourcePackProgressListener(File $$0, RealmsDownloadLatestWorldScreen.DownloadStatus $$1, WorldDownload $$2) {
            this.tempFile = $$0;
            this.downloadStatus = $$1;
            this.worldDownload = $$2;
        }

        @Override
        public void actionPerformed(ActionEvent $$0) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)$$0.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    String $$1 = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
                    if ($$1.equals(this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile((File)this.tempFile, (File)FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    } else {
                        LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", (Object)this.worldDownload.resourcePackHash, (Object)$$1);
                        FileUtils.deleteQuietly((File)this.tempFile);
                        FileDownload.this.error = true;
                    }
                } catch (IOException $$2) {
                    LOGGER.error("Error copying resourcepack file: {}", (Object)$$2.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }

    static class DownloadCountingOutputStream
    extends CountingOutputStream {
        @Nullable
        private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream $$0) {
            super($$0);
        }

        public void setListener(ActionListener $$0) {
            this.listener = $$0;
        }

        protected void afterWrite(int $$0) throws IOException {
            super.afterWrite($$0);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent((Object)this, 0, null));
            }
        }
    }

    class ProgressListener
    implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final LevelStorageSource levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        ProgressListener(String $$0, File $$1, LevelStorageSource $$2, RealmsDownloadLatestWorldScreen.DownloadStatus $$3) {
            this.worldName = $$0;
            this.tempFile = $$1;
            this.levelStorageSource = $$2;
            this.downloadStatus = $$3;
        }

        @Override
        public void actionPerformed(ActionEvent $$0) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)$$0.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                } catch (IOException $$1) {
                    LOGGER.error("Error extracting archive", $$1);
                    FileDownload.this.error = true;
                }
            }
        }
    }
}

