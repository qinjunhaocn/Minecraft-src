/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.client.worldupload.RealmsUploadCanceledException;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.User;
import net.minecraft.util.LenientJsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

public class FileUpload {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_RETRIES = 5;
    private static final String UPLOAD_PATH = "/upload";
    private final File file;
    private final long realmId;
    private final int slotId;
    private final UploadInfo uploadInfo;
    private final String sessionId;
    private final String username;
    private final String clientVersion;
    private final String worldVersion;
    private final UploadStatus uploadStatus;
    final AtomicBoolean cancelled = new AtomicBoolean(false);
    @Nullable
    private CompletableFuture<UploadResult> uploadTask;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();

    public FileUpload(File $$0, long $$1, int $$2, UploadInfo $$3, User $$4, String $$5, String $$6, UploadStatus $$7) {
        this.file = $$0;
        this.realmId = $$1;
        this.slotId = $$2;
        this.uploadInfo = $$3;
        this.sessionId = $$4.getSessionId();
        this.username = $$4.getName();
        this.clientVersion = $$5;
        this.worldVersion = $$6;
        this.uploadStatus = $$7;
    }

    public UploadResult upload() {
        if (this.uploadTask != null) {
            return new UploadResult.Builder().build();
        }
        this.uploadTask = CompletableFuture.supplyAsync(() -> this.requestUpload(0), Util.backgroundExecutor());
        if (this.cancelled.get()) {
            this.cancel();
            return new UploadResult.Builder().build();
        }
        return this.uploadTask.join();
    }

    public void cancel() {
        this.cancelled.set(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UploadResult requestUpload(int $$0) {
        UploadResult.Builder $$1;
        block9: {
            $$1 = new UploadResult.Builder();
            if (this.cancelled.get()) {
                return $$1.build();
            }
            this.uploadStatus.setTotalBytes(this.file.length());
            HttpPost $$2 = new HttpPost(this.uploadInfo.getUploadEndpoint().resolve("/upload/" + this.realmId + "/" + this.slotId));
            CloseableHttpClient $$3 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            try {
                this.setupRequest($$2);
                CloseableHttpResponse $$4 = $$3.execute($$2);
                long $$5 = this.getRetryDelaySeconds($$4);
                if (this.shouldRetry($$5, $$0)) {
                    UploadResult uploadResult = this.retryUploadAfter($$5, $$0);
                    return uploadResult;
                }
                this.handleResponse($$4, $$1);
            } catch (Exception $$6) {
                if (!this.cancelled.get()) {
                    LOGGER.error("Caught exception while uploading: ", $$6);
                    break block9;
                }
                throw new RealmsUploadCanceledException();
            } finally {
                this.cleanup($$2, $$3);
            }
        }
        return $$1.build();
    }

    private void cleanup(HttpPost $$0, @Nullable CloseableHttpClient $$1) {
        $$0.releaseConnection();
        if ($$1 != null) {
            try {
                $$1.close();
            } catch (IOException $$2) {
                LOGGER.error("Failed to close Realms upload client");
            }
        }
    }

    private void setupRequest(HttpPost $$0) throws FileNotFoundException {
        $$0.setHeader("Cookie", "sid=" + this.sessionId + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion + ";worldVersion=" + this.worldVersion);
        CustomInputStreamEntity $$1 = new CustomInputStreamEntity(new FileInputStream(this.file), this.file.length(), this.uploadStatus);
        $$1.setContentType("application/octet-stream");
        $$0.setEntity($$1);
    }

    private void handleResponse(HttpResponse $$0, UploadResult.Builder $$1) throws IOException {
        String $$3;
        int $$2 = $$0.getStatusLine().getStatusCode();
        if ($$2 == 401) {
            LOGGER.debug("Realms server returned 401: {}", (Object)$$0.getFirstHeader("WWW-Authenticate"));
        }
        $$1.withStatusCode($$2);
        if ($$0.getEntity() != null && ($$3 = EntityUtils.toString($$0.getEntity(), "UTF-8")) != null) {
            try {
                JsonElement $$4 = LenientJsonParser.parse($$3).getAsJsonObject().get("errorMsg");
                Optional<String> $$5 = Optional.ofNullable($$4).map(JsonElement::getAsString);
                $$1.withErrorMessage($$5.orElse(null));
            } catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private boolean shouldRetry(long $$0, int $$1) {
        return $$0 > 0L && $$1 + 1 < 5;
    }

    private UploadResult retryUploadAfter(long $$0, int $$1) throws InterruptedException {
        Thread.sleep(Duration.ofSeconds($$0).toMillis());
        return this.requestUpload($$1 + 1);
    }

    private long getRetryDelaySeconds(HttpResponse $$0) {
        return Optional.ofNullable($$0.getFirstHeader("Retry-After")).map(NameValuePair::getValue).map(Long::valueOf).orElse(0L);
    }

    public boolean isFinished() {
        return this.uploadTask.isDone() || this.uploadTask.isCancelled();
    }

    class CustomInputStreamEntity
    extends InputStreamEntity {
        private final long length;
        private final InputStream content;
        private final UploadStatus uploadStatus;

        public CustomInputStreamEntity(InputStream $$0, long $$1, UploadStatus $$2) {
            super($$0);
            this.content = $$0;
            this.length = $$1;
            this.uploadStatus = $$2;
        }

        @Override
        public void writeTo(OutputStream $$0) throws IOException {
            block12: {
                Args.notNull($$0, "Output stream");
                try (InputStream $$1 = this.content;){
                    int $$5;
                    byte[] $$2 = new byte[4096];
                    if (this.length < 0L) {
                        int $$3;
                        while (($$3 = $$1.read($$2)) != -1) {
                            if (FileUpload.this.cancelled.get()) {
                                throw new RealmsUploadCanceledException();
                            }
                            $$0.write($$2, 0, $$3);
                            this.uploadStatus.onWrite($$3);
                        }
                        break block12;
                    }
                    for (long $$4 = this.length; $$4 > 0L; $$4 -= (long)$$5) {
                        $$5 = $$1.read($$2, 0, (int)Math.min(4096L, $$4));
                        if ($$5 == -1) {
                            break;
                        }
                        if (FileUpload.this.cancelled.get()) {
                            throw new RealmsUploadCanceledException();
                        }
                        $$0.write($$2, 0, $$5);
                        this.uploadStatus.onWrite($$5);
                        $$0.flush();
                    }
                }
            }
        }
    }
}

