/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.exceptions.MinecraftClientException
 *  com.mojang.authlib.exceptions.MinecraftClientException$ErrorType
 *  com.mojang.authlib.exceptions.MinecraftClientHttpException
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest
 *  com.mojang.datafixers.util.Unit
 *  java.lang.MatchException
 */
package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.datafixers.util.Unit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ThrowingComponent;

public interface AbuseReportSender {
    public static AbuseReportSender create(ReportEnvironment $$0, UserApiService $$1) {
        return new Services($$0, $$1);
    }

    public CompletableFuture<Unit> send(UUID var1, ReportType var2, AbuseReport var3);

    public boolean isEnabled();

    default public AbuseReportLimits reportLimits() {
        return AbuseReportLimits.DEFAULTS;
    }

    public record Services(ReportEnvironment environment, UserApiService userApiService) implements AbuseReportSender
    {
        private static final Component SERVICE_UNAVAILABLE_TEXT = Component.translatable("gui.abuseReport.send.service_unavailable");
        private static final Component HTTP_ERROR_TEXT = Component.translatable("gui.abuseReport.send.http_error");
        private static final Component JSON_ERROR_TEXT = Component.translatable("gui.abuseReport.send.json_error");

        @Override
        public CompletableFuture<Unit> send(UUID $$0, ReportType $$1, AbuseReport $$2) {
            return CompletableFuture.supplyAsync(() -> {
                AbuseReportRequest $$3 = new AbuseReportRequest(1, $$0, $$2, this.environment.clientInfo(), this.environment.thirdPartyServerInfo(), this.environment.realmInfo(), $$1.backendName());
                try {
                    this.userApiService.reportAbuse($$3);
                    return Unit.INSTANCE;
                } catch (MinecraftClientHttpException $$4) {
                    Component $$5 = this.getHttpErrorDescription($$4);
                    throw new CompletionException(new SendException($$5, (Throwable)$$4));
                } catch (MinecraftClientException $$6) {
                    Component $$7 = this.getErrorDescription($$6);
                    throw new CompletionException(new SendException($$7, (Throwable)$$6));
                }
            }, Util.ioPool());
        }

        @Override
        public boolean isEnabled() {
            return this.userApiService.canSendReports();
        }

        private Component getHttpErrorDescription(MinecraftClientHttpException $$0) {
            return Component.a("gui.abuseReport.send.error_message", $$0.getMessage());
        }

        private Component getErrorDescription(MinecraftClientException $$0) {
            return switch ($$0.getType()) {
                default -> throw new MatchException(null, null);
                case MinecraftClientException.ErrorType.SERVICE_UNAVAILABLE -> SERVICE_UNAVAILABLE_TEXT;
                case MinecraftClientException.ErrorType.HTTP_ERROR -> HTTP_ERROR_TEXT;
                case MinecraftClientException.ErrorType.JSON_ERROR -> JSON_ERROR_TEXT;
            };
        }

        @Override
        public AbuseReportLimits reportLimits() {
            return this.userApiService.getAbuseReportLimits();
        }
    }

    public static class SendException
    extends ThrowingComponent {
        public SendException(Component $$0, Throwable $$1) {
            super($$0, $$1);
        }
    }
}

