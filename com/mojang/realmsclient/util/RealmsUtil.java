/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.util;

import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class RealmsUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component RIGHT_NOW = Component.translatable("mco.util.time.now");
    private static final int MINUTES = 60;
    private static final int HOURS = 3600;
    private static final int DAYS = 86400;

    public static Component convertToAgePresentation(long $$0) {
        if ($$0 < 0L) {
            return RIGHT_NOW;
        }
        long $$1 = $$0 / 1000L;
        if ($$1 < 60L) {
            return Component.a("mco.time.secondsAgo", $$1);
        }
        if ($$1 < 3600L) {
            long $$2 = $$1 / 60L;
            return Component.a("mco.time.minutesAgo", $$2);
        }
        if ($$1 < 86400L) {
            long $$3 = $$1 / 3600L;
            return Component.a("mco.time.hoursAgo", $$3);
        }
        long $$4 = $$1 / 86400L;
        return Component.a("mco.time.daysAgo", $$4);
    }

    public static Component convertToAgePresentationFromInstant(Date $$0) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - $$0.getTime());
    }

    public static void renderPlayerFace(GuiGraphics $$0, int $$1, int $$2, int $$3, UUID $$4) {
        Minecraft $$5 = Minecraft.getInstance();
        ProfileResult $$6 = $$5.getMinecraftSessionService().fetchProfile($$4, false);
        PlayerSkin $$7 = $$6 != null ? $$5.getSkinManager().getInsecureSkin($$6.profile()) : DefaultPlayerSkin.get($$4);
        PlayerFaceRenderer.draw($$0, $$7, $$1, $$2, $$3);
    }

    public static <T> CompletableFuture<T> supplyAsync(RealmsIoFunction<T> $$0, @Nullable Consumer<RealmsServiceException> $$1) {
        return CompletableFuture.supplyAsync(() -> {
            RealmsClient $$2 = RealmsClient.getOrCreate();
            try {
                return $$0.apply($$2);
            } catch (Throwable $$3) {
                if ($$3 instanceof RealmsServiceException) {
                    RealmsServiceException $$4 = (RealmsServiceException)$$3;
                    if ($$1 != null) {
                        $$1.accept($$4);
                    }
                } else {
                    LOGGER.error("Unhandled exception", $$3);
                }
                throw new RuntimeException($$3);
            }
        }, Util.nonCriticalIoPool());
    }

    public static CompletableFuture<Void> runAsync(RealmsIoConsumer $$0, @Nullable Consumer<RealmsServiceException> $$1) {
        return RealmsUtil.supplyAsync($$0, $$1);
    }

    public static Consumer<RealmsServiceException> openScreenOnFailure(Function<RealmsServiceException, Screen> $$0) {
        Minecraft $$1 = Minecraft.getInstance();
        return $$2 -> $$1.execute(() -> $$1.setScreen((Screen)$$0.apply((RealmsServiceException)$$2)));
    }

    public static Consumer<RealmsServiceException> openScreenAndLogOnFailure(Function<RealmsServiceException, Screen> $$0, String $$12) {
        return RealmsUtil.openScreenOnFailure($$0).andThen($$1 -> LOGGER.error($$12, (Throwable)$$1));
    }

    @FunctionalInterface
    public static interface RealmsIoFunction<T> {
        public T apply(RealmsClient var1) throws RealmsServiceException;
    }

    @FunctionalInterface
    public static interface RealmsIoConsumer
    extends RealmsIoFunction<Void> {
        public void accept(RealmsClient var1) throws RealmsServiceException;

        @Override
        default public Void apply(RealmsClient $$0) throws RealmsServiceException {
            this.accept($$0);
            return null;
        }
    }
}

