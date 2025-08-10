/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens.options;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.UnsupportedGraphicsWarningScreen;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VideoSettingsScreen
extends OptionsSubScreen {
    private static final Component TITLE = Component.translatable("options.videoTitle");
    private static final Component FABULOUS = Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC);
    private static final Component WARNING_MESSAGE = Component.a("options.graphics.warning.message", FABULOUS, FABULOUS);
    private static final Component WARNING_TITLE = Component.translatable("options.graphics.warning.title").withStyle(ChatFormatting.RED);
    private static final Component BUTTON_ACCEPT = Component.translatable("options.graphics.warning.accept");
    private static final Component BUTTON_CANCEL = Component.translatable("options.graphics.warning.cancel");
    private final GpuWarnlistManager gpuWarnlistManager;
    private final int oldMipmaps;

    private static OptionInstance<?>[] a(Options $$0) {
        return new OptionInstance[]{$$0.graphicsMode(), $$0.renderDistance(), $$0.prioritizeChunkUpdates(), $$0.simulationDistance(), $$0.ambientOcclusion(), $$0.framerateLimit(), $$0.enableVsync(), $$0.inactivityFpsLimit(), $$0.guiScale(), $$0.attackIndicator(), $$0.gamma(), $$0.cloudStatus(), $$0.fullscreen(), $$0.particles(), $$0.mipmapLevels(), $$0.entityShadows(), $$0.screenEffectScale(), $$0.entityDistanceScaling(), $$0.fovEffectScale(), $$0.showAutosaveIndicator(), $$0.glintSpeed(), $$0.glintStrength(), $$0.menuBackgroundBlurriness(), $$0.bobView(), $$0.cloudRange()};
    }

    public VideoSettingsScreen(Screen $$0, Minecraft $$1, Options $$2) {
        super($$0, $$2, TITLE);
        this.gpuWarnlistManager = $$1.getGpuWarnlistManager();
        this.gpuWarnlistManager.resetWarnings();
        if ($$2.graphicsMode().get() == GraphicsStatus.FABULOUS) {
            this.gpuWarnlistManager.dismissWarning();
        }
        this.oldMipmaps = $$2.mipmapLevels().get();
    }

    @Override
    protected void addOptions() {
        int $$5;
        int $$0 = -1;
        Window $$12 = this.minecraft.getWindow();
        Monitor $$22 = $$12.findBestMonitor();
        if ($$22 == null) {
            int $$3 = -1;
        } else {
            Optional<VideoMode> $$4 = $$12.getPreferredFullscreenVideoMode();
            $$5 = $$4.map($$22::getVideoModeIndex).orElse(-1);
        }
        OptionInstance<Integer> $$6 = new OptionInstance<Integer>("options.fullscreen.resolution", OptionInstance.noTooltip(), ($$1, $$2) -> {
            if ($$22 == null) {
                return Component.translatable("options.fullscreen.unavailable");
            }
            if ($$2 == -1) {
                return Options.genericValueLabel($$1, Component.translatable("options.fullscreen.current"));
            }
            VideoMode $$3 = $$22.getMode((int)$$2);
            return Options.genericValueLabel($$1, Component.a("options.fullscreen.entry", $$3.getWidth(), $$3.getHeight(), $$3.getRefreshRate(), $$3.getRedBits() + $$3.getGreenBits() + $$3.getBlueBits()));
        }, new OptionInstance.IntRange(-1, $$22 != null ? $$22.getModeCount() - 1 : -1), $$5, $$2 -> {
            if ($$22 == null) {
                return;
            }
            $$12.setPreferredFullscreenVideoMode($$2 == -1 ? Optional.empty() : Optional.of($$22.getMode((int)$$2)));
        });
        this.list.addBig($$6);
        this.list.addBig(this.options.biomeBlendRadius());
        this.list.a(VideoSettingsScreen.a(this.options));
    }

    @Override
    public void onClose() {
        this.minecraft.getWindow().changeFullscreenVideoMode();
        super.onClose();
    }

    @Override
    public void removed() {
        if (this.options.mipmapLevels().get() != this.oldMipmaps) {
            this.minecraft.updateMaxMipLevel(this.options.mipmapLevels().get());
            this.minecraft.delayTextureReload();
        }
        super.removed();
    }

    @Override
    public boolean mouseClicked(double $$02, double $$1, int $$2) {
        if (super.mouseClicked($$02, $$1, $$2)) {
            if (this.gpuWarnlistManager.isShowingWarning()) {
                String $$6;
                String $$5;
                ArrayList<Component> $$3 = Lists.newArrayList(WARNING_MESSAGE, CommonComponents.NEW_LINE);
                String $$4 = this.gpuWarnlistManager.getRendererWarnings();
                if ($$4 != null) {
                    $$3.add(CommonComponents.NEW_LINE);
                    $$3.add(Component.a("options.graphics.warning.renderer", $$4).withStyle(ChatFormatting.GRAY));
                }
                if (($$5 = this.gpuWarnlistManager.getVendorWarnings()) != null) {
                    $$3.add(CommonComponents.NEW_LINE);
                    $$3.add(Component.a("options.graphics.warning.vendor", $$5).withStyle(ChatFormatting.GRAY));
                }
                if (($$6 = this.gpuWarnlistManager.getVersionWarnings()) != null) {
                    $$3.add(CommonComponents.NEW_LINE);
                    $$3.add(Component.a("options.graphics.warning.version", $$6).withStyle(ChatFormatting.GRAY));
                }
                this.minecraft.setScreen(new UnsupportedGraphicsWarningScreen(WARNING_TITLE, $$3, ImmutableList.of(new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_ACCEPT, $$0 -> {
                    this.options.graphicsMode().set(GraphicsStatus.FABULOUS);
                    Minecraft.getInstance().levelRenderer.allChanged();
                    this.gpuWarnlistManager.dismissWarning();
                    this.minecraft.setScreen(this);
                }), new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_CANCEL, $$0 -> {
                    this.gpuWarnlistManager.dismissWarningAndSkipFabulous();
                    this.minecraft.setScreen(this);
                }))));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2, double $$3) {
        if (Screen.hasControlDown()) {
            OptionInstance<Integer> $$4 = this.options.guiScale();
            OptionInstance.ValueSet<Integer> valueSet = $$4.values();
            if (valueSet instanceof OptionInstance.ClampingLazyMaxIntRange) {
                CycleButton $$9;
                OptionInstance.ClampingLazyMaxIntRange $$5 = (OptionInstance.ClampingLazyMaxIntRange)valueSet;
                int $$6 = $$4.get();
                int $$7 = $$6 == 0 ? $$5.maxInclusive() + 1 : $$6;
                int $$8 = $$7 + (int)Math.signum($$3);
                if ($$8 != 0 && $$8 <= $$5.maxInclusive() && $$8 >= $$5.minInclusive() && ($$9 = (CycleButton)this.list.findOption($$4)) != null) {
                    $$4.set($$8);
                    $$9.setValue($$8);
                    this.list.setScrollAmount(0.0);
                    return true;
                }
            }
            return false;
        }
        return super.mouseScrolled($$0, $$1, $$2, $$3);
    }

    public void updateFullscreenButton(boolean $$0) {
        AbstractWidget $$1;
        if (this.list != null && ($$1 = this.list.findOption(this.options.fullscreen())) != null) {
            CycleButton $$2 = (CycleButton)$$1;
            $$2.setValue($$0);
        }
    }
}

