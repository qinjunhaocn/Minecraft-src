/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.InputType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundChangeGameModePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.VersionCommand;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class KeyboardHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int DEBUG_CRASH_TIME = 10000;
    private final Minecraft minecraft;
    private final ClipboardManager clipboardManager = new ClipboardManager();
    private long debugCrashKeyTime = -1L;
    private long debugCrashKeyReportedTime = -1L;
    private long debugCrashKeyReportedCount = -1L;
    private boolean handledDebugKey;

    public KeyboardHandler(Minecraft $$0) {
        this.minecraft = $$0;
    }

    private boolean handleChunkDebugKeys(int $$0) {
        switch ($$0) {
            case 69: {
                this.minecraft.sectionPath = !this.minecraft.sectionPath;
                this.a("SectionPath: {0}", this.minecraft.sectionPath ? "shown" : "hidden");
                return true;
            }
            case 76: {
                this.minecraft.smartCull = !this.minecraft.smartCull;
                this.a("SmartCull: {0}", this.minecraft.smartCull ? "enabled" : "disabled");
                return true;
            }
            case 79: {
                boolean $$1 = this.minecraft.debugRenderer.toggleRenderOctree();
                this.a("Frustum culling Octree: {0}", $$1 ? "enabled" : "disabled");
                return true;
            }
            case 70: {
                boolean $$2 = FogRenderer.toggleFog();
                this.a("Fog: {0}", $$2 ? "enabled" : "disabled");
                return true;
            }
            case 85: {
                if (Screen.hasShiftDown()) {
                    this.minecraft.levelRenderer.killFrustum();
                    this.a("Killed frustum", new Object[0]);
                } else {
                    this.minecraft.levelRenderer.captureFrustum();
                    this.a("Captured frustum", new Object[0]);
                }
                return true;
            }
            case 86: {
                this.minecraft.sectionVisibility = !this.minecraft.sectionVisibility;
                this.a("SectionVisibility: {0}", this.minecraft.sectionVisibility ? "enabled" : "disabled");
                return true;
            }
            case 87: {
                this.minecraft.wireframe = !this.minecraft.wireframe;
                this.a("WireFrame: {0}", this.minecraft.wireframe ? "enabled" : "disabled");
                return true;
            }
        }
        return false;
    }

    private void showDebugChat(Component $$0) {
        this.minecraft.gui.getChat().addMessage($$0);
        this.minecraft.getNarrator().saySystemQueued($$0);
    }

    private static Component decorateDebugComponent(ChatFormatting $$0, Component $$1) {
        return Component.empty().append(Component.translatable("debug.prefix").a($$0, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append($$1);
    }

    private void debugWarningComponent(Component $$0) {
        this.showDebugChat(KeyboardHandler.decorateDebugComponent(ChatFormatting.RED, $$0));
    }

    private void debugFeedbackComponent(Component $$0) {
        this.showDebugChat(KeyboardHandler.decorateDebugComponent(ChatFormatting.YELLOW, $$0));
    }

    private void debugFeedbackTranslated(String $$0) {
        this.debugFeedbackComponent(Component.translatable($$0));
    }

    private void a(String $$0, Object ... $$1) {
        this.debugFeedbackComponent(Component.literal(MessageFormat.format($$0, $$1)));
    }

    private boolean handleDebugKeys(int $$0) {
        if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
            return true;
        }
        switch ($$0) {
            case 65: {
                this.minecraft.levelRenderer.allChanged();
                this.debugFeedbackTranslated("debug.reload_chunks.message");
                return true;
            }
            case 66: {
                boolean $$12 = !this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
                this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes($$12);
                this.debugFeedbackTranslated($$12 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
                return true;
            }
            case 68: {
                if (this.minecraft.gui != null) {
                    this.minecraft.gui.getChat().clearMessages(false);
                }
                return true;
            }
            case 71: {
                boolean $$2 = this.minecraft.debugRenderer.switchRenderChunkborder();
                this.debugFeedbackTranslated($$2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
                return true;
            }
            case 72: {
                this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
                this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
                this.minecraft.options.save();
                return true;
            }
            case 73: {
                if (!this.minecraft.player.isReducedDebugInfo()) {
                    this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
                }
                return true;
            }
            case 78: {
                if (!this.minecraft.player.hasPermissions(2)) {
                    this.debugFeedbackTranslated("debug.creative_spectator.error");
                } else if (!this.minecraft.player.isSpectator()) {
                    this.minecraft.player.connection.send(new ServerboundChangeGameModePacket(GameType.SPECTATOR));
                } else {
                    GameType $$3 = MoreObjects.firstNonNull(this.minecraft.gameMode.getPreviousPlayerMode(), GameType.CREATIVE);
                    this.minecraft.player.connection.send(new ServerboundChangeGameModePacket($$3));
                }
                return true;
            }
            case 293: {
                if (!this.minecraft.player.hasPermissions(2)) {
                    this.debugFeedbackTranslated("debug.gamemodes.error");
                } else {
                    this.minecraft.setScreen(new GameModeSwitcherScreen());
                }
                return true;
            }
            case 80: {
                this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
                this.minecraft.options.save();
                this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
                return true;
            }
            case 81: {
                this.debugFeedbackTranslated("debug.help.message");
                this.showDebugChat(Component.translatable("debug.reload_chunks.help"));
                this.showDebugChat(Component.translatable("debug.show_hitboxes.help"));
                this.showDebugChat(Component.translatable("debug.copy_location.help"));
                this.showDebugChat(Component.translatable("debug.clear_chat.help"));
                this.showDebugChat(Component.translatable("debug.chunk_boundaries.help"));
                this.showDebugChat(Component.translatable("debug.advanced_tooltips.help"));
                this.showDebugChat(Component.translatable("debug.inspect.help"));
                this.showDebugChat(Component.translatable("debug.profiling.help"));
                this.showDebugChat(Component.translatable("debug.creative_spectator.help"));
                this.showDebugChat(Component.translatable("debug.pause_focus.help"));
                this.showDebugChat(Component.translatable("debug.help.help"));
                this.showDebugChat(Component.translatable("debug.dump_dynamic_textures.help"));
                this.showDebugChat(Component.translatable("debug.reload_resourcepacks.help"));
                this.showDebugChat(Component.translatable("debug.version.help"));
                this.showDebugChat(Component.translatable("debug.pause.help"));
                this.showDebugChat(Component.translatable("debug.gamemodes.help"));
                return true;
            }
            case 83: {
                Path $$4 = this.minecraft.gameDirectory.toPath().toAbsolutePath();
                Path $$5 = TextureUtil.getDebugTexturePath($$4);
                this.minecraft.getTextureManager().dumpAllSheets($$5);
                MutableComponent $$6 = Component.literal($$4.relativize($$5).toString()).withStyle(ChatFormatting.UNDERLINE).withStyle($$1 -> $$1.withClickEvent(new ClickEvent.OpenFile($$5)));
                this.debugFeedbackComponent(Component.a("debug.dump_dynamic_textures", $$6));
                return true;
            }
            case 84: {
                this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
                this.minecraft.reloadResourcePacks();
                return true;
            }
            case 76: {
                if (this.minecraft.debugClientMetricsStart(this::debugFeedbackComponent)) {
                    this.debugFeedbackComponent(Component.a("debug.profiling.start", 10));
                }
                return true;
            }
            case 67: {
                if (this.minecraft.player.isReducedDebugInfo()) {
                    return false;
                }
                ClientPacketListener $$7 = this.minecraft.player.connection;
                if ($$7 == null) {
                    return false;
                }
                this.debugFeedbackTranslated("debug.copy_location.message");
                this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", this.minecraft.player.level().dimension().location(), this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), Float.valueOf(this.minecraft.player.getYRot()), Float.valueOf(this.minecraft.player.getXRot())));
                return true;
            }
            case 86: {
                this.debugFeedbackTranslated("debug.version.header");
                VersionCommand.dumpVersion(this::showDebugChat);
                return true;
            }
            case 49: {
                this.minecraft.getDebugOverlay().toggleProfilerChart();
                return true;
            }
            case 50: {
                this.minecraft.getDebugOverlay().toggleFpsCharts();
                return true;
            }
            case 51: {
                this.minecraft.getDebugOverlay().toggleNetworkCharts();
                return true;
            }
        }
        return false;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void copyRecreateCommand(boolean $$0, boolean $$1) {
        HitResult $$22 = this.minecraft.hitResult;
        if ($$22 == null) {
            return;
        }
        switch ($$22.getType()) {
            case BLOCK: {
                BlockPos $$3 = ((BlockHitResult)$$22).getBlockPos();
                Level $$4 = this.minecraft.player.level();
                BlockState $$5 = $$4.getBlockState($$3);
                if (!$$0) {
                    this.copyCreateBlockCommand($$5, $$3, null);
                    this.debugFeedbackTranslated("debug.inspect.client.block");
                    return;
                }
                if ($$1) {
                    this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag($$3, $$2 -> {
                        this.copyCreateBlockCommand($$5, $$3, (CompoundTag)$$2);
                        this.debugFeedbackTranslated("debug.inspect.server.block");
                    });
                    return;
                }
                BlockEntity $$6 = $$4.getBlockEntity($$3);
                CompoundTag $$7 = $$6 != null ? $$6.saveWithoutMetadata($$4.registryAccess()) : null;
                this.copyCreateBlockCommand($$5, $$3, $$7);
                this.debugFeedbackTranslated("debug.inspect.client.block");
                return;
            }
            case ENTITY: {
                Entity $$8 = ((EntityHitResult)$$22).getEntity();
                ResourceLocation $$9 = BuiltInRegistries.ENTITY_TYPE.getKey($$8.getType());
                if (!$$0) {
                    this.copyCreateEntityCommand($$9, $$8.position(), null);
                    this.debugFeedbackTranslated("debug.inspect.client.entity");
                    return;
                }
                if ($$1) {
                    this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag($$8.getId(), $$2 -> {
                        this.copyCreateEntityCommand($$9, $$8.position(), (CompoundTag)$$2);
                        this.debugFeedbackTranslated("debug.inspect.server.entity");
                    });
                    return;
                }
                try (ProblemReporter.ScopedCollector $$10 = new ProblemReporter.ScopedCollector($$8.problemPath(), LOGGER);){
                    TagValueOutput $$11 = TagValueOutput.createWithContext($$10, $$8.registryAccess());
                    $$8.saveWithoutId($$11);
                    this.copyCreateEntityCommand($$9, $$8.position(), $$11.buildResult());
                }
                this.debugFeedbackTranslated("debug.inspect.client.entity");
                return;
            }
        }
    }

    private void copyCreateBlockCommand(BlockState $$0, BlockPos $$1, @Nullable CompoundTag $$2) {
        StringBuilder $$3 = new StringBuilder(BlockStateParser.serialize($$0));
        if ($$2 != null) {
            $$3.append($$2);
        }
        String $$4 = String.format(Locale.ROOT, "/setblock %d %d %d %s", $$1.getX(), $$1.getY(), $$1.getZ(), $$3);
        this.setClipboard($$4);
    }

    private void copyCreateEntityCommand(ResourceLocation $$0, Vec3 $$1, @Nullable CompoundTag $$2) {
        String $$5;
        if ($$2 != null) {
            $$2.remove("UUID");
            $$2.remove("Pos");
            String $$3 = NbtUtils.toPrettyComponent($$2).getString();
            String $$4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", $$0, $$1.x, $$1.y, $$1.z, $$3);
        } else {
            $$5 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", $$0, $$1.x, $$1.y, $$1.z);
        }
        this.setClipboard($$5);
    }

    public void keyPress(long $$02, int $$1, int $$2, int $$3, int $$4) {
        PauseScreen $$16;
        Screen screen;
        boolean $$17;
        Screen $$6;
        if ($$02 != this.minecraft.getWindow().getWindow()) {
            return;
        }
        this.minecraft.getFramerateLimitTracker().onInputReceived();
        boolean $$5 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292);
        if (this.debugCrashKeyTime > 0L) {
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) || !$$5) {
                this.debugCrashKeyTime = -1L;
            }
        } else if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) && $$5) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
        }
        if (($$6 = this.minecraft.screen) != null) {
            switch ($$1) {
                case 262: 
                case 263: 
                case 264: 
                case 265: {
                    this.minecraft.setLastInputType(InputType.KEYBOARD_ARROW);
                    break;
                }
                case 258: {
                    this.minecraft.setLastInputType(InputType.KEYBOARD_TAB);
                }
            }
        }
        if (!($$3 != 1 || this.minecraft.screen instanceof KeyBindsScreen && ((KeyBindsScreen)$$6).lastKeySelection > Util.getMillis() - 20L)) {
            if (this.minecraft.options.keyFullscreen.matches($$1, $$2)) {
                this.minecraft.getWindow().toggleFullScreen();
                boolean $$7 = this.minecraft.getWindow().isFullscreen();
                this.minecraft.options.fullscreen().set($$7);
                this.minecraft.options.save();
                Screen screen2 = this.minecraft.screen;
                if (screen2 instanceof VideoSettingsScreen) {
                    VideoSettingsScreen $$8 = (VideoSettingsScreen)screen2;
                    $$8.updateFullscreenButton($$7);
                }
                return;
            }
            if (this.minecraft.options.keyScreenshot.matches($$1, $$2)) {
                if (Screen.hasControlDown()) {
                    // empty if block
                }
                Screenshot.grab(this.minecraft.gameDirectory, this.minecraft.getMainRenderTarget(), $$0 -> this.minecraft.execute(() -> this.showDebugChat((Component)$$0)));
                return;
            }
        }
        if ($$3 != 0) {
            boolean $$9;
            boolean bl = $$9 = $$6 == null || !($$6.getFocused() instanceof EditBox) || !((EditBox)$$6.getFocused()).canConsumeInput();
            if ($$9) {
                if (Screen.hasControlDown() && $$1 == 66 && this.minecraft.getNarrator().isActive() && this.minecraft.options.narratorHotkey().get().booleanValue()) {
                    boolean $$10 = this.minecraft.options.narrator().get() == NarratorStatus.OFF;
                    this.minecraft.options.narrator().set(NarratorStatus.byId(this.minecraft.options.narrator().get().getId() + 1));
                    this.minecraft.options.save();
                    if ($$6 != null) {
                        $$6.updateNarratorStatus($$10);
                    }
                }
                LocalPlayer $$10 = this.minecraft.player;
            }
        }
        if ($$6 != null) {
            try {
                if ($$3 == 1 || $$3 == 2) {
                    $$6.afterKeyboardAction();
                    if ($$6.keyPressed($$1, $$2, $$4)) {
                        return;
                    }
                } else if ($$3 == 0 && $$6.keyReleased($$1, $$2, $$4)) {
                    return;
                }
            } catch (Throwable $$11) {
                CrashReport $$12 = CrashReport.forThrowable($$11, "keyPressed event handler");
                $$6.fillCrashDetails($$12);
                CrashReportCategory $$13 = $$12.addCategory("Key");
                $$13.setDetail("Key", $$1);
                $$13.setDetail("Scancode", $$2);
                $$13.setDetail("Mods", $$4);
                throw new ReportedException($$12);
            }
        }
        InputConstants.Key $$14 = InputConstants.getKey($$1, $$2);
        boolean $$15 = this.minecraft.screen == null;
        boolean bl = $$17 = $$15 || (screen = this.minecraft.screen) instanceof PauseScreen && !($$16 = (PauseScreen)screen).showsPauseMenu();
        if ($$3 == 0) {
            KeyMapping.set($$14, false);
            if ($$17 && $$1 == 292) {
                if (this.handledDebugKey) {
                    this.handledDebugKey = false;
                } else {
                    this.minecraft.getDebugOverlay().toggleOverlay();
                }
            }
            return;
        }
        boolean $$18 = false;
        if ($$17) {
            if ($$1 == 293 && this.minecraft.gameRenderer != null) {
                this.minecraft.gameRenderer.togglePostEffect();
            }
            if ($$1 == 256) {
                this.minecraft.pauseGame($$5);
                $$18 |= $$5;
            }
            this.handledDebugKey |= ($$18 |= $$5 && this.handleDebugKeys($$1));
            if ($$1 == 290) {
                boolean bl2 = this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
            }
            if (this.minecraft.getDebugOverlay().showProfilerChart() && !$$5 && $$1 >= 48 && $$1 <= 57) {
                this.minecraft.getDebugOverlay().getProfilerPieChart().profilerPieChartKeyPress($$1 - 48);
            }
        }
        if ($$15) {
            if ($$18) {
                KeyMapping.set($$14, false);
            } else {
                KeyMapping.set($$14, true);
                KeyMapping.click($$14);
            }
        }
    }

    private void charTyped(long $$0, int $$1, int $$2) {
        if ($$0 != this.minecraft.getWindow().getWindow()) {
            return;
        }
        Screen $$3 = this.minecraft.screen;
        if ($$3 == null || this.minecraft.getOverlay() != null) {
            return;
        }
        try {
            if (Character.isBmpCodePoint($$1)) {
                $$3.a((char)$$1, $$2);
            } else if (Character.isValidCodePoint($$1)) {
                $$3.a(Character.highSurrogate($$1), $$2);
                $$3.a(Character.lowSurrogate($$1), $$2);
            }
        } catch (Throwable $$4) {
            CrashReport $$5 = CrashReport.forThrowable($$4, "charTyped event handler");
            $$3.fillCrashDetails($$5);
            CrashReportCategory $$6 = $$5.addCategory("Key");
            $$6.setDetail("Codepoint", $$1);
            $$6.setDetail("Mods", $$2);
            throw new ReportedException($$5);
        }
    }

    public void setup(long $$02) {
        InputConstants.setupKeyboardCallbacks($$02, ($$0, $$1, $$2, $$3, $$4) -> this.minecraft.execute(() -> this.keyPress($$0, $$1, $$2, $$3, $$4)), ($$0, $$1, $$2) -> this.minecraft.execute(() -> this.charTyped($$0, $$1, $$2)));
    }

    public String getClipboard() {
        return this.clipboardManager.getClipboard(this.minecraft.getWindow().getWindow(), ($$0, $$1) -> {
            if ($$0 != 65545) {
                this.minecraft.getWindow().defaultErrorCallback($$0, $$1);
            }
        });
    }

    public void setClipboard(String $$0) {
        if (!$$0.isEmpty()) {
            this.clipboardManager.setClipboard(this.minecraft.getWindow().getWindow(), $$0);
        }
    }

    public void tick() {
        if (this.debugCrashKeyTime > 0L) {
            long $$0 = Util.getMillis();
            long $$1 = 10000L - ($$0 - this.debugCrashKeyTime);
            long $$2 = $$0 - this.debugCrashKeyReportedTime;
            if ($$1 < 0L) {
                if (Screen.hasControlDown()) {
                    Blaze3D.youJustLostTheGame();
                }
                String $$3 = "Manually triggered debug crash";
                CrashReport $$4 = new CrashReport("Manually triggered debug crash", new Throwable("Manually triggered debug crash"));
                CrashReportCategory $$5 = $$4.addCategory("Manual crash details");
                NativeModuleLister.addCrashSection($$5);
                throw new ReportedException($$4);
            }
            if ($$2 >= 1000L) {
                if (this.debugCrashKeyReportedCount == 0L) {
                    this.debugFeedbackTranslated("debug.crash.message");
                } else {
                    this.debugWarningComponent(Component.a("debug.crash.warning", Mth.ceil((float)$$1 / 1000.0f)));
                }
                this.debugCrashKeyReportedTime = $$0;
                ++this.debugCrashKeyReportedCount;
            }
        }
    }
}

