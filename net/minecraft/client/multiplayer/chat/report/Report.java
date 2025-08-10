/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.Component;

public abstract class Report {
    protected final UUID reportId;
    protected final Instant createdAt;
    protected final UUID reportedProfileId;
    protected String comments = "";
    @Nullable
    protected ReportReason reason;
    protected boolean attested;

    public Report(UUID $$0, Instant $$1, UUID $$2) {
        this.reportId = $$0;
        this.createdAt = $$1;
        this.reportedProfileId = $$2;
    }

    public boolean isReportedPlayer(UUID $$0) {
        return $$0.equals(this.reportedProfileId);
    }

    public abstract Report copy();

    public abstract Screen createScreen(Screen var1, ReportingContext var2);

    public record CannotBuildReason(Component message) {
        public static final CannotBuildReason NO_REASON = new CannotBuildReason(Component.translatable("gui.abuseReport.send.no_reason"));
        public static final CannotBuildReason NO_REPORTED_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reported_messages"));
        public static final CannotBuildReason TOO_MANY_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.too_many_messages"));
        public static final CannotBuildReason COMMENT_TOO_LONG = new CannotBuildReason(Component.translatable("gui.abuseReport.send.comment_too_long"));
        public static final CannotBuildReason NOT_ATTESTED = new CannotBuildReason(Component.translatable("gui.abuseReport.send.not_attested"));

        public Tooltip tooltip() {
            return Tooltip.create(this.message);
        }
    }

    public record Result(UUID id, ReportType reportType, AbuseReport report) {
    }

    public static abstract class Builder<R extends Report> {
        protected final R report;
        protected final AbuseReportLimits limits;

        protected Builder(R $$0, AbuseReportLimits $$1) {
            this.report = $$0;
            this.limits = $$1;
        }

        public R report() {
            return this.report;
        }

        public UUID reportedProfileId() {
            return ((Report)this.report).reportedProfileId;
        }

        public String comments() {
            return ((Report)this.report).comments;
        }

        public boolean attested() {
            return ((Report)this.report()).attested;
        }

        public void setComments(String $$0) {
            ((Report)this.report).comments = $$0;
        }

        @Nullable
        public ReportReason reason() {
            return ((Report)this.report).reason;
        }

        public void setReason(ReportReason $$0) {
            ((Report)this.report).reason = $$0;
        }

        public void setAttested(boolean $$0) {
            ((Report)this.report).attested = $$0;
        }

        public abstract boolean hasContent();

        @Nullable
        public CannotBuildReason checkBuildable() {
            if (!((Report)this.report()).attested) {
                return CannotBuildReason.NOT_ATTESTED;
            }
            return null;
        }

        public abstract Either<Result, CannotBuildReason> build(ReportingContext var1);
    }
}

