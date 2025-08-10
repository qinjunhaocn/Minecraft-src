/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetrySession
 *  com.mojang.authlib.minecraft.UserApiService
 */
package net.minecraft.client.telemetry;

import com.google.common.base.Suppliers;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.telemetry.TelemetryEventInstance;
import net.minecraft.client.telemetry.TelemetryEventLogger;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryLogManager;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;

public class ClientTelemetryManager
implements AutoCloseable {
    private static final AtomicInteger THREAD_COUNT = new AtomicInteger(1);
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor($$0 -> {
        Thread $$1 = new Thread($$0);
        $$1.setName("Telemetry-Sender-#" + THREAD_COUNT.getAndIncrement());
        return $$1;
    });
    private final Minecraft minecraft;
    private final UserApiService userApiService;
    private final TelemetryPropertyMap deviceSessionProperties;
    private final Path logDirectory;
    private final CompletableFuture<Optional<TelemetryLogManager>> logManager;
    private final Supplier<TelemetryEventSender> outsideSessionSender = Suppliers.memoize(this::createEventSender);

    public ClientTelemetryManager(Minecraft $$0, UserApiService $$12, User $$2) {
        this.minecraft = $$0;
        this.userApiService = $$12;
        TelemetryPropertyMap.Builder $$3 = TelemetryPropertyMap.builder();
        $$2.getXuid().ifPresent($$1 -> $$3.put(TelemetryProperty.USER_ID, $$1));
        $$2.getClientId().ifPresent($$1 -> $$3.put(TelemetryProperty.CLIENT_ID, $$1));
        $$3.put(TelemetryProperty.MINECRAFT_SESSION_ID, UUID.randomUUID());
        $$3.put(TelemetryProperty.GAME_VERSION, SharedConstants.getCurrentVersion().id());
        $$3.put(TelemetryProperty.OPERATING_SYSTEM, Util.getPlatform().telemetryName());
        $$3.put(TelemetryProperty.PLATFORM, System.getProperty("os.name"));
        $$3.put(TelemetryProperty.CLIENT_MODDED, Minecraft.checkModStatus().shouldReportAsModified());
        $$3.putIfNotNull(TelemetryProperty.LAUNCHER_NAME, Minecraft.getLauncherBrand());
        this.deviceSessionProperties = $$3.build();
        this.logDirectory = $$0.gameDirectory.toPath().resolve("logs/telemetry");
        this.logManager = TelemetryLogManager.open(this.logDirectory);
    }

    public WorldSessionTelemetryManager createWorldSessionManager(boolean $$0, @Nullable Duration $$1, @Nullable String $$2) {
        return new WorldSessionTelemetryManager(this.createEventSender(), $$0, $$1, $$2);
    }

    public TelemetryEventSender getOutsideSessionSender() {
        return this.outsideSessionSender.get();
    }

    private TelemetryEventSender createEventSender() {
        if (!this.minecraft.allowsTelemetry()) {
            return TelemetryEventSender.DISABLED;
        }
        TelemetrySession $$02 = this.userApiService.newTelemetrySession(EXECUTOR);
        if (!$$02.isEnabled()) {
            return TelemetryEventSender.DISABLED;
        }
        CompletionStage $$1 = this.logManager.thenCompose($$0 -> $$0.map(TelemetryLogManager::openLogger).orElseGet(() -> CompletableFuture.completedFuture(Optional.empty())));
        return (arg_0, arg_1) -> this.lambda$createEventSender$6((CompletableFuture)$$1, $$02, arg_0, arg_1);
    }

    public Path getLogDirectory() {
        return this.logDirectory;
    }

    @Override
    public void close() {
        this.logManager.thenAccept($$0 -> $$0.ifPresent(TelemetryLogManager::close));
    }

    private /* synthetic */ void lambda$createEventSender$6(CompletableFuture $$0, TelemetrySession $$1, TelemetryEventType $$22, Consumer $$3) {
        if ($$22.isOptIn() && !Minecraft.getInstance().telemetryOptInExtra()) {
            return;
        }
        TelemetryPropertyMap.Builder $$4 = TelemetryPropertyMap.builder();
        $$4.putAll(this.deviceSessionProperties);
        $$4.put(TelemetryProperty.EVENT_TIMESTAMP_UTC, Instant.now());
        $$4.put(TelemetryProperty.OPT_IN, $$22.isOptIn());
        $$3.accept($$4);
        TelemetryEventInstance $$5 = new TelemetryEventInstance($$22, $$4.build());
        $$0.thenAccept($$2 -> {
            if ($$2.isEmpty()) {
                return;
            }
            ((TelemetryEventLogger)$$2.get()).log($$5);
            $$5.export($$1).send();
        });
    }
}

