/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 */
package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.report.AbuseReportSender;
import net.minecraft.client.multiplayer.chat.report.Report;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.network.chat.Component;

public final class ReportingContext {
    private static final int LOG_CAPACITY = 1024;
    private final AbuseReportSender sender;
    private final ReportEnvironment environment;
    private final ChatLog chatLog;
    @Nullable
    private Report draftReport;

    public ReportingContext(AbuseReportSender $$0, ReportEnvironment $$1, ChatLog $$2) {
        this.sender = $$0;
        this.environment = $$1;
        this.chatLog = $$2;
    }

    public static ReportingContext create(ReportEnvironment $$0, UserApiService $$1) {
        ChatLog $$2 = new ChatLog(1024);
        AbuseReportSender $$3 = AbuseReportSender.create($$0, $$1);
        return new ReportingContext($$3, $$0, $$2);
    }

    public void draftReportHandled(Minecraft $$0, Screen $$1, Runnable $$2, boolean $$3) {
        if (this.draftReport != null) {
            Report $$42 = this.draftReport.copy();
            $$0.setScreen(new ConfirmScreen($$4 -> {
                this.setReportDraft(null);
                if ($$4) {
                    $$0.setScreen($$42.createScreen($$1, this));
                } else {
                    $$2.run();
                }
            }, Component.translatable($$3 ? "gui.abuseReport.draft.quittotitle.title" : "gui.abuseReport.draft.title"), Component.translatable($$3 ? "gui.abuseReport.draft.quittotitle.content" : "gui.abuseReport.draft.content"), Component.translatable("gui.abuseReport.draft.edit"), Component.translatable("gui.abuseReport.draft.discard")));
        } else {
            $$2.run();
        }
    }

    public AbuseReportSender sender() {
        return this.sender;
    }

    public ChatLog chatLog() {
        return this.chatLog;
    }

    public boolean matches(ReportEnvironment $$0) {
        return Objects.equals((Object)this.environment, (Object)$$0);
    }

    public void setReportDraft(@Nullable Report $$0) {
        this.draftReport = $$0;
    }

    public boolean hasDraftReport() {
        return this.draftReport != null;
    }

    public boolean hasDraftReportFor(UUID $$0) {
        return this.hasDraftReport() && this.draftReport.isReportedPlayer($$0);
    }
}

