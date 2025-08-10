/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.RealmsText;
import com.mojang.realmsclient.util.JsonUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public class RealmsNotification {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String NOTIFICATION_UUID = "notificationUuid";
    private static final String DISMISSABLE = "dismissable";
    private static final String SEEN = "seen";
    private static final String TYPE = "type";
    private static final String VISIT_URL = "visitUrl";
    private static final String INFO_POPUP = "infoPopup";
    static final Component BUTTON_TEXT_FALLBACK = Component.translatable("mco.notification.visitUrl.buttonText.default");
    final UUID uuid;
    final boolean dismissable;
    final boolean seen;
    final String type;

    RealmsNotification(UUID $$0, boolean $$1, boolean $$2, String $$3) {
        this.uuid = $$0;
        this.dismissable = $$1;
        this.seen = $$2;
        this.type = $$3;
    }

    public boolean seen() {
        return this.seen;
    }

    public boolean dismissable() {
        return this.dismissable;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public static List<RealmsNotification> parseList(String $$0) {
        ArrayList<RealmsNotification> $$1 = new ArrayList<RealmsNotification>();
        try {
            JsonArray $$2 = LenientJsonParser.parse($$0).getAsJsonObject().get("notifications").getAsJsonArray();
            for (JsonElement $$3 : $$2) {
                $$1.add(RealmsNotification.parse($$3.getAsJsonObject()));
            }
        } catch (Exception $$4) {
            LOGGER.error("Could not parse list of RealmsNotifications", $$4);
        }
        return $$1;
    }

    private static RealmsNotification parse(JsonObject $$0) {
        UUID $$1 = JsonUtils.getUuidOr(NOTIFICATION_UUID, $$0, null);
        if ($$1 == null) {
            throw new IllegalStateException("Missing required property notificationUuid");
        }
        boolean $$2 = JsonUtils.getBooleanOr(DISMISSABLE, $$0, true);
        boolean $$3 = JsonUtils.getBooleanOr(SEEN, $$0, false);
        String $$4 = JsonUtils.getRequiredString(TYPE, $$0);
        RealmsNotification $$5 = new RealmsNotification($$1, $$2, $$3, $$4);
        return switch ($$4) {
            case VISIT_URL -> VisitUrl.parse($$5, $$0);
            case INFO_POPUP -> InfoPopup.parse($$5, $$0);
            default -> $$5;
        };
    }

    public static class VisitUrl
    extends RealmsNotification {
        private static final String URL = "url";
        private static final String BUTTON_TEXT = "buttonText";
        private static final String MESSAGE = "message";
        private final String url;
        private final RealmsText buttonText;
        private final RealmsText message;

        private VisitUrl(RealmsNotification $$0, String $$1, RealmsText $$2, RealmsText $$3) {
            super($$0.uuid, $$0.dismissable, $$0.seen, $$0.type);
            this.url = $$1;
            this.buttonText = $$2;
            this.message = $$3;
        }

        public static VisitUrl parse(RealmsNotification $$0, JsonObject $$1) {
            String $$2 = JsonUtils.getRequiredString(URL, $$1);
            RealmsText $$3 = JsonUtils.getRequired(BUTTON_TEXT, $$1, RealmsText::parse);
            RealmsText $$4 = JsonUtils.getRequired(MESSAGE, $$1, RealmsText::parse);
            return new VisitUrl($$0, $$2, $$3, $$4);
        }

        public Component getMessage() {
            return this.message.createComponent(Component.translatable("mco.notification.visitUrl.message.default"));
        }

        public Button buildOpenLinkButton(Screen $$0) {
            Component $$1 = this.buttonText.createComponent(BUTTON_TEXT_FALLBACK);
            return Button.builder($$1, ConfirmLinkScreen.confirmLink($$0, this.url)).build();
        }
    }

    public static class InfoPopup
    extends RealmsNotification {
        private static final String TITLE = "title";
        private static final String MESSAGE = "message";
        private static final String IMAGE = "image";
        private static final String URL_BUTTON = "urlButton";
        private final RealmsText title;
        private final RealmsText message;
        private final ResourceLocation image;
        @Nullable
        private final UrlButton urlButton;

        private InfoPopup(RealmsNotification $$0, RealmsText $$1, RealmsText $$2, ResourceLocation $$3, @Nullable UrlButton $$4) {
            super($$0.uuid, $$0.dismissable, $$0.seen, $$0.type);
            this.title = $$1;
            this.message = $$2;
            this.image = $$3;
            this.urlButton = $$4;
        }

        public static InfoPopup parse(RealmsNotification $$0, JsonObject $$1) {
            RealmsText $$2 = JsonUtils.getRequired(TITLE, $$1, RealmsText::parse);
            RealmsText $$3 = JsonUtils.getRequired(MESSAGE, $$1, RealmsText::parse);
            ResourceLocation $$4 = ResourceLocation.parse(JsonUtils.getRequiredString(IMAGE, $$1));
            UrlButton $$5 = JsonUtils.getOptional(URL_BUTTON, $$1, UrlButton::parse);
            return new InfoPopup($$0, $$2, $$3, $$4, $$5);
        }

        @Nullable
        public PopupScreen buildScreen(Screen $$0, Consumer<UUID> $$12) {
            Component $$22 = this.title.createComponent();
            if ($$22 == null) {
                LOGGER.warn("Realms info popup had title with no available translation: {}", (Object)this.title);
                return null;
            }
            PopupScreen.Builder $$3 = new PopupScreen.Builder($$0, $$22).setImage(this.image).setMessage(this.message.createComponent(CommonComponents.EMPTY));
            if (this.urlButton != null) {
                $$3.addButton(this.urlButton.urlText.createComponent(BUTTON_TEXT_FALLBACK), $$2 -> {
                    Minecraft $$32 = Minecraft.getInstance();
                    $$32.setScreen(new ConfirmLinkScreen($$3 -> {
                        if ($$3) {
                            Util.getPlatform().openUri(this.urlButton.url);
                            $$32.setScreen($$0);
                        } else {
                            $$32.setScreen((Screen)$$2);
                        }
                    }, this.urlButton.url, true));
                    $$12.accept(this.uuid());
                });
            }
            $$3.addButton(CommonComponents.GUI_OK, $$1 -> {
                $$1.onClose();
                $$12.accept(this.uuid());
            });
            $$3.onClose(() -> $$12.accept(this.uuid()));
            return $$3.build();
        }
    }

    static final class UrlButton
    extends Record {
        final String url;
        final RealmsText urlText;
        private static final String URL = "url";
        private static final String URL_TEXT = "urlText";

        private UrlButton(String $$0, RealmsText $$1) {
            this.url = $$0;
            this.urlText = $$1;
        }

        public static UrlButton parse(JsonObject $$0) {
            String $$1 = JsonUtils.getRequiredString(URL, $$0);
            RealmsText $$2 = JsonUtils.getRequired(URL_TEXT, $$0, RealmsText::parse);
            return new UrlButton($$1, $$2);
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{UrlButton.class, "url;urlText", "url", "urlText"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UrlButton.class, "url;urlText", "url", "urlText"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UrlButton.class, "url;urlText", "url", "urlText"}, this, $$0);
        }

        public String url() {
            return this.url;
        }

        public RealmsText urlText() {
            return this.urlText;
        }
    }
}

