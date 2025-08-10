/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.exception.RealmsHttpException;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

public interface RealmsError {
    public static final Component NO_MESSAGE = Component.translatable("mco.errorMessage.noDetails");
    public static final Logger LOGGER = LogUtils.getLogger();

    public int errorCode();

    public Component errorMessage();

    public String logMessage();

    public static RealmsError parse(int $$0, String $$1) {
        if ($$0 == 429) {
            return CustomError.SERVICE_BUSY;
        }
        if (Strings.isNullOrEmpty($$1)) {
            return CustomError.noPayload($$0);
        }
        try {
            JsonObject $$2 = LenientJsonParser.parse($$1).getAsJsonObject();
            String $$3 = GsonHelper.getAsString($$2, "reason", null);
            String $$4 = GsonHelper.getAsString($$2, "errorMsg", null);
            int $$5 = GsonHelper.getAsInt($$2, "errorCode", -1);
            if ($$4 != null || $$3 != null || $$5 != -1) {
                return new ErrorWithJsonPayload($$0, $$5 != -1 ? $$5 : $$0, $$3, $$4);
            }
        } catch (Exception $$6) {
            LOGGER.error("Could not parse RealmsError", $$6);
        }
        return new ErrorWithRawPayload($$0, $$1);
    }

    public record CustomError(int httpCode, @Nullable Component payload) implements RealmsError
    {
        public static final CustomError SERVICE_BUSY = new CustomError(429, Component.translatable("mco.errorMessage.serviceBusy"));
        public static final Component RETRY_MESSAGE = Component.translatable("mco.errorMessage.retry");
        public static final String BODY_TAG = "<body>";
        public static final String CLOSING_BODY_TAG = "</body>";

        public static CustomError unknownCompatibilityResponse(String $$0) {
            return new CustomError(500, Component.a("mco.errorMessage.realmsService.unknownCompatibility", $$0));
        }

        public static CustomError configurationError() {
            return new CustomError(500, Component.translatable("mco.errorMessage.realmsService.configurationError"));
        }

        public static CustomError connectivityError(RealmsHttpException $$0) {
            return new CustomError(500, Component.a("mco.errorMessage.realmsService.connectivity", $$0.getMessage()));
        }

        public static CustomError retry(int $$0) {
            return new CustomError($$0, RETRY_MESSAGE);
        }

        public static CustomError noPayload(int $$0) {
            return new CustomError($$0, null);
        }

        public static CustomError htmlPayload(int $$0, String $$1) {
            int $$2 = $$1.indexOf(BODY_TAG);
            int $$3 = $$1.indexOf(CLOSING_BODY_TAG);
            if ($$2 >= 0 && $$3 > $$2) {
                return new CustomError($$0, Component.literal($$1.substring($$2 + BODY_TAG.length(), $$3).trim()));
            }
            LOGGER.error("Got an error with an unreadable html body {}", (Object)$$1);
            return new CustomError($$0, null);
        }

        @Override
        public int errorCode() {
            return this.httpCode;
        }

        @Override
        public Component errorMessage() {
            return this.payload != null ? this.payload : NO_MESSAGE;
        }

        @Override
        public String logMessage() {
            if (this.payload != null) {
                return String.format(Locale.ROOT, "Realms service error (%d) with message '%s'", this.httpCode, this.payload.getString());
            }
            return String.format(Locale.ROOT, "Realms service error (%d) with no payload", this.httpCode);
        }

        @Nullable
        public Component payload() {
            return this.payload;
        }
    }

    public record ErrorWithJsonPayload(int httpCode, int code, @Nullable String reason, @Nullable String message) implements RealmsError
    {
        @Override
        public int errorCode() {
            return this.code;
        }

        @Override
        public Component errorMessage() {
            String $$1;
            String $$0 = "mco.errorMessage." + this.code;
            if (I18n.exists($$0)) {
                return Component.translatable($$0);
            }
            if (this.reason != null && I18n.exists($$1 = "mco.errorReason." + this.reason)) {
                return Component.translatable($$1);
            }
            return this.message != null ? Component.literal(this.message) : NO_MESSAGE;
        }

        @Override
        public String logMessage() {
            return String.format(Locale.ROOT, "Realms service error (%d/%d/%s) with message '%s'", this.httpCode, this.code, this.reason, this.message);
        }

        @Nullable
        public String reason() {
            return this.reason;
        }

        @Nullable
        public String message() {
            return this.message;
        }
    }

    public record ErrorWithRawPayload(int httpCode, String payload) implements RealmsError
    {
        @Override
        public int errorCode() {
            return this.httpCode;
        }

        @Override
        public Component errorMessage() {
            return Component.literal(this.payload);
        }

        @Override
        public String logMessage() {
            return String.format(Locale.ROOT, "Realms service error (%d) with raw payload '%s'", this.httpCode, this.payload);
        }
    }

    public record AuthenticationError(String message) implements RealmsError
    {
        public static final int ERROR_CODE = 401;

        @Override
        public int errorCode() {
            return 401;
        }

        @Override
        public Component errorMessage() {
            return Component.literal(this.message);
        }

        @Override
        public String logMessage() {
            return String.format(Locale.ROOT, "Realms authentication error with message '%s'", this.message);
        }
    }
}

