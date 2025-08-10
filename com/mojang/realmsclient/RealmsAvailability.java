/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.MatchException
 */
package com.mojang.realmsclient;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class RealmsAvailability {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private static CompletableFuture<Result> future;

    public static CompletableFuture<Result> get() {
        if (future == null || RealmsAvailability.shouldRefresh(future)) {
            future = RealmsAvailability.check();
        }
        return future;
    }

    private static boolean shouldRefresh(CompletableFuture<Result> $$0) {
        Result $$1 = $$0.getNow(null);
        return $$1 != null && $$1.exception() != null;
    }

    private static CompletableFuture<Result> check() {
        User $$0 = Minecraft.getInstance().getUser();
        if ($$0.getType() != User.Type.MSA) {
            return CompletableFuture.completedFuture(new Result(Type.AUTHENTICATION_ERROR));
        }
        return CompletableFuture.supplyAsync(() -> {
            RealmsClient $$0 = RealmsClient.getOrCreate();
            try {
                if ($$0.clientCompatible() != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                    return new Result(Type.INCOMPATIBLE_CLIENT);
                }
                if (!$$0.hasParentalConsent()) {
                    return new Result(Type.NEEDS_PARENTAL_CONSENT);
                }
                return new Result(Type.SUCCESS);
            } catch (RealmsServiceException $$1) {
                LOGGER.error("Couldn't connect to realms", $$1);
                if ($$1.realmsError.errorCode() == 401) {
                    return new Result(Type.AUTHENTICATION_ERROR);
                }
                return new Result($$1);
            }
        }, Util.ioPool());
    }

    public record Result(Type type, @Nullable RealmsServiceException exception) {
        public Result(Type $$0) {
            this($$0, null);
        }

        public Result(RealmsServiceException $$0) {
            this(Type.UNEXPECTED_ERROR, $$0);
        }

        @Nullable
        public Screen createErrorScreen(Screen $$0) {
            return switch (this.type.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> null;
                case 1 -> new RealmsClientOutdatedScreen($$0);
                case 2 -> new RealmsParentalConsentScreen($$0);
                case 3 -> new RealmsGenericErrorScreen(Component.translatable("mco.error.invalid.session.title"), Component.translatable("mco.error.invalid.session.message"), $$0);
                case 4 -> new RealmsGenericErrorScreen(Objects.requireNonNull(this.exception), $$0);
            };
        }

        @Nullable
        public RealmsServiceException exception() {
            return this.exception;
        }
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type SUCCESS = new Type();
        public static final /* enum */ Type INCOMPATIBLE_CLIENT = new Type();
        public static final /* enum */ Type NEEDS_PARENTAL_CONSENT = new Type();
        public static final /* enum */ Type AUTHENTICATION_ERROR = new Type();
        public static final /* enum */ Type UNEXPECTED_ERROR = new Type();
        private static final /* synthetic */ Type[] $VALUES;

        public static Type[] values() {
            return (Type[])$VALUES.clone();
        }

        public static Type valueOf(String $$0) {
            return Enum.valueOf(Type.class, $$0);
        }

        private static /* synthetic */ Type[] a() {
            return new Type[]{SUCCESS, INCOMPATIBLE_CLIENT, NEEDS_PARENTAL_CONSENT, AUTHENTICATION_ERROR, UNEXPECTED_ERROR};
        }

        static {
            $VALUES = Type.a();
        }
    }
}

