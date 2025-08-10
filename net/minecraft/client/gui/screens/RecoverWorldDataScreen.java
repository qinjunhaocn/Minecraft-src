/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonLinks;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class RecoverWorldDataScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SCREEN_SIDE_MARGIN = 25;
    private static final Component TITLE = Component.translatable("recover_world.title").withStyle(ChatFormatting.BOLD);
    private static final Component BUGTRACKER_BUTTON = Component.translatable("recover_world.bug_tracker");
    private static final Component RESTORE_BUTTON = Component.translatable("recover_world.restore");
    private static final Component NO_FALLBACK_TOOLTIP = Component.translatable("recover_world.no_fallback");
    private static final Component DONE_TITLE = Component.translatable("recover_world.done.title");
    private static final Component DONE_SUCCESS = Component.translatable("recover_world.done.success");
    private static final Component DONE_FAILED = Component.translatable("recover_world.done.failed");
    private static final Component NO_ISSUES = Component.translatable("recover_world.issue.none").withStyle(ChatFormatting.GREEN);
    private static final Component MISSING_FILE = Component.translatable("recover_world.issue.missing_file").withStyle(ChatFormatting.RED);
    private final BooleanConsumer callback;
    private final LinearLayout layout = LinearLayout.vertical().spacing(8);
    private final Component message;
    private final MultiLineTextWidget messageWidget;
    private final MultiLineTextWidget issuesWidget;
    private final LevelStorageSource.LevelStorageAccess storageAccess;

    public RecoverWorldDataScreen(Minecraft $$02, BooleanConsumer $$1, LevelStorageSource.LevelStorageAccess $$2) {
        super(TITLE);
        this.callback = $$1;
        this.message = Component.a("recover_world.message", Component.literal($$2.getLevelId()).withStyle(ChatFormatting.GRAY));
        this.messageWidget = new MultiLineTextWidget(this.message, $$02.font);
        this.storageAccess = $$2;
        Exception $$3 = this.collectIssue($$2, false);
        Exception $$4 = this.collectIssue($$2, true);
        MutableComponent $$5 = Component.empty().append(this.buildInfo($$2, false, $$3)).append("\n").append(this.buildInfo($$2, true, $$4));
        this.issuesWidget = new MultiLineTextWidget($$5, $$02.font);
        boolean $$6 = $$3 != null && $$4 == null;
        this.layout.defaultCellSetting().alignHorizontallyCenter();
        this.layout.addChild(new StringWidget(this.title, $$02.font));
        this.layout.addChild(this.messageWidget.setCentered(true));
        this.layout.addChild(this.issuesWidget);
        LinearLayout $$7 = LinearLayout.horizontal().spacing(5);
        $$7.addChild(Button.builder(BUGTRACKER_BUTTON, ConfirmLinkScreen.confirmLink((Screen)this, CommonLinks.SNAPSHOT_BUGS_FEEDBACK)).size(120, 20).build());
        $$7.addChild(Button.builder((Component)RecoverWorldDataScreen.RESTORE_BUTTON, (Button.OnPress)(Button.OnPress)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/components/Button;)V, lambda$new$0(net.minecraft.client.Minecraft net.minecraft.client.gui.components.Button ), (Lnet/minecraft/client/gui/components/Button;)V)((RecoverWorldDataScreen)this, (Minecraft)$$02)).size((int)120, (int)20).tooltip((Tooltip)($$6 ? null : Tooltip.create((Component)RecoverWorldDataScreen.NO_FALLBACK_TOOLTIP))).build()).active = $$6;
        this.layout.addChild($$7);
        this.layout.addChild(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onClose()).size(120, 20).build());
        this.layout.visitWidgets(this::addRenderableWidget);
    }

    private void attemptRestore(Minecraft $$0) {
        Exception $$1 = this.collectIssue(this.storageAccess, false);
        Exception $$2 = this.collectIssue(this.storageAccess, true);
        if ($$1 == null || $$2 != null) {
            LOGGER.error("Failed to recover world, files not as expected. level.dat: {}, level.dat_old: {}", (Object)($$1 != null ? $$1.getMessage() : "no issues"), (Object)($$2 != null ? $$2.getMessage() : "no issues"));
            $$0.setScreen(new AlertScreen(() -> this.callback.accept(false), DONE_TITLE, DONE_FAILED));
            return;
        }
        $$0.forceSetScreen(new GenericMessageScreen(Component.translatable("recover_world.restoring")));
        EditWorldScreen.makeBackupAndShowToast(this.storageAccess);
        if (this.storageAccess.restoreLevelDataFromOld()) {
            $$0.setScreen(new ConfirmScreen(this.callback, DONE_TITLE, DONE_SUCCESS, CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
        } else {
            $$0.setScreen(new AlertScreen(() -> this.callback.accept(false), DONE_TITLE, DONE_FAILED));
        }
    }

    private Component buildInfo(LevelStorageSource.LevelStorageAccess $$0, boolean $$1, @Nullable Exception $$2) {
        if ($$1 && $$2 instanceof FileNotFoundException) {
            return Component.empty();
        }
        MutableComponent $$3 = Component.empty();
        Instant $$4 = $$0.getFileModificationTime($$1);
        MutableComponent $$5 = $$4 != null ? Component.literal(WorldSelectionList.DATE_FORMAT.format($$4)) : Component.translatable("recover_world.state_entry.unknown");
        $$3.append(Component.a("recover_world.state_entry", $$5.withStyle(ChatFormatting.GRAY)));
        if ($$2 == null) {
            $$3.append(NO_ISSUES);
        } else if ($$2 instanceof FileNotFoundException) {
            $$3.append(MISSING_FILE);
        } else if ($$2 instanceof ReportedNbtException) {
            $$3.append(Component.literal($$2.getCause().toString()).withStyle(ChatFormatting.RED));
        } else {
            $$3.append(Component.literal($$2.toString()).withStyle(ChatFormatting.RED));
        }
        return $$3;
    }

    @Nullable
    private Exception collectIssue(LevelStorageSource.LevelStorageAccess $$0, boolean $$1) {
        try {
            if (!$$1) {
                $$0.getSummary($$0.getDataTag());
            } else {
                $$0.getSummary($$0.getDataTagFallback());
            }
        } catch (IOException | NbtException | ReportedNbtException $$2) {
            return $$2;
        }
        return null;
    }

    @Override
    protected void init() {
        super.init();
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.issuesWidget.setMaxWidth(this.width - 50);
        this.messageWidget.setMaxWidth(this.width - 50);
        this.layout.arrangeElements();
        FrameLayout.centerInRectangle(this.layout, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.a(super.getNarrationMessage(), this.message);
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    private /* synthetic */ void lambda$new$0(Minecraft $$0, Button $$1) {
        this.attemptRestore($$0);
    }
}

