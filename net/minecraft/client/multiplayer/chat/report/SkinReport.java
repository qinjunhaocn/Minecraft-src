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
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.SkinReportScreen;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.resources.PlayerSkin;
import org.apache.commons.lang3.StringUtils;

public class SkinReport
extends Report {
    final Supplier<PlayerSkin> skinGetter;

    SkinReport(UUID $$0, Instant $$1, UUID $$2, Supplier<PlayerSkin> $$3) {
        super($$0, $$1, $$2);
        this.skinGetter = $$3;
    }

    public Supplier<PlayerSkin> getSkinGetter() {
        return this.skinGetter;
    }

    @Override
    public SkinReport copy() {
        SkinReport $$0 = new SkinReport(this.reportId, this.createdAt, this.reportedProfileId, this.skinGetter);
        $$0.comments = this.comments;
        $$0.reason = this.reason;
        $$0.attested = this.attested;
        return $$0;
    }

    @Override
    public Screen createScreen(Screen $$0, ReportingContext $$1) {
        return new SkinReportScreen($$0, $$1, this);
    }

    @Override
    public /* synthetic */ Report copy() {
        return this.copy();
    }

    public static class Builder
    extends Report.Builder<SkinReport> {
        public Builder(SkinReport $$0, AbuseReportLimits $$1) {
            super($$0, $$1);
        }

        public Builder(UUID $$0, Supplier<PlayerSkin> $$1, AbuseReportLimits $$2) {
            super(new SkinReport(UUID.randomUUID(), Instant.now(), $$0, $$1), $$2);
        }

        @Override
        public boolean hasContent() {
            return StringUtils.isNotEmpty(this.comments()) || this.reason() != null;
        }

        @Override
        @Nullable
        public Report.CannotBuildReason checkBuildable() {
            if (((SkinReport)this.report).reason == null) {
                return Report.CannotBuildReason.NO_REASON;
            }
            if (((SkinReport)this.report).comments.length() > this.limits.maxOpinionCommentsLength()) {
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
            String $$2 = Objects.requireNonNull(((SkinReport)this.report).reason).backendName();
            ReportedEntity $$3 = new ReportedEntity(((SkinReport)this.report).reportedProfileId);
            PlayerSkin $$4 = ((SkinReport)this.report).skinGetter.get();
            String $$5 = $$4.textureUrl();
            AbuseReport $$6 = AbuseReport.skin((String)((SkinReport)this.report).comments, (String)$$2, (String)$$5, (ReportedEntity)$$3, (Instant)((SkinReport)this.report).createdAt);
            return Either.left((Object)((Object)new Report.Result(((SkinReport)this.report).reportId, ReportType.SKIN, $$6)));
        }
    }
}

