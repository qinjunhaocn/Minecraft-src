/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.MatchException
 *  java.lang.runtime.SwitchBootstraps
 */
package net.minecraft.client.quickplay;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

public class QuickPlay {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Component ERROR_TITLE = Component.translatable("quickplay.error.title");
    private static final Component INVALID_IDENTIFIER = Component.translatable("quickplay.error.invalid_identifier");
    private static final Component REALM_CONNECT = Component.translatable("quickplay.error.realm_connect");
    private static final Component REALM_PERMISSION = Component.translatable("quickplay.error.realm_permission");
    private static final Component TO_TITLE = Component.translatable("gui.toTitle");
    private static final Component TO_WORLD_LIST = Component.translatable("gui.toWorld");
    private static final Component TO_REALMS_LIST = Component.translatable("gui.toRealms");

    public static void connect(Minecraft $$0, GameConfig.QuickPlayVariant $$1, RealmsClient $$2) {
        if (!$$1.isEnabled()) {
            LOGGER.error("Quick play disabled");
            $$0.setScreen(new TitleScreen());
            return;
        }
        GameConfig.QuickPlayVariant quickPlayVariant = $$1;
        Objects.requireNonNull(quickPlayVariant);
        GameConfig.QuickPlayVariant quickPlayVariant2 = quickPlayVariant;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{GameConfig.QuickPlayMultiplayerData.class, GameConfig.QuickPlayRealmsData.class, GameConfig.QuickPlaySinglePlayerData.class, GameConfig.QuickPlayDisabled.class}, (Object)quickPlayVariant2, (int)n)) {
            default: {
                throw new MatchException(null, null);
            }
            case 0: {
                GameConfig.QuickPlayMultiplayerData $$3 = (GameConfig.QuickPlayMultiplayerData)quickPlayVariant2;
                QuickPlay.joinMultiplayerWorld($$0, $$3.serverAddress());
                break;
            }
            case 1: {
                GameConfig.QuickPlayRealmsData $$4 = (GameConfig.QuickPlayRealmsData)quickPlayVariant2;
                QuickPlay.joinRealmsWorld($$0, $$2, $$4.realmId());
                break;
            }
            case 2: {
                GameConfig.QuickPlaySinglePlayerData $$5 = (GameConfig.QuickPlaySinglePlayerData)quickPlayVariant2;
                String $$6 = $$5.worldId();
                if (StringUtil.isBlank($$6)) {
                    $$6 = QuickPlay.getLatestSingleplayerWorld($$0.getLevelSource());
                }
                QuickPlay.joinSingleplayerWorld($$0, $$6);
                break;
            }
            case 3: {
                GameConfig.QuickPlayDisabled $$7 = (GameConfig.QuickPlayDisabled)quickPlayVariant2;
                LOGGER.error("Quick play disabled");
                $$0.setScreen(new TitleScreen());
            }
        }
    }

    @Nullable
    private static String getLatestSingleplayerWorld(LevelStorageSource $$0) {
        try {
            List<LevelSummary> $$1 = $$0.loadLevelSummaries($$0.findLevelCandidates()).get();
            if ($$1.isEmpty()) {
                LOGGER.warn("no latest singleplayer world found");
                return null;
            }
            return ((LevelSummary)$$1.getFirst()).getLevelId();
        } catch (InterruptedException | ExecutionException $$2) {
            LOGGER.error("failed to load singleplayer world summaries", $$2);
            return null;
        }
    }

    private static void joinSingleplayerWorld(Minecraft $$0, @Nullable String $$1) {
        if (StringUtil.isBlank($$1) || !$$0.getLevelSource().levelExists($$1)) {
            SelectWorldScreen $$2 = new SelectWorldScreen(new TitleScreen());
            $$0.setScreen(new DisconnectedScreen((Screen)$$2, ERROR_TITLE, INVALID_IDENTIFIER, TO_WORLD_LIST));
            return;
        }
        $$0.createWorldOpenFlows().openWorld($$1, () -> $$0.setScreen(new TitleScreen()));
    }

    private static void joinMultiplayerWorld(Minecraft $$0, String $$1) {
        ServerList $$2 = new ServerList($$0);
        $$2.load();
        ServerData $$3 = $$2.get($$1);
        if ($$3 == null) {
            $$3 = new ServerData(I18n.a("selectServer.defaultName", new Object[0]), $$1, ServerData.Type.OTHER);
            $$2.add($$3, true);
            $$2.save();
        }
        ServerAddress $$4 = ServerAddress.parseString($$1);
        ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), $$0, $$4, $$3, true, null);
    }

    /*
     * WARNING - void declaration
     */
    private static void joinRealmsWorld(Minecraft $$0, RealmsClient $$1, String $$2) {
        void $$9;
        void $$10;
        try {
            long $$3 = Long.parseLong($$2);
            RealmsServerList $$4 = $$1.listRealms();
        } catch (NumberFormatException $$5) {
            RealmsMainScreen $$6 = new RealmsMainScreen(new TitleScreen());
            $$0.setScreen(new DisconnectedScreen((Screen)$$6, ERROR_TITLE, INVALID_IDENTIFIER, TO_REALMS_LIST));
            return;
        } catch (RealmsServiceException $$7) {
            TitleScreen $$8 = new TitleScreen();
            $$0.setScreen(new DisconnectedScreen((Screen)$$8, ERROR_TITLE, REALM_CONNECT, TO_TITLE));
            return;
        }
        RealmsServer $$11 = $$10.servers.stream().filter(arg_0 -> QuickPlay.lambda$joinRealmsWorld$1((long)$$9, arg_0)).findFirst().orElse(null);
        if ($$11 == null) {
            RealmsMainScreen $$12 = new RealmsMainScreen(new TitleScreen());
            $$0.setScreen(new DisconnectedScreen((Screen)$$12, ERROR_TITLE, REALM_PERMISSION, TO_REALMS_LIST));
            return;
        }
        TitleScreen $$13 = new TitleScreen();
        $$0.setScreen(new RealmsLongRunningMcoTaskScreen($$13, new GetServerDetailsTask($$13, $$11)));
    }

    private static /* synthetic */ boolean lambda$joinRealmsWorld$1(long $$0, RealmsServer $$1) {
        return $$1.id == $$0;
    }
}

