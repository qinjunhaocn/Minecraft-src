/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package com.mojang.realmsclient.client.worldupload;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.worldupload.RealmsUploadCanceledException;
import com.mojang.realmsclient.client.worldupload.RealmsUploadFailedException;
import com.mojang.realmsclient.client.worldupload.RealmsWorldUpload;
import com.mojang.realmsclient.client.worldupload.RealmsWorldUploadStatusTracker;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSetting;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.slf4j.Logger;

public class RealmsCreateWorldFlow {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void createWorld(Minecraft $$0, Screen $$1, Screen $$2, int $$3, RealmsServer $$4, @Nullable RealmCreationTask $$5) {
        CreateWorldScreen.openFresh($$0, $$1, ($$62, $$7, $$8, $$9) -> {
            void $$12;
            try {
                Path $$10 = RealmsCreateWorldFlow.createTemporaryWorldFolder($$7, $$8, $$9);
            } catch (IOException $$11) {
                LOGGER.warn("Failed to create temporary world folder.");
                $$0.setScreen(new RealmsGenericErrorScreen(Component.translatable("mco.create.world.failed"), $$2));
                return true;
            }
            RealmsWorldOptions $$13 = RealmsWorldOptions.createFromSettings($$8.getLevelSettings(), $$8.getLevelSettings().allowCommands(), SharedConstants.getCurrentVersion().name());
            RealmsSlot $$14 = new RealmsSlot($$3, $$13, List.of((Object)RealmsSetting.hardcoreSetting($$8.getLevelSettings().hardcore())));
            RealmsWorldUpload $$15 = new RealmsWorldUpload((Path)$$12, $$14, $$0.getUser(), $$3.id, RealmsWorldUploadStatusTracker.noOp());
            $$0.forceSetScreen(new AlertScreen($$15::cancel, Component.translatable("mco.create.world.reset.title"), Component.empty(), CommonComponents.GUI_CANCEL, false));
            if ($$5 != null) {
                $$5.run();
            }
            $$15.packAndUpload().handleAsync(($$5, $$6) -> {
                if ($$6 != null) {
                    if ($$6 instanceof CompletionException) {
                        LayeredRegistryAccess $$7 = (CompletionException)$$6;
                        $$6 = $$7.getCause();
                    }
                    if ($$6 instanceof RealmsUploadCanceledException) {
                        $$0.forceSetScreen($$2);
                    } else {
                        if ($$6 instanceof RealmsUploadFailedException) {
                            RealmsUploadFailedException $$8 = (RealmsUploadFailedException)$$6;
                            LOGGER.warn("Failed to create realms world {}", (Object)$$8.getStatusMessage());
                        } else {
                            LOGGER.warn("Failed to create realms world {}", (Object)$$6.getMessage());
                        }
                        $$0.forceSetScreen(new RealmsGenericErrorScreen(Component.translatable("mco.create.world.failed"), $$2));
                    }
                } else {
                    if ($$1 instanceof RealmsConfigureWorldScreen) {
                        RealmsConfigureWorldScreen $$9 = (RealmsConfigureWorldScreen)$$1;
                        $$9.fetchServerData($$3.id);
                    }
                    if ($$5 != null) {
                        RealmsMainScreen.play($$4, $$1, true);
                    } else {
                        $$0.forceSetScreen($$1);
                    }
                    RealmsMainScreen.refreshServerList();
                }
                return null;
            }, (Executor)$$0);
            return true;
        });
    }

    private static Path createTemporaryWorldFolder(LayeredRegistryAccess<RegistryLayer> $$0, PrimaryLevelData $$1, @Nullable Path $$2) throws IOException {
        Path $$3 = Files.createTempDirectory("minecraft_realms_world_upload", new FileAttribute[0]);
        if ($$2 != null) {
            Files.move($$2, $$3.resolve("datapacks"), new CopyOption[0]);
        }
        CompoundTag $$4 = $$1.createTag($$0.compositeAccess(), null);
        CompoundTag $$5 = new CompoundTag();
        $$5.put("Data", $$4);
        Path $$6 = Files.createFile($$3.resolve("level.dat"), new FileAttribute[0]);
        NbtIo.writeCompressed($$5, $$6);
        return $$3;
    }
}

