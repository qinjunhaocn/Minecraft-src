/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.BanDetails
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class TitleScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("narrator.screen.title");
    private static final Component COPYRIGHT_TEXT = Component.translatable("title.credits");
    private static final String DEMO_LEVEL_ID = "Demo_World";
    @Nullable
    private SplashRenderer splash;
    @Nullable
    private RealmsNotificationsScreen realmsNotificationsScreen;
    private boolean fading;
    private long fadeInStart;
    private final LogoRenderer logoRenderer;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean $$0) {
        this($$0, null);
    }

    public TitleScreen(boolean $$0, @Nullable LogoRenderer $$1) {
        super(TITLE);
        this.fading = $$0;
        this.logoRenderer = (LogoRenderer)Objects.requireNonNullElseGet((Object)$$1, () -> new LogoRenderer(false));
    }

    private boolean realmsNotificationsEnabled() {
        return this.realmsNotificationsScreen != null;
    }

    @Override
    public void tick() {
        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.tick();
        }
    }

    public static void registerTextures(TextureManager $$0) {
        $$0.registerForNextReload(LogoRenderer.MINECRAFT_LOGO);
        $$0.registerForNextReload(LogoRenderer.MINECRAFT_EDITION);
        $$0.registerForNextReload(PanoramaRenderer.PANORAMA_OVERLAY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (this.splash == null) {
            this.splash = this.minecraft.getSplashManager().getSplash();
        }
        int $$02 = this.font.width(COPYRIGHT_TEXT);
        int $$1 = this.width - $$02 - 2;
        int $$2 = 24;
        int $$3 = this.height / 4 + 48;
        $$3 = this.minecraft.isDemo() ? this.createDemoMenuOptions($$3, 24) : this.createNormalMenuOptions($$3, 24);
        $$3 = this.createTestWorldButton($$3, 24);
        SpriteIconButton $$4 = this.addRenderableWidget(CommonButtons.language(20, $$0 -> this.minecraft.setScreen(new LanguageSelectScreen((Screen)this, this.minecraft.options, this.minecraft.getLanguageManager())), true));
        $$4.setPosition(this.width / 2 - 124, $$3 += 36);
        this.addRenderableWidget(Button.builder(Component.translatable("menu.options"), $$0 -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))).bounds(this.width / 2 - 100, $$3, 98, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), $$0 -> this.minecraft.stop()).bounds(this.width / 2 + 2, $$3, 98, 20).build());
        SpriteIconButton $$5 = this.addRenderableWidget(CommonButtons.accessibility(20, $$0 -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), true));
        $$5.setPosition(this.width / 2 + 104, $$3);
        this.addRenderableWidget(new PlainTextButton($$1, this.height - 10, $$02, 10, COPYRIGHT_TEXT, $$0 -> this.minecraft.setScreen(new CreditsAndAttributionScreen(this)), this.font));
        if (this.realmsNotificationsScreen == null) {
            this.realmsNotificationsScreen = new RealmsNotificationsScreen();
        }
        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
        }
    }

    private int createTestWorldButton(int $$02, int $$1) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            this.addRenderableWidget(Button.builder(Component.literal("Create Test World"), $$0 -> CreateWorldScreen.testWorld(this.minecraft, this)).bounds(this.width / 2 - 100, $$02 += $$1, 200, 20).build());
        }
        return $$02;
    }

    private int createNormalMenuOptions(int $$02, int $$1) {
        this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), $$0 -> this.minecraft.setScreen(new SelectWorldScreen(this))).bounds(this.width / 2 - 100, $$02, 200, 20).build());
        Component $$2 = this.getMultiplayerDisabledReason();
        boolean $$3 = $$2 == null;
        Tooltip $$4 = $$2 != null ? Tooltip.create($$2) : null;
        $$02 += $$1;
        this.addRenderableWidget(Button.builder((Component)Component.translatable((String)"menu.multiplayer"), (Button.OnPress)(Button.OnPress)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/components/Button;)V, lambda$createNormalMenuOptions$8(net.minecraft.client.gui.components.Button ), (Lnet/minecraft/client/gui/components/Button;)V)((TitleScreen)this)).bounds((int)(this.width / 2 - 100), (int)v0, (int)200, (int)20).tooltip((Tooltip)$$4).build()).active = $$3;
        this.addRenderableWidget(Button.builder((Component)Component.translatable((String)"menu.online"), (Button.OnPress)(Button.OnPress)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/components/Button;)V, lambda$createNormalMenuOptions$9(net.minecraft.client.gui.components.Button ), (Lnet/minecraft/client/gui/components/Button;)V)((TitleScreen)this)).bounds((int)(this.width / 2 - 100), (int)v1, (int)200, (int)20).tooltip((Tooltip)$$4).build()).active = $$3;
        return $$02 += $$1;
    }

    @Nullable
    private Component getMultiplayerDisabledReason() {
        if (this.minecraft.allowsMultiplayer()) {
            return null;
        }
        if (this.minecraft.isNameBanned()) {
            return Component.translatable("title.multiplayer.disabled.banned.name");
        }
        BanDetails $$0 = this.minecraft.multiplayerBan();
        if ($$0 != null) {
            if ($$0.expires() != null) {
                return Component.translatable("title.multiplayer.disabled.banned.temporary");
            }
            return Component.translatable("title.multiplayer.disabled.banned.permanent");
        }
        return Component.translatable("title.multiplayer.disabled");
    }

    private int createDemoMenuOptions(int $$02, int $$12) {
        boolean $$2 = this.checkDemoWorldPresence();
        this.addRenderableWidget(Button.builder(Component.translatable("menu.playdemo"), $$1 -> {
            if ($$2) {
                this.minecraft.createWorldOpenFlows().openWorld(DEMO_LEVEL_ID, () -> this.minecraft.setScreen(this));
            } else {
                this.minecraft.createWorldOpenFlows().createFreshLevel(DEMO_LEVEL_ID, MinecraftServer.DEMO_SETTINGS, WorldOptions.DEMO_OPTIONS, WorldPresets::createNormalWorldDimensions, this);
            }
        }).bounds(this.width / 2 - 100, $$02, 200, 20).build());
        Button $$3 = this.addRenderableWidget(Button.builder(Component.translatable("menu.resetdemo"), $$0 -> {
            LevelStorageSource $$1 = this.minecraft.getLevelSource();
            try (LevelStorageSource.LevelStorageAccess $$2 = $$1.createAccess(DEMO_LEVEL_ID);){
                if ($$2.hasWorldData()) {
                    this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, Component.translatable("selectWorld.deleteQuestion"), Component.a("selectWorld.deleteWarning", MinecraftServer.DEMO_SETTINGS.levelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
                }
            } catch (IOException $$3) {
                SystemToast.onWorldAccessFailure(this.minecraft, DEMO_LEVEL_ID);
                LOGGER.warn("Failed to access demo world", $$3);
            }
        }).bounds(this.width / 2 - 100, $$02 += $$12, 200, 20).build());
        $$3.active = $$2;
        return $$02;
    }

    private boolean checkDemoWorldPresence() {
        boolean bl;
        block8: {
            LevelStorageSource.LevelStorageAccess $$0 = this.minecraft.getLevelSource().createAccess(DEMO_LEVEL_ID);
            try {
                bl = $$0.hasWorldData();
                if ($$0 == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if ($$0 != null) {
                        try {
                            $$0.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (IOException $$1) {
                    SystemToast.onWorldAccessFailure(this.minecraft, DEMO_LEVEL_ID);
                    LOGGER.warn("Failed to read demo world data", $$1);
                    return false;
                }
            }
            $$0.close();
        }
        return bl;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.fadeInStart == 0L && this.fading) {
            this.fadeInStart = Util.getMillis();
        }
        float $$4 = 1.0f;
        if (this.fading) {
            float $$5 = (float)(Util.getMillis() - this.fadeInStart) / 2000.0f;
            if ($$5 > 1.0f) {
                this.fading = false;
            } else {
                $$5 = Mth.clamp($$5, 0.0f, 1.0f);
                $$4 = Mth.clampedMap($$5, 0.5f, 1.0f, 0.0f, 1.0f);
            }
            this.fadeWidgets($$4);
        }
        this.renderPanorama($$0, $$3);
        super.render($$0, $$1, $$2, $$3);
        this.logoRenderer.renderLogo($$0, this.width, this.logoRenderer.keepLogoThroughFade() ? 1.0f : $$4);
        if (this.splash != null && !this.minecraft.options.hideSplashTexts().get().booleanValue()) {
            this.splash.render($$0, this.width, this.font, $$4);
        }
        String $$6 = "Minecraft " + SharedConstants.getCurrentVersion().name();
        $$6 = this.minecraft.isDemo() ? $$6 + " Demo" : $$6 + (String)("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
        if (Minecraft.checkModStatus().shouldReportAsModified()) {
            $$6 = $$6 + I18n.a("menu.modded", new Object[0]);
        }
        $$0.drawString(this.font, $$6, 2, this.height - 10, ARGB.color($$4, -1));
        if (this.realmsNotificationsEnabled() && $$4 >= 1.0f) {
            this.realmsNotificationsScreen.render($$0, $$1, $$2, $$3);
        }
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (super.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        return this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public void removed() {
        if (this.realmsNotificationsScreen != null) {
            this.realmsNotificationsScreen.removed();
        }
    }

    @Override
    public void added() {
        super.added();
        if (this.realmsNotificationsScreen != null) {
            this.realmsNotificationsScreen.added();
        }
    }

    private void confirmDemo(boolean $$0) {
        if ($$0) {
            try (LevelStorageSource.LevelStorageAccess $$1 = this.minecraft.getLevelSource().createAccess(DEMO_LEVEL_ID);){
                $$1.deleteLevel();
            } catch (IOException $$2) {
                SystemToast.onWorldDeleteFailure(this.minecraft, DEMO_LEVEL_ID);
                LOGGER.warn("Failed to delete demo world", $$2);
            }
        }
        this.minecraft.setScreen(this);
    }

    private /* synthetic */ void lambda$createNormalMenuOptions$9(Button $$0) {
        this.minecraft.setScreen(new RealmsMainScreen(this));
    }

    private /* synthetic */ void lambda$createNormalMenuOptions$8(Button $$0) {
        Screen $$1 = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
        this.minecraft.setScreen($$1);
    }
}

