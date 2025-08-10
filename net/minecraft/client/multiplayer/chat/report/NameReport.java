/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.minecraft.report.ReportedEntity
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.NameReportScreen;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import org.apache.commons.lang3.StringUtils;

public class NameReport
extends Report {
    private final String reportedName;

    NameReport(UUID $$0, Instant $$1, UUID $$2, String $$3) {
        super($$0, $$1, $$2);
        this.reportedName = $$3;
    }

    public String getReportedName() {
        return this.reportedName;
    }

    @Override
    public NameReport copy() {
        NameReport $$0 = new NameReport(this.reportId, this.createdAt, this.reportedProfileId, this.reportedName);
        $$0.comments = this.comments;
        $$0.attested = this.attested;
        return $$0;
    }

    @Override
    public Screen createScreen(Screen $$0, ReportingContext $$1) {
        return new NameReportScreen($$0, $$1, this);
    }

    @Override
    public /* synthetic */ Report copy() {
        return this.copy();
    }

    public static class Builder
    extends Report.Builder<NameReport> {
        public Builder(NameReport $$0, AbuseReportLimits $$1) {
            super($$0, $$1);
        }

        public Builder(UUID $$0, String $$1, AbuseReportLimits $$2) {
            super(new NameReport(UUID.randomUUID(), Instant.now(), $$0, $$1), $$2);
        }

        @Override
        public boolean hasContent() {
            return StringUtils.isNotEmpty(this.comments());
        }

        @Override
        @Nullable
        public Report.CannotBuildReason checkBuildable() {
            if (((NameReport)this.report).comments.length() > this.limits.maxOpinionCommentsLength()) {
                return Report.CannotBuildReason.COMMENT_TOO_LONG;
            }
            return super.checkBuildable();
        }

        @Override
        public Either<Report.Result, Report.CannotBuildReason> build(ReportingContext $$0) {
            Report.CannotBuildReason $$1 = this.checkBuildable();
            if ($$1 != null) {
                return Either.right((Object)((Object)$$1));
            }
            ReportedEntity $$2 = new ReportedEntity(((NameReport)this.report).reportedProfileId);
            AbuseReport $$3 = AbuseReport.name((String)((NameReport)this.report).comments, (ReportedEntity)$$2, (Instant)((NameReport)this.report).createdAt);
            return Either.left((Object)((Object)new Report.Result(((NameReport)this.report).reportId, ReportType.USERNAME, $$3)));
        }
    }
}

