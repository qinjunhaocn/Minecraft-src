/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.multiplayer.chat.report;

import java.util.Locale;

public final class ReportType
extends Enum<ReportType> {
    public static final /* enum */ ReportType CHAT = new ReportType("chat");
    public static final /* enum */ ReportType SKIN = new ReportType("skin");
    public static final /* enum */ ReportType USERNAME = new ReportType("username");
    private final String backendName;
    private static final /* synthetic */ ReportType[] $VALUES;

    public static ReportType[] values() {
        return (ReportType[])$VALUES.clone();
    }

    public static ReportType valueOf(String $$0) {
        return Enum.valueOf(ReportType.class, $$0);
    }

    private ReportType(String $$0) {
        this.backendName = $$0.toUpperCase(Locale.ROOT);
    }

    public String backendName() {
        return this.backendName;
    }

    private static /* synthetic */ ReportType[] b() {
        return new ReportType[]{CHAT, SKIN, USERNAME};
    }

    static {
        $VALUES = ReportType.b();
    }
}

