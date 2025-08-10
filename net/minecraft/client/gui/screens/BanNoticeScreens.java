/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.BanDetails
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.multiplayer.chat.report.BanReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonLinks;
import org.apache.commons.lang3.StringUtils;

public class BanNoticeScreens {
    private static final Component TEMPORARY_BAN_TITLE = Component.translatable("gui.banned.title.temporary").withStyle(ChatFormatting.BOLD);
    private static final Component PERMANENT_BAN_TITLE = Component.translatable("gui.banned.title.permanent").withStyle(ChatFormatting.BOLD);
    public static final Component NAME_BAN_TITLE = Component.translatable("gui.banned.name.title").withStyle(ChatFormatting.BOLD);
    private static final Component SKIN_BAN_TITLE = Component.translatable("gui.banned.skin.title").withStyle(ChatFormatting.BOLD);
    private static final Component SKIN_BAN_DESCRIPTION = Component.a("gui.banned.skin.description", Component.translationArg(CommonLinks.SUSPENSION_HELP));

    public static ConfirmLinkScreen create(BooleanConsumer $$0, BanDetails $$1) {
        return new ConfirmLinkScreen($$0, BanNoticeScreens.getBannedTitle($$1), BanNoticeScreens.getBannedScreenText($$1), CommonLinks.SUSPENSION_HELP, CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    public static ConfirmLinkScreen createSkinBan(Runnable $$0) {
        URI $$1 = CommonLinks.SUSPENSION_HELP;
        return new ConfirmLinkScreen($$2 -> {
            if ($$2) {
                Util.getPlatform().openUri($$1);
            }
            $$0.run();
        }, SKIN_BAN_TITLE, SKIN_BAN_DESCRIPTION, $$1, CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    public static ConfirmLinkScreen createNameBan(String $$0, Runnable $$1) {
        URI $$22 = CommonLinks.SUSPENSION_HELP;
        return new ConfirmLinkScreen($$2 -> {
            if ($$2) {
                Util.getPlatform().openUri($$22);
            }
            $$1.run();
        }, NAME_BAN_TITLE, (Component)Component.a("gui.banned.name.description", Component.literal($$0).withStyle(ChatFormatting.YELLOW), Component.translationArg(CommonLinks.SUSPENSION_HELP)), $$22, CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    private static Component getBannedTitle(BanDetails $$0) {
        return BanNoticeScreens.isTemporaryBan($$0) ? TEMPORARY_BAN_TITLE : PERMANENT_BAN_TITLE;
    }

    private static Component getBannedScreenText(BanDetails $$0) {
        return Component.a("gui.banned.description", BanNoticeScreens.getBanReasonText($$0), BanNoticeScreens.getBanStatusText($$0), Component.translationArg(CommonLinks.SUSPENSION_HELP));
    }

    private static Component getBanReasonText(BanDetails $$0) {
        String $$1 = $$0.reason();
        String $$2 = $$0.reasonMessage();
        if (StringUtils.isNumeric($$1)) {
            MutableComponent $$7;
            int $$3 = Integer.parseInt($$1);
            BanReason $$4 = BanReason.byId($$3);
            if ($$4 != null) {
                MutableComponent $$5 = ComponentUtils.mergeStyles($$4.title().copy(), Style.EMPTY.withBold(true));
            } else if ($$2 != null) {
                MutableComponent $$6 = Component.a("gui.banned.description.reason_id_message", $$3, $$2).withStyle(ChatFormatting.BOLD);
            } else {
                $$7 = Component.a("gui.banned.description.reason_id", $$3).withStyle(ChatFormatting.BOLD);
            }
            return Component.a("gui.banned.description.reason", $$7);
        }
        return Component.translatable("gui.banned.description.unknownreason");
    }

    private static Component getBanStatusText(BanDetails $$0) {
        if (BanNoticeScreens.isTemporaryBan($$0)) {
            Component $$1 = BanNoticeScreens.getBanDurationText($$0);
            return Component.a("gui.banned.description.temporary", Component.a("gui.banned.description.temporary.duration", $$1).withStyle(ChatFormatting.BOLD));
        }
        return Component.translatable("gui.banned.description.permanent").withStyle(ChatFormatting.BOLD);
    }

    private static Component getBanDurationText(BanDetails $$0) {
        Duration $$1 = Duration.between(Instant.now(), $$0.expires());
        long $$2 = $$1.toHours();
        if ($$2 > 72L) {
            return CommonComponents.days($$1.toDays());
        }
        if ($$2 < 1L) {
            return CommonComponents.minutes($$1.toMinutes());
        }
        return CommonComponents.hours($$1.toHours());
    }

    private static boolean isTemporaryBan(BanDetails $$0) {
        return $$0.expires() != null;
    }
}

