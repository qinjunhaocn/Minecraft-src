/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.multiplayer.chat.report;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.multiplayer.chat.report.ReportType;
import net.minecraft.network.chat.Component;

public final class ReportReason
extends Enum<ReportReason> {
    public static final /* enum */ ReportReason I_WANT_TO_REPORT_THEM = new ReportReason("i_want_to_report_them");
    public static final /* enum */ ReportReason HATE_SPEECH = new ReportReason("hate_speech");
    public static final /* enum */ ReportReason HARASSMENT_OR_BULLYING = new ReportReason("harassment_or_bullying");
    public static final /* enum */ ReportReason SELF_HARM_OR_SUICIDE = new ReportReason("self_harm_or_suicide");
    public static final /* enum */ ReportReason IMMINENT_HARM = new ReportReason("imminent_harm");
    public static final /* enum */ ReportReason DEFAMATION_IMPERSONATION_FALSE_INFORMATION = new ReportReason("defamation_impersonation_false_information");
    public static final /* enum */ ReportReason ALCOHOL_TOBACCO_DRUGS = new ReportReason("alcohol_tobacco_drugs");
    public static final /* enum */ ReportReason CHILD_SEXUAL_EXPLOITATION_OR_ABUSE = new ReportReason("child_sexual_exploitation_or_abuse");
    public static final /* enum */ ReportReason TERRORISM_OR_VIOLENT_EXTREMISM = new ReportReason("terrorism_or_violent_extremism");
    public static final /* enum */ ReportReason NON_CONSENSUAL_INTIMATE_IMAGERY = new ReportReason("non_consensual_intimate_imagery");
    public static final /* enum */ ReportReason SEXUALLY_INAPPROPRIATE = new ReportReason("sexually_inappropriate");
    private final String backendName;
    private final Component title;
    private final Component description;
    private static final /* synthetic */ ReportReason[] $VALUES;

    public static ReportReason[] values() {
        return (ReportReason[])$VALUES.clone();
    }

    public static ReportReason valueOf(String $$0) {
        return Enum.valueOf(ReportReason.class, $$0);
    }

    private ReportReason(String $$0) {
        this.backendName = $$0.toUpperCase(Locale.ROOT);
        String $$1 = "gui.abuseReport.reason." + $$0;
        this.title = Component.translatable($$1);
        this.description = Component.translatable($$1 + ".description");
    }

    public String backendName() {
        return this.backendName;
    }

    public Component title() {
        return this.title;
    }

    public Component description() {
        return this.description;
    }

    public static List<ReportReason> getIncompatibleCategories(ReportType $$0) {
        return switch ($$0) {
            case ReportType.CHAT -> List.of((Object)((Object)SEXUALLY_INAPPROPRIATE));
            case ReportType.SKIN -> List.of((Object)((Object)IMMINENT_HARM), (Object)((Object)DEFAMATION_IMPERSONATION_FALSE_INFORMATION));
            default -> List.of();
        };
    }

    private static /* synthetic */ ReportReason[] d() {
        return new ReportReason[]{I_WANT_TO_REPORT_THEM, HATE_SPEECH, HARASSMENT_OR_BULLYING, SELF_HARM_OR_SUICIDE, IMMINENT_HARM, DEFAMATION_IMPERSONATION_FALSE_INFORMATION, ALCOHOL_TOBACCO_DRUGS, CHILD_SEXUAL_EXPLOITATION_OR_ABUSE, TERRORISM_OR_VIOLENT_EXTREMISM, NON_CONSENSUAL_INTIMATE_IMAGERY, SEXUALLY_INAPPROPRIATE};
    }

    static {
        $VALUES = ReportReason.d();
    }
}

