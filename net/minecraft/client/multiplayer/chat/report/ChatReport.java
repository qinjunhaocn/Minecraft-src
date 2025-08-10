/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.minecraft.report.ReportChatMessage
 *  com.mojang.authlib.minecraft.report.ReportEvidence
 *  com.mojang.authlib.minecraft.report.ReportedEntity
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package net.minecraft.client.multiplayer.chat.report;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.ChatReportScreen;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.report.ChatReportContextBuilder;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageLink;
import org.apache.commons.lang3.StringUtils;

public class ChatReport
extends Report {
    final IntSet reportedMessages = new IntOpenHashSet();

    ChatReport(UUID $$0, Instant $$1, UUID $$2) {
        super($$0, $$1, $$2);
    }

    public void toggleReported(int $$0, AbuseReportLimits $$1) {
        if (this.reportedMessages.contains($$0)) {
            this.reportedMessages.remove($$0);
        } else if (this.reportedMessages.size() < $$1.maxReportedMessageCount()) {
            this.reportedMessages.add($$0);
        }
    }

    @Override
    public ChatReport copy() {
        ChatReport $$0 = new ChatReport(this.reportId, this.createdAt, this.reportedProfileId);
        $$0.reportedMessages.addAll((IntCollection)this.reportedMessages);
        $$0.comments = this.comments;
        $$0.reason = this.reason;
        $$0.attested = this.attested;
        return $$0;
    }

    @Override
    public Screen createScreen(Screen $$0, ReportingContext $$1) {
        return new ChatReportScreen($$0, $$1, this);
    }

    @Override
    public /* synthetic */ Report copy() {
        return this.copy();
    }

    public static class Builder
    extends Report.Builder<ChatReport> {
        public Builder(ChatReport $$0, AbuseReportLimits $$1) {
            super($$0, $$1);
        }

        public Builder(UUID $$0, AbuseReportLimits $$1) {
            super(new ChatReport(UUID.randomUUID(), Instant.now(), $$0), $$1);
        }

        public IntSet reportedMessages() {
            return ((ChatReport)this.report).reportedMessages;
        }

        public void toggleReported(int $$0) {
            ((ChatReport)this.report).toggleReported($$0, this.limits);
        }

        public boolean isReported(int $$0) {
            return ((ChatReport)this.report).reportedMessages.contains($$0);
        }

        @Override
        public boolean hasContent() {
            return StringUtils.isNotEmpty(this.comments()) || !this.reportedMessages().isEmpty() || this.reason() != null;
        }

        @Override
        @Nullable
        public Report.CannotBuildReason checkBuildable() {
            if (((ChatReport)this.report).reportedMessages.isEmpty()) {
                return Report.CannotBuildReason.NO_REPORTED_MESSAGES;
            }
            if (((ChatReport)this.report).reportedMessages.size() > this.limits.maxReportedMessageCount()) {
                return Report.CannotBuildReason.TOO_MANY_MESSAGES;
            }
            if (((ChatReport)this.report).reason == null) {
                return Report.CannotBuildReason.NO_REASON;
            }
            if (((ChatReport)this.report).comments.length() > this.limits.maxOpinionCommentsLength()) {
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
            String $$2 = Objects.requireNonNull(((ChatReport)this.report).reason).backendName();
            ReportEvidence $$3 = this.buildEvidence($$0);
            ReportedEntity $$4 = new ReportedEntity(((ChatReport)this.report).reportedProfileId);
            AbuseReport $$5 = AbuseReport.chat((String)((ChatReport)this.report).comments, (String)$$2, (ReportEvidence)$$3, (ReportedEntity)$$4, (Instant)((ChatReport)this.report).createdAt);
            return Either.left((Object)((Object)new Report.Result(((ChatReport)this.report).reportId, ReportType.CHAT, $$5)));
        }

        private ReportEvidence buildEvidence(ReportingContext $$0) {
            ArrayList $$12 = new ArrayList();
            ChatReportContextBuilder $$22 = new ChatReportContextBuilder(this.limits.leadingContextMessageCount());
            $$22.collectAllContext($$0.chatLog(), (IntCollection)((ChatReport)this.report).reportedMessages, ($$1, $$2) -> $$12.add(this.buildReportedChatMessage($$2, this.isReported($$1))));
            return new ReportEvidence(Lists.reverse($$12));
        }

        private ReportChatMessage buildReportedChatMessage(LoggedChatMessage.Player $$0, boolean $$1) {
            SignedMessageLink $$2 = $$0.message().link();
            SignedMessageBody $$3 = $$0.message().signedBody();
            List $$4 = $$3.lastSeen().entries().stream().map(MessageSignature::asByteBuffer).toList();
            ByteBuffer $$5 = Optionull.map($$0.message().signature(), MessageSignature::asByteBuffer);
            return new ReportChatMessage($$2.index(), $$2.sender(), $$2.sessionId(), $$3.timeStamp(), $$3.salt(), $$4, $$3.content(), $$5, $$1);
        }

        public Builder copy() {
            return new Builder(((ChatReport)this.report).copy(), this.limits);
        }
    }
}

