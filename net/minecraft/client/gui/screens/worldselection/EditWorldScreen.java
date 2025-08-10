/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  org.apache.commons.io.FileUtils
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class EditWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component NAME_LABEL = Component.translatable("selectWorld.enterName").withStyle(ChatFormatting.GRAY);
    private static final Component RESET_ICON_BUTTON = Component.translatable("selectWorld.edit.resetIcon");
    private static final Component FOLDER_BUTTON = Component.translatable("selectWorld.edit.openFolder");
    private static final Component BACKUP_BUTTON = Component.translatable("selectWorld.edit.backup");
    private static final Component BACKUP_FOLDER_BUTTON = Component.translatable("selectWorld.edit.backupFolder");
    private static final Component OPTIMIZE_BUTTON = Component.translatable("selectWorld.edit.optimize");
    private static final Component OPTIMIZE_TITLE = Component.translatable("optimizeWorld.confirm.title");
    private static final Component OPTIMIIZE_DESCRIPTION = Component.translatable("optimizeWorld.confirm.description");
    private static final Component OPTIMIIZE_CONFIRMATION = Component.translatable("optimizeWorld.confirm.proceed");
    private static final Component SAVE_BUTTON = Component.translatable("selectWorld.edit.save");
    private static final int DEFAULT_WIDTH = 200;
    private static final int VERTICAL_SPACING = 4;
    private static final int HALF_WIDTH = 98;
    private final LinearLayout layout = LinearLayout.vertical().spacing(5);
    private final BooleanConsumer callback;
    private final LevelStorageSource.LevelStorageAccess levelAccess;
    private final EditBox nameEdit;

    public static EditWorldScreen create(Minecraft $$0, LevelStorageSource.LevelStorageAccess $$1, BooleanConsumer $$2) throws IOException {
        LevelSummary $$3 = $$1.getSummary($$1.getDataTag());
        return new EditWorldScreen($$0, $$1, $$3.getLevelName(), $$2);
    }

    private EditWorldScreen(Minecraft $$02, LevelStorageSource.LevelStorageAccess $$12, String $$2, BooleanConsumer $$3) {
        super(Component.translatable("selectWorld.edit.title"));
        this.callback = $$3;
        this.levelAccess = $$12;
        Font $$4 = $$02.font;
        this.layout.addChild(new SpacerElement(200, 20));
        this.layout.addChild(new StringWidget(NAME_LABEL, $$4));
        this.nameEdit = this.layout.addChild(new EditBox($$4, 200, 20, NAME_LABEL));
        this.nameEdit.setValue($$2);
        LinearLayout $$5 = LinearLayout.horizontal().spacing(4);
        Button $$6 = $$5.addChild(Button.builder(SAVE_BUTTON, $$0 -> this.onRename(this.nameEdit.getValue())).width(98).build());
        $$5.addChild(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onClose()).width(98).build());
        this.nameEdit.setResponder($$1 -> {
            $$0.active = !StringUtil.isBlank($$1);
        });
        this.layout.addChild(Button.builder((Component)EditWorldScreen.RESET_ICON_BUTTON, (Button.OnPress)(Button.OnPress)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/components/Button;)V, lambda$new$4(net.minecraft.world.level.storage.LevelStorageSource$LevelStorageAccess net.minecraft.client.gui.components.Button ), (Lnet/minecraft/client/gui/components/Button;)V)((LevelStorageSource.LevelStorageAccess)$$12)).width((int)200).build()).active = $$12.getIconFile().filter($$0 -> Files.isRegularFile($$0, new LinkOption[0])).isPresent();
        this.layout.addChild(Button.builder(FOLDER_BUTTON, $$1 -> Util.getPlatform().openPath($$12.getLevelPath(LevelResource.ROOT))).width(200).build());
        this.layout.addChild(Button.builder(BACKUP_BUTTON, $$1 -> {
            boolean $$2 = EditWorldScreen.makeBackupAndShowToast($$12);
            this.callback.accept(!$$2);
        }).width(200).build());
        this.layout.addChild(Button.builder(BACKUP_FOLDER_BUTTON, $$1 -> {
            LevelStorageSource $$2 = $$02.getLevelSource();
            Path $$3 = $$2.getBackupPath();
            try {
                FileUtil.createDirectoriesSafe($$3);
            } catch (IOException $$4) {
                throw new RuntimeException($$4);
            }
            Util.getPlatform().openPath($$3);
        }).width(200).build());
        this.layout.addChild(Button.builder(OPTIMIZE_BUTTON, $$22 -> $$02.setScreen(new BackupConfirmScreen(() -> $$02.setScreen(this), ($$2, $$3) -> {
            if ($$2) {
                EditWorldScreen.makeBackupAndShowToast($$12);
            }
            $$02.setScreen(OptimizeWorldScreen.create($$02, this.callback, $$02.getFixerUpper(), $$12, $$3));
        }, OPTIMIZE_TITLE, OPTIMIIZE_DESCRIPTION, OPTIMIIZE_CONFIRMATION, true))).width(200).build());
        this.layout.addChild(new SpacerElement(200, 20));
        this.layout.addChild($$5);
        this.layout.visitWidgets($$1 -> {
            AbstractWidget cfr_ignored_0 = (AbstractWidget)this.addRenderableWidget($$1);
        });
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameEdit);
    }

    @Override
    protected void init() {
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    private void onRename(String $$0) {
        try {
            this.levelAccess.renameLevel($$0);
        } catch (IOException | NbtException | ReportedNbtException $$1) {
            LOGGER.error("Failed to access world '{}'", (Object)this.levelAccess.getLevelId(), (Object)$$1);
            SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
        }
        this.callback.accept(true);
    }

    public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess $$0) {
        long $$1 = 0L;
        IOException $$2 = null;
        try {
            $$1 = $$0.makeWorldBackup();
        } catch (IOException $$3) {
            $$2 = $$3;
        }
        if ($$2 != null) {
            MutableComponent $$4 = Component.translatable("selectWorld.edit.backupFailed");
            MutableComponent $$5 = Component.literal($$2.getMessage());
            Minecraft.getInstance().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.WORLD_BACKUP, $$4, $$5));
            return false;
        }
        MutableComponent $$6 = Component.a("selectWorld.edit.backupCreated", $$0.getLevelId());
        MutableComponent $$7 = Component.a("selectWorld.edit.backupSize", Mth.ceil((double)$$1 / 1048576.0));
        Minecraft.getInstance().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.WORLD_BACKUP, $$6, $$7));
        return true;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        $$0.drawCenteredString(this.font, this.title, this.width / 2, 15, -1);
    }

    private static /* synthetic */ void lambda$new$4(LevelStorageSource.LevelStorageAccess $$02, Button $$1) {
        $$02.getIconFile().ifPresent($$0 -> FileUtils.deleteQuietly((File)$$0.toFile()));
        $$1.active = false;
    }
}

