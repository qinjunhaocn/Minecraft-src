/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.properties.PropertyMap$Serializer
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UndashedUuid
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 */
package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.TracyBootstrap;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Optionull;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.core.UUIDUtil;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;

public class Main {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    @DontObfuscate
    public static void main(String[] $$0) {
        void $$76;
        void $$75;
        OptionParser $$1 = new OptionParser();
        $$1.allowsUnrecognizedOptions();
        $$1.accepts("demo");
        $$1.accepts("disableMultiplayer");
        $$1.accepts("disableChat");
        $$1.accepts("fullscreen");
        $$1.accepts("checkGlErrors");
        OptionSpecBuilder $$2 = $$1.accepts("renderDebugLabels");
        OptionSpecBuilder $$3 = $$1.accepts("jfrProfile");
        OptionSpecBuilder $$4 = $$1.accepts("tracy");
        OptionSpecBuilder $$5 = $$1.accepts("tracyNoImages");
        ArgumentAcceptingOptionSpec $$6 = $$1.accepts("quickPlayPath").withRequiredArg();
        ArgumentAcceptingOptionSpec $$7 = $$1.accepts("quickPlaySingleplayer").withOptionalArg();
        ArgumentAcceptingOptionSpec $$8 = $$1.accepts("quickPlayMultiplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec $$9 = $$1.accepts("quickPlayRealms").withRequiredArg();
        ArgumentAcceptingOptionSpec $$10 = $$1.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo((Object)new File("."), (Object[])new File[0]);
        ArgumentAcceptingOptionSpec $$11 = $$1.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec $$12 = $$1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec $$13 = $$1.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec $$14 = $$1.accepts("proxyPort").withRequiredArg().defaultsTo((Object)"8080", (Object[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec $$15 = $$1.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec $$16 = $$1.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec $$17 = $$1.accepts("username").withRequiredArg().defaultsTo((Object)("Player" + System.currentTimeMillis() % 1000L), (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$18 = $$1.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec $$19 = $$1.accepts("xuid").withOptionalArg().defaultsTo((Object)"", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$20 = $$1.accepts("clientId").withOptionalArg().defaultsTo((Object)"", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$21 = $$1.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec $$22 = $$1.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec $$23 = $$1.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo((Object)854, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec $$24 = $$1.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo((Object)480, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec $$25 = $$1.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec $$26 = $$1.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec $$27 = $$1.accepts("userProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$28 = $$1.accepts("profileProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$29 = $$1.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec $$30 = $$1.accepts("userType").withRequiredArg().defaultsTo((Object)"legacy", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$31 = $$1.accepts("versionType").withRequiredArg().defaultsTo((Object)"release", (Object[])new String[0]);
        NonOptionArgumentSpec $$32 = $$1.nonOptions();
        OptionSet $$33 = $$1.parse($$0);
        File $$34 = (File)Main.parseArgument($$33, $$10);
        String $$35 = (String)Main.parseArgument($$33, $$22);
        String $$36 = "Pre-bootstrap";
        try {
            String $$42;
            User.Type $$43;
            if ($$33.has((OptionSpec)$$3)) {
                JvmProfiler.INSTANCE.start(Environment.CLIENT);
            }
            if ($$33.has((OptionSpec)$$4)) {
                TracyBootstrap.setup();
            }
            Stopwatch $$37 = Stopwatch.createStarted(Ticker.systemTicker());
            Stopwatch $$38 = Stopwatch.createStarted(Ticker.systemTicker());
            GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_TOTAL_TIME_MS, $$37);
            GameLoadTimesEvent.INSTANCE.beginStep(TelemetryProperty.LOAD_TIME_PRE_WINDOW_MS, $$38);
            SharedConstants.tryDetectVersion();
            TracyClient.reportAppInfo((String)("Minecraft Java Edition " + SharedConstants.getCurrentVersion().name()));
            CompletableFuture<?> $$39 = DataFixers.optimize(DataFixTypes.TYPES_FOR_LEVEL_LIST);
            CrashReport.preload();
            Logger $$40 = LogUtils.getLogger();
            $$36 = "Bootstrap";
            Bootstrap.bootStrap();
            ClientBootstrap.bootstrap();
            GameLoadTimesEvent.INSTANCE.setBootstrapTime(Bootstrap.bootstrapDuration.get());
            Bootstrap.validate();
            $$36 = "Argument parsing";
            List $$41 = $$33.valuesOf((OptionSpec)$$32);
            if (!$$41.isEmpty()) {
                $$40.info("Completely ignored arguments: {}", (Object)$$41);
            }
            if (($$43 = User.Type.byName($$42 = (String)$$30.value($$33))) == null) {
                $$40.warn("Unrecognized user type: {}", (Object)$$42);
            }
            String $$44 = (String)Main.parseArgument($$33, $$13);
            Proxy $$45 = Proxy.NO_PROXY;
            if ($$44 != null) {
                try {
                    $$45 = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress($$44, (int)((Integer)Main.parseArgument($$33, $$14))));
                } catch (Exception exception) {
                    // empty catch block
                }
            }
            final String $$46 = (String)Main.parseArgument($$33, $$15);
            final String $$47 = (String)Main.parseArgument($$33, $$16);
            if (!$$45.equals(Proxy.NO_PROXY) && Main.stringHasValue($$46) && Main.stringHasValue($$47)) {
                Authenticator.setDefault(new Authenticator(){

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication($$46, $$47.toCharArray());
                    }
                });
            }
            int $$48 = (Integer)Main.parseArgument($$33, $$23);
            int $$49 = (Integer)Main.parseArgument($$33, $$24);
            OptionalInt $$50 = Main.ofNullable((Integer)Main.parseArgument($$33, $$25));
            OptionalInt $$51 = Main.ofNullable((Integer)Main.parseArgument($$33, $$26));
            boolean $$52 = $$33.has("fullscreen");
            boolean $$53 = $$33.has("demo");
            boolean $$54 = $$33.has("disableMultiplayer");
            boolean $$55 = $$33.has("disableChat");
            boolean $$56 = !$$33.has((OptionSpec)$$5);
            boolean $$57 = $$33.has((OptionSpec)$$2);
            Gson $$58 = new GsonBuilder().registerTypeAdapter(PropertyMap.class, (Object)new PropertyMap.Serializer()).create();
            PropertyMap $$59 = GsonHelper.fromJson($$58, (String)Main.parseArgument($$33, $$27), PropertyMap.class);
            PropertyMap $$60 = GsonHelper.fromJson($$58, (String)Main.parseArgument($$33, $$28), PropertyMap.class);
            String $$61 = (String)Main.parseArgument($$33, $$31);
            File $$62 = $$33.has((OptionSpec)$$11) ? (File)Main.parseArgument($$33, $$11) : new File($$34, "assets/");
            File $$63 = $$33.has((OptionSpec)$$12) ? (File)Main.parseArgument($$33, $$12) : new File($$34, "resourcepacks/");
            UUID $$64 = Main.hasValidUuid((OptionSpec<String>)$$18, $$33, $$40) ? UndashedUuid.fromStringLenient((String)((String)$$18.value($$33))) : UUIDUtil.createOfflinePlayerUUID((String)$$17.value($$33));
            String $$65 = $$33.has((OptionSpec)$$29) ? (String)$$29.value($$33) : null;
            String $$66 = (String)$$33.valueOf((OptionSpec)$$19);
            String $$67 = (String)$$33.valueOf((OptionSpec)$$20);
            String $$68 = (String)Main.parseArgument($$33, $$6);
            GameConfig.QuickPlayVariant $$69 = Main.getQuickPlayVariant($$33, (OptionSpec<String>)$$7, (OptionSpec<String>)$$8, (OptionSpec<String>)$$9);
            User $$70 = new User((String)$$17.value($$33), $$64, (String)$$21.value($$33), Main.emptyStringToEmptyOptional($$66), Main.emptyStringToEmptyOptional($$67), $$43);
            GameConfig $$71 = new GameConfig(new GameConfig.UserData($$70, $$59, $$60, $$45), new DisplayData($$48, $$49, $$50, $$51, $$52), new GameConfig.FolderData($$34, $$63, $$62, $$65), new GameConfig.GameData($$53, $$35, $$61, $$54, $$55, $$56, $$57), new GameConfig.QuickPlayData($$68, $$69));
            Util.startTimerHackThread();
            $$39.join();
        } catch (Throwable $$72) {
            CrashReport $$73 = CrashReport.forThrowable($$72, $$36);
            CrashReportCategory $$74 = $$73.addCategory("Initialization");
            NativeModuleLister.addCrashSection($$74);
            Minecraft.fillReport(null, null, $$35, null, $$73);
            Minecraft.crash(null, $$34, $$73);
            return;
        }
        Thread $$77 = new Thread("Client Shutdown Thread"){

            @Override
            public void run() {
                Minecraft $$0 = Minecraft.getInstance();
                if ($$0 == null) {
                    return;
                }
                IntegratedServer $$1 = $$0.getSingleplayerServer();
                if ($$1 != null) {
                    $$1.halt(true);
                }
            }
        };
        $$77.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler((Logger)$$75));
        Runtime.getRuntime().addShutdownHook($$77);
        Minecraft $$78 = null;
        try {
            Thread.currentThread().setName("Render thread");
            RenderSystem.initRenderThread();
            $$78 = new Minecraft((GameConfig)$$76);
        } catch (SilentInitException $$79) {
            Util.shutdownExecutors();
            $$75.warn("Failed to create window: ", $$79);
            return;
        } catch (Throwable $$80) {
            CrashReport $$81 = CrashReport.forThrowable($$80, "Initializing game");
            CrashReportCategory $$82 = $$81.addCategory("Initialization");
            NativeModuleLister.addCrashSection($$82);
            Minecraft.fillReport($$78, null, $$76.game.launchVersion, null, $$81);
            Minecraft.crash($$78, $$76.location.gameDirectory, $$81);
            return;
        }
        Minecraft $$83 = $$78;
        $$83.run();
        try {
            $$83.stop();
        } finally {
            $$83.destroy();
        }
    }

    private static GameConfig.QuickPlayVariant getQuickPlayVariant(OptionSet $$0, OptionSpec<String> $$1, OptionSpec<String> $$2, OptionSpec<String> $$3) {
        long $$4 = Stream.of($$1, $$2, $$3).filter(arg_0 -> ((OptionSet)$$0).has(arg_0)).count();
        if ($$4 == 0L) {
            return GameConfig.QuickPlayVariant.DISABLED;
        }
        if ($$4 > 1L) {
            throw new IllegalArgumentException("Only one quick play option can be specified");
        }
        if ($$0.has($$1)) {
            String $$5 = Main.unescapeJavaArgument(Main.parseArgument($$0, $$1));
            return new GameConfig.QuickPlaySinglePlayerData($$5);
        }
        if ($$0.has($$2)) {
            String $$6 = Main.unescapeJavaArgument(Main.parseArgument($$0, $$2));
            return Optionull.mapOrDefault($$6, GameConfig.QuickPlayMultiplayerData::new, GameConfig.QuickPlayVariant.DISABLED);
        }
        if ($$0.has($$3)) {
            String $$7 = Main.unescapeJavaArgument(Main.parseArgument($$0, $$3));
            return Optionull.mapOrDefault($$7, GameConfig.QuickPlayRealmsData::new, GameConfig.QuickPlayVariant.DISABLED);
        }
        return GameConfig.QuickPlayVariant.DISABLED;
    }

    @Nullable
    private static String unescapeJavaArgument(@Nullable String $$0) {
        if ($$0 == null) {
            return null;
        }
        return StringEscapeUtils.unescapeJava($$0);
    }

    private static Optional<String> emptyStringToEmptyOptional(String $$0) {
        return $$0.isEmpty() ? Optional.empty() : Optional.of($$0);
    }

    private static OptionalInt ofNullable(@Nullable Integer $$0) {
        return $$0 != null ? OptionalInt.of($$0) : OptionalInt.empty();
    }

    @Nullable
    private static <T> T parseArgument(OptionSet $$0, OptionSpec<T> $$1) {
        try {
            return (T)$$0.valueOf($$1);
        } catch (Throwable $$2) {
            ArgumentAcceptingOptionSpec $$3;
            List $$4;
            if ($$1 instanceof ArgumentAcceptingOptionSpec && !($$4 = ($$3 = (ArgumentAcceptingOptionSpec)$$1).defaultValues()).isEmpty()) {
                return (T)$$4.get(0);
            }
            throw $$2;
        }
    }

    private static boolean stringHasValue(@Nullable String $$0) {
        return $$0 != null && !$$0.isEmpty();
    }

    private static boolean hasValidUuid(OptionSpec<String> $$0, OptionSet $$1, Logger $$2) {
        return $$1.has($$0) && Main.isUuidValid($$0, $$1, $$2);
    }

    private static boolean isUuidValid(OptionSpec<String> $$0, OptionSet $$1, Logger $$2) {
        try {
            UndashedUuid.fromStringLenient((String)((String)$$0.value($$1)));
        } catch (IllegalArgumentException $$3) {
            $$2.warn("Invalid UUID: '{}", $$0.value($$1));
            return false;
        }
        return true;
    }

    static {
        System.setProperty("java.awt.headless", "true");
    }
}

