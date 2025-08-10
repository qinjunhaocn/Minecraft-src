/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.lang.MatchException
 */
package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.CameraType;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.InactivityFpsLimit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class Options {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> LIST_OF_STRINGS_TYPE = new TypeToken<List<String>>(){};
    public static final int RENDER_DISTANCE_TINY = 2;
    public static final int RENDER_DISTANCE_SHORT = 4;
    public static final int RENDER_DISTANCE_NORMAL = 8;
    public static final int RENDER_DISTANCE_FAR = 12;
    public static final int RENDER_DISTANCE_REALLY_FAR = 16;
    public static final int RENDER_DISTANCE_EXTREME = 32;
    private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
    public static final String DEFAULT_SOUND_DEVICE = "";
    private static final Component ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND = Component.translatable("options.darkMojangStudiosBackgroundColor.tooltip");
    private final OptionInstance<Boolean> darkMojangStudiosBackground = OptionInstance.createBoolean("options.darkMojangStudiosBackgroundColor", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND), false);
    private static final Component ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES = Component.translatable("options.hideLightningFlashes.tooltip");
    private final OptionInstance<Boolean> hideLightningFlash = OptionInstance.createBoolean("options.hideLightningFlashes", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES), false);
    private static final Component ACCESSIBILITY_TOOLTIP_HIDE_SPLASH_TEXTS = Component.translatable("options.hideSplashTexts.tooltip");
    private final OptionInstance<Boolean> hideSplashTexts = OptionInstance.createBoolean("options.hideSplashTexts", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_SPLASH_TEXTS), false);
    private final OptionInstance<Double> sensitivity = new OptionInstance<Double>("options.sensitivity", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return Options.genericValueLabel($$0, Component.translatable("options.sensitivity.min"));
        }
        if ($$1 == 1.0) {
            return Options.genericValueLabel($$0, Component.translatable("options.sensitivity.max"));
        }
        return Options.percentValueLabel($$0, 2.0 * $$1);
    }, OptionInstance.UnitDouble.INSTANCE, 0.5, $$0 -> {});
    private final OptionInstance<Integer> renderDistance;
    private final OptionInstance<Integer> simulationDistance;
    private int serverRenderDistance = 0;
    private final OptionInstance<Double> entityDistanceScaling = new OptionInstance<Double>("options.entityDistanceScaling", OptionInstance.noTooltip(), Options::percentValueLabel, new OptionInstance.IntRange(2, 20).xmap($$0 -> (double)$$0 / 4.0, $$0 -> (int)($$0 * 4.0)), Codec.doubleRange((double)0.5, (double)5.0), 1.0, $$0 -> {});
    public static final int UNLIMITED_FRAMERATE_CUTOFF = 260;
    private final OptionInstance<Integer> framerateLimit = new OptionInstance<Integer>("options.framerateLimit", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 == 260) {
            return Options.genericValueLabel($$0, Component.translatable("options.framerateLimit.max"));
        }
        return Options.genericValueLabel($$0, Component.a("options.framerate", $$1));
    }, new OptionInstance.IntRange(1, 26).xmap($$0 -> $$0 * 10, $$0 -> $$0 / 10), Codec.intRange((int)10, (int)260), 120, $$0 -> Minecraft.getInstance().getFramerateLimitTracker().setFramerateLimit((int)$$0));
    private static final Component INACTIVITY_FPS_LIMIT_TOOLTIP_MINIMIZED = Component.translatable("options.inactivityFpsLimit.minimized.tooltip");
    private static final Component INACTIVITY_FPS_LIMIT_TOOLTIP_AFK = Component.translatable("options.inactivityFpsLimit.afk.tooltip");
    private final OptionInstance<InactivityFpsLimit> inactivityFpsLimit = new OptionInstance<InactivityFpsLimit>("options.inactivityFpsLimit", $$0 -> switch ($$0) {
        default -> throw new MatchException(null, null);
        case InactivityFpsLimit.MINIMIZED -> Tooltip.create(INACTIVITY_FPS_LIMIT_TOOLTIP_MINIMIZED);
        case InactivityFpsLimit.AFK -> Tooltip.create(INACTIVITY_FPS_LIMIT_TOOLTIP_AFK);
    }, OptionInstance.forOptionEnum(), new OptionInstance.Enum<InactivityFpsLimit>(Arrays.asList(InactivityFpsLimit.values()), InactivityFpsLimit.CODEC), InactivityFpsLimit.AFK, $$0 -> {});
    private final OptionInstance<CloudStatus> cloudStatus = new OptionInstance<CloudStatus>("options.renderClouds", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum<CloudStatus>(Arrays.asList(CloudStatus.values()), Codec.withAlternative(CloudStatus.CODEC, (Codec)Codec.BOOL, $$0 -> $$0 != false ? CloudStatus.FANCY : CloudStatus.OFF)), CloudStatus.FANCY, $$0 -> {});
    private final OptionInstance<Integer> cloudRange = new OptionInstance<Integer>("options.renderCloudsDistance", OptionInstance.noTooltip(), ($$0, $$1) -> Options.genericValueLabel($$0, Component.a("options.chunks", $$1)), new OptionInstance.IntRange(2, 128, true), 128, $$0 -> Minecraft.getInstance().levelRenderer.getCloudRenderer().markForRebuild());
    private static final Component GRAPHICS_TOOLTIP_FAST = Component.translatable("options.graphics.fast.tooltip");
    private static final Component GRAPHICS_TOOLTIP_FABULOUS = Component.a("options.graphics.fabulous.tooltip", Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC));
    private static final Component GRAPHICS_TOOLTIP_FANCY = Component.translatable("options.graphics.fancy.tooltip");
    private final OptionInstance<GraphicsStatus> graphicsMode = new OptionInstance<GraphicsStatus>("options.graphics", $$0 -> switch ($$0) {
        default -> throw new MatchException(null, null);
        case GraphicsStatus.FANCY -> Tooltip.create(GRAPHICS_TOOLTIP_FANCY);
        case GraphicsStatus.FAST -> Tooltip.create(GRAPHICS_TOOLTIP_FAST);
        case GraphicsStatus.FABULOUS -> Tooltip.create(GRAPHICS_TOOLTIP_FABULOUS);
    }, ($$0, $$1) -> {
        MutableComponent $$2 = Component.translatable($$1.getKey());
        if ($$1 == GraphicsStatus.FABULOUS) {
            return $$2.withStyle(ChatFormatting.ITALIC);
        }
        return $$2;
    }, new OptionInstance.AltEnum<GraphicsStatus>(Arrays.asList(GraphicsStatus.values()), Stream.of(GraphicsStatus.values()).filter($$0 -> $$0 != GraphicsStatus.FABULOUS).collect(Collectors.toList()), () -> Minecraft.getInstance().isRunning() && Minecraft.getInstance().getGpuWarnlistManager().isSkippingFabulous(), ($$0, $$1) -> {
        Minecraft $$2 = Minecraft.getInstance();
        GpuWarnlistManager $$3 = $$2.getGpuWarnlistManager();
        if ($$1 == GraphicsStatus.FABULOUS && $$3.willShowWarning()) {
            $$3.showWarning();
            return;
        }
        $$0.set($$1);
        $$2.levelRenderer.allChanged();
    }, Codec.INT.xmap(GraphicsStatus::byId, GraphicsStatus::getId)), GraphicsStatus.FANCY, $$0 -> {});
    private final OptionInstance<Boolean> ambientOcclusion = OptionInstance.createBoolean("options.ao", true, $$0 -> Minecraft.getInstance().levelRenderer.allChanged());
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_NONE = Component.translatable("options.prioritizeChunkUpdates.none.tooltip");
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED = Component.translatable("options.prioritizeChunkUpdates.byPlayer.tooltip");
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_NEARBY = Component.translatable("options.prioritizeChunkUpdates.nearby.tooltip");
    private final OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates = new OptionInstance<PrioritizeChunkUpdates>("options.prioritizeChunkUpdates", $$0 -> switch ($$0) {
        default -> throw new MatchException(null, null);
        case PrioritizeChunkUpdates.NONE -> Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NONE);
        case PrioritizeChunkUpdates.PLAYER_AFFECTED -> Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED);
        case PrioritizeChunkUpdates.NEARBY -> Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NEARBY);
    }, OptionInstance.forOptionEnum(), new OptionInstance.Enum<PrioritizeChunkUpdates>(Arrays.asList(PrioritizeChunkUpdates.values()), Codec.INT.xmap(PrioritizeChunkUpdates::byId, PrioritizeChunkUpdates::getId)), PrioritizeChunkUpdates.NONE, $$0 -> {});
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    private final OptionInstance<ChatVisiblity> chatVisibility = new OptionInstance<ChatVisiblity>("options.chat.visibility", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum<ChatVisiblity>(Arrays.asList(ChatVisiblity.values()), Codec.INT.xmap(ChatVisiblity::byId, ChatVisiblity::getId)), ChatVisiblity.FULL, $$0 -> {});
    private final OptionInstance<Double> chatOpacity = new OptionInstance<Double>("options.chat.opacity", OptionInstance.noTooltip(), ($$0, $$1) -> Options.percentValueLabel($$0, $$1 * 0.9 + 0.1), OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatLineSpacing = new OptionInstance<Double>("options.chat.line_spacing", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.0, $$0 -> {});
    private static final Component MENU_BACKGROUND_BLURRINESS_TOOLTIP = Component.translatable("options.accessibility.menu_background_blurriness.tooltip");
    private static final int BLURRINESS_DEFAULT_VALUE = 5;
    private final OptionInstance<Integer> menuBackgroundBlurriness = new OptionInstance<Integer>("options.accessibility.menu_background_blurriness", OptionInstance.cachedConstantTooltip(MENU_BACKGROUND_BLURRINESS_TOOLTIP), Options::genericValueOrOffLabel, new OptionInstance.IntRange(0, 10), 5, $$0 -> {});
    private final OptionInstance<Double> textBackgroundOpacity = new OptionInstance<Double>("options.accessibility.text_background_opacity", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.5, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> panoramaSpeed = new OptionInstance<Double>("options.accessibility.panorama_speed", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_CONTRAST_MODE = Component.translatable("options.accessibility.high_contrast.tooltip");
    private final OptionInstance<Boolean> highContrast = OptionInstance.createBoolean("options.accessibility.high_contrast", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_CONTRAST_MODE), false, $$0 -> {
        PackRepository $$1 = Minecraft.getInstance().getResourcePackRepository();
        boolean $$2 = $$1.getSelectedIds().contains("high_contrast");
        if (!$$2 && $$0.booleanValue()) {
            if ($$1.addPack("high_contrast")) {
                this.updateResourcePacks($$1);
            }
        } else if ($$2 && !$$0.booleanValue() && $$1.removePack("high_contrast")) {
            this.updateResourcePacks($$1);
        }
    });
    private static final Component HIGH_CONTRAST_BLOCK_OUTLINE_TOOLTIP = Component.translatable("options.accessibility.high_contrast_block_outline.tooltip");
    private final OptionInstance<Boolean> highContrastBlockOutline = OptionInstance.createBoolean("options.accessibility.high_contrast_block_outline", OptionInstance.cachedConstantTooltip(HIGH_CONTRAST_BLOCK_OUTLINE_TOOLTIP), false);
    private final OptionInstance<Boolean> narratorHotkey = OptionInstance.createBoolean("options.accessibility.narrator_hotkey", OptionInstance.cachedConstantTooltip(Minecraft.ON_OSX ? Component.translatable("options.accessibility.narrator_hotkey.mac.tooltip") : Component.translatable("options.accessibility.narrator_hotkey.tooltip")), true);
    @Nullable
    public String fullscreenVideoModeString;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<PlayerModelPart> modelParts = EnumSet.allOf(PlayerModelPart.class);
    private final OptionInstance<HumanoidArm> mainHand = new OptionInstance<HumanoidArm>("options.mainHand", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum<HumanoidArm>(Arrays.asList(HumanoidArm.values()), HumanoidArm.CODEC), HumanoidArm.RIGHT, $$0 -> {});
    public int overrideWidth;
    public int overrideHeight;
    private final OptionInstance<Double> chatScale = new OptionInstance<Double>("options.chat.scale", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return CommonComponents.optionStatus($$0, false);
        }
        return Options.percentValueLabel($$0, $$1);
    }, OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatWidth = new OptionInstance<Double>("options.chat.width", OptionInstance.noTooltip(), ($$0, $$1) -> Options.pixelValueLabel($$0, ChatComponent.getWidth($$1)), OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatHeightUnfocused = new OptionInstance<Double>("options.chat.height.unfocused", OptionInstance.noTooltip(), ($$0, $$1) -> Options.pixelValueLabel($$0, ChatComponent.getHeight($$1)), OptionInstance.UnitDouble.INSTANCE, ChatComponent.defaultUnfocusedPct(), $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatHeightFocused = new OptionInstance<Double>("options.chat.height.focused", OptionInstance.noTooltip(), ($$0, $$1) -> Options.pixelValueLabel($$0, ChatComponent.getHeight($$1)), OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatDelay = new OptionInstance<Double>("options.chat.delay_instant", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 <= 0.0) {
            return Component.translatable("options.chat.delay_none");
        }
        return Component.a("options.chat.delay", String.format(Locale.ROOT, "%.1f", $$1));
    }, new OptionInstance.IntRange(0, 60).xmap($$0 -> (double)$$0 / 10.0, $$0 -> (int)($$0 * 10.0)), Codec.doubleRange((double)0.0, (double)6.0), 0.0, $$0 -> Minecraft.getInstance().getChatListener().setMessageDelay((double)$$0));
    private static final Component ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME = Component.translatable("options.notifications.display_time.tooltip");
    private final OptionInstance<Double> notificationDisplayTime = new OptionInstance<Double>("options.notifications.display_time", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME), ($$0, $$1) -> Options.genericValueLabel($$0, Component.a("options.multiplier", $$1)), new OptionInstance.IntRange(5, 100).xmap($$0 -> (double)$$0 / 10.0, $$0 -> (int)($$0 * 10.0)), Codec.doubleRange((double)0.5, (double)10.0), 1.0, $$0 -> {});
    private final OptionInstance<Integer> mipmapLevels = new OptionInstance<Integer>("options.mipmapLevels", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 == 0) {
            return CommonComponents.optionStatus($$0, false);
        }
        return Options.genericValueLabel($$0, $$1);
    }, new OptionInstance.IntRange(0, 4), 4, $$0 -> {});
    public boolean useNativeTransport = true;
    private final OptionInstance<AttackIndicatorStatus> attackIndicator = new OptionInstance<AttackIndicatorStatus>("options.attackIndicator", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum<AttackIndicatorStatus>(Arrays.asList(AttackIndicatorStatus.values()), Codec.INT.xmap(AttackIndicatorStatus::byId, AttackIndicatorStatus::getId)), AttackIndicatorStatus.CROSSHAIR, $$0 -> {});
    public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
    public boolean joinedFirstServer = false;
    private final OptionInstance<Integer> biomeBlendRadius = new OptionInstance<Integer>("options.biomeBlendRadius", OptionInstance.noTooltip(), ($$0, $$1) -> {
        int $$2 = $$1 * 2 + 1;
        return Options.genericValueLabel($$0, Component.translatable("options.biomeBlendRadius." + $$2));
    }, new OptionInstance.IntRange(0, 7, false), 2, $$0 -> Minecraft.getInstance().levelRenderer.allChanged());
    private final OptionInstance<Double> mouseWheelSensitivity = new OptionInstance<Double>("options.mouseWheelSensitivity", OptionInstance.noTooltip(), ($$0, $$1) -> Options.genericValueLabel($$0, Component.literal(String.format(Locale.ROOT, "%.2f", $$1))), new OptionInstance.IntRange(-200, 100).xmap(Options::logMouse, Options::unlogMouse), Codec.doubleRange((double)Options.logMouse(-200), (double)Options.logMouse(100)), Options.logMouse(0), $$0 -> {});
    private final OptionInstance<Boolean> rawMouseInput = OptionInstance.createBoolean("options.rawMouseInput", true, $$0 -> {
        Window $$1 = Minecraft.getInstance().getWindow();
        if ($$1 != null) {
            $$1.updateRawMouseInput((boolean)$$0);
        }
    });
    public int glDebugVerbosity = 1;
    private final OptionInstance<Boolean> autoJump = OptionInstance.createBoolean("options.autoJump", false);
    private static final Component ACCESSIBILITY_TOOLTIP_ROTATE_WITH_MINECART = Component.translatable("options.rotateWithMinecart.tooltip");
    private final OptionInstance<Boolean> rotateWithMinecart = OptionInstance.createBoolean("options.rotateWithMinecart", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_ROTATE_WITH_MINECART), false);
    private final OptionInstance<Boolean> operatorItemsTab = OptionInstance.createBoolean("options.operatorItemsTab", false);
    private final OptionInstance<Boolean> autoSuggestions = OptionInstance.createBoolean("options.autoSuggestCommands", true);
    private final OptionInstance<Boolean> chatColors = OptionInstance.createBoolean("options.chat.color", true);
    private final OptionInstance<Boolean> chatLinks = OptionInstance.createBoolean("options.chat.links", true);
    private final OptionInstance<Boolean> chatLinksPrompt = OptionInstance.createBoolean("options.chat.links.prompt", true);
    private final OptionInstance<Boolean> enableVsync = OptionInstance.createBoolean("options.vsync", true, $$0 -> {
        if (Minecraft.getInstance().getWindow() != null) {
            Minecraft.getInstance().getWindow().updateVsync((boolean)$$0);
        }
    });
    private final OptionInstance<Boolean> entityShadows = OptionInstance.createBoolean("options.entityShadows", true);
    private final OptionInstance<Boolean> forceUnicodeFont = OptionInstance.createBoolean("options.forceUnicodeFont", false, $$0 -> Options.updateFontOptions());
    private final OptionInstance<Boolean> japaneseGlyphVariants = OptionInstance.createBoolean("options.japaneseGlyphVariants", OptionInstance.cachedConstantTooltip(Component.translatable("options.japaneseGlyphVariants.tooltip")), Options.japaneseGlyphVariantsDefault(), $$0 -> Options.updateFontOptions());
    private final OptionInstance<Boolean> invertYMouse = OptionInstance.createBoolean("options.invertMouse", false);
    private final OptionInstance<Boolean> discreteMouseScroll = OptionInstance.createBoolean("options.discrete_mouse_scroll", false);
    private static final Component REALMS_NOTIFICATIONS_TOOLTIP = Component.translatable("options.realmsNotifications.tooltip");
    private final OptionInstance<Boolean> realmsNotifications = OptionInstance.createBoolean("options.realmsNotifications", OptionInstance.cachedConstantTooltip(REALMS_NOTIFICATIONS_TOOLTIP), true);
    private static final Component ALLOW_SERVER_LISTING_TOOLTIP = Component.translatable("options.allowServerListing.tooltip");
    private final OptionInstance<Boolean> allowServerListing = OptionInstance.createBoolean("options.allowServerListing", OptionInstance.cachedConstantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, $$0 -> {});
    private final OptionInstance<Boolean> reducedDebugInfo = OptionInstance.createBoolean("options.reducedDebugInfo", false);
    private final Map<SoundSource, OptionInstance<Double>> soundSourceVolumes = Util.makeEnumMap(SoundSource.class, $$0 -> this.createSoundSliderOptionInstance("soundCategory." + $$0.getName(), (SoundSource)((Object)$$0)));
    private final OptionInstance<Boolean> showSubtitles = OptionInstance.createBoolean("options.showSubtitles", false);
    private static final Component DIRECTIONAL_AUDIO_TOOLTIP_ON = Component.translatable("options.directionalAudio.on.tooltip");
    private static final Component DIRECTIONAL_AUDIO_TOOLTIP_OFF = Component.translatable("options.directionalAudio.off.tooltip");
    private final OptionInstance<Boolean> directionalAudio = OptionInstance.createBoolean("options.directionalAudio", $$0 -> $$0 != false ? Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_ON) : Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_OFF), false, $$0 -> {
        SoundManager $$1 = Minecraft.getInstance().getSoundManager();
        $$1.reload();
        $$1.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    });
    private final OptionInstance<Boolean> backgroundForChatOnly = new OptionInstance<Boolean>("options.accessibility.text_background", OptionInstance.noTooltip(), ($$0, $$1) -> $$1 != false ? Component.translatable("options.accessibility.text_background.chat") : Component.translatable("options.accessibility.text_background.everywhere"), OptionInstance.BOOLEAN_VALUES, true, $$0 -> {});
    private final OptionInstance<Boolean> touchscreen = OptionInstance.createBoolean("options.touchscreen", false);
    private final OptionInstance<Boolean> fullscreen = OptionInstance.createBoolean("options.fullscreen", false, $$0 -> {
        Minecraft $$1 = Minecraft.getInstance();
        if ($$1.getWindow() != null && $$1.getWindow().isFullscreen() != $$0.booleanValue()) {
            $$1.getWindow().toggleFullScreen();
            this.fullscreen().set($$1.getWindow().isFullscreen());
        }
    });
    private final OptionInstance<Boolean> bobView = OptionInstance.createBoolean("options.viewBobbing", true);
    private static final Component MOVEMENT_TOGGLE = Component.translatable("options.key.toggle");
    private static final Component MOVEMENT_HOLD = Component.translatable("options.key.hold");
    private final OptionInstance<Boolean> toggleCrouch = new OptionInstance<Boolean>("key.sneak", OptionInstance.noTooltip(), ($$0, $$1) -> $$1 != false ? MOVEMENT_TOGGLE : MOVEMENT_HOLD, OptionInstance.BOOLEAN_VALUES, false, $$0 -> {});
    private final OptionInstance<Boolean> toggleSprint = new OptionInstance<Boolean>("key.sprint", OptionInstance.noTooltip(), ($$0, $$1) -> $$1 != false ? MOVEMENT_TOGGLE : MOVEMENT_HOLD, OptionInstance.BOOLEAN_VALUES, false, $$0 -> {});
    public boolean skipMultiplayerWarning;
    private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES = Component.translatable("options.hideMatchedNames.tooltip");
    private final OptionInstance<Boolean> hideMatchedNames = OptionInstance.createBoolean("options.hideMatchedNames", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_HIDE_MATCHED_NAMES), true);
    private final OptionInstance<Boolean> showAutosaveIndicator = OptionInstance.createBoolean("options.autosaveIndicator", true);
    private static final Component CHAT_TOOLTIP_ONLY_SHOW_SECURE = Component.translatable("options.onlyShowSecureChat.tooltip");
    private final OptionInstance<Boolean> onlyShowSecureChat = OptionInstance.createBoolean("options.onlyShowSecureChat", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_ONLY_SHOW_SECURE), false);
    public final KeyMapping keyUp = new KeyMapping("key.forward", 87, "key.categories.movement");
    public final KeyMapping keyLeft = new KeyMapping("key.left", 65, "key.categories.movement");
    public final KeyMapping keyDown = new KeyMapping("key.back", 83, "key.categories.movement");
    public final KeyMapping keyRight = new KeyMapping("key.right", 68, "key.categories.movement");
    public final KeyMapping keyJump = new KeyMapping("key.jump", 32, "key.categories.movement");
    public final KeyMapping keyShift = new ToggleKeyMapping("key.sneak", 340, "key.categories.movement", this.toggleCrouch::get);
    public final KeyMapping keySprint = new ToggleKeyMapping("key.sprint", 341, "key.categories.movement", this.toggleSprint::get);
    public final KeyMapping keyInventory = new KeyMapping("key.inventory", 69, "key.categories.inventory");
    public final KeyMapping keySwapOffhand = new KeyMapping("key.swapOffhand", 70, "key.categories.inventory");
    public final KeyMapping keyDrop = new KeyMapping("key.drop", 81, "key.categories.inventory");
    public final KeyMapping keyUse = new KeyMapping("key.use", InputConstants.Type.MOUSE, 1, "key.categories.gameplay");
    public final KeyMapping keyAttack = new KeyMapping("key.attack", InputConstants.Type.MOUSE, 0, "key.categories.gameplay");
    public final KeyMapping keyPickItem = new KeyMapping("key.pickItem", InputConstants.Type.MOUSE, 2, "key.categories.gameplay");
    public final KeyMapping keyChat = new KeyMapping("key.chat", 84, "key.categories.multiplayer");
    public final KeyMapping keyPlayerList = new KeyMapping("key.playerlist", 258, "key.categories.multiplayer");
    public final KeyMapping keyCommand = new KeyMapping("key.command", 47, "key.categories.multiplayer");
    public final KeyMapping keySocialInteractions = new KeyMapping("key.socialInteractions", 80, "key.categories.multiplayer");
    public final KeyMapping keyScreenshot = new KeyMapping("key.screenshot", 291, "key.categories.misc");
    public final KeyMapping keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, "key.categories.misc");
    public final KeyMapping keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
    public final KeyMapping keyFullscreen = new KeyMapping("key.fullscreen", 300, "key.categories.misc");
    public final KeyMapping keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
    public final KeyMapping keyAdvancements = new KeyMapping("key.advancements", 76, "key.categories.misc");
    public final KeyMapping keyQuickActions = new KeyMapping("key.quickActions", 71, "key.categories.misc");
    public final KeyMapping[] keyHotbarSlots = new KeyMapping[]{new KeyMapping("key.hotbar.1", 49, "key.categories.inventory"), new KeyMapping("key.hotbar.2", 50, "key.categories.inventory"), new KeyMapping("key.hotbar.3", 51, "key.categories.inventory"), new KeyMapping("key.hotbar.4", 52, "key.categories.inventory"), new KeyMapping("key.hotbar.5", 53, "key.categories.inventory"), new KeyMapping("key.hotbar.6", 54, "key.categories.inventory"), new KeyMapping("key.hotbar.7", 55, "key.categories.inventory"), new KeyMapping("key.hotbar.8", 56, "key.categories.inventory"), new KeyMapping("key.hotbar.9", 57, "key.categories.inventory")};
    public final KeyMapping keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, "key.categories.creative");
    public final KeyMapping keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, "key.categories.creative");
    public final KeyMapping[] keyMappings = ArrayUtils.addAll(new KeyMapping[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapOffhand, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements, this.keyQuickActions}, this.keyHotbarSlots);
    protected Minecraft minecraft;
    private final File optionsFile;
    public boolean hideGui;
    private CameraType cameraType = CameraType.FIRST_PERSON;
    public String lastMpIp = "";
    public boolean smoothCamera;
    private final OptionInstance<Integer> fov = new OptionInstance<Integer>("options.fov", OptionInstance.noTooltip(), ($$0, $$1) -> switch ($$1) {
        case 70 -> Options.genericValueLabel($$0, Component.translatable("options.fov.min"));
        case 110 -> Options.genericValueLabel($$0, Component.translatable("options.fov.max"));
        default -> Options.genericValueLabel($$0, $$1);
    }, new OptionInstance.IntRange(30, 110), Codec.DOUBLE.xmap($$0 -> (int)($$0 * 40.0 + 70.0), $$0 -> ((double)$$0.intValue() - 70.0) / 40.0), 70, $$0 -> Minecraft.getInstance().levelRenderer.needsUpdate());
    private static final Component TELEMETRY_TOOLTIP = Component.a("options.telemetry.button.tooltip", Component.translatable("options.telemetry.state.minimal"), Component.translatable("options.telemetry.state.all"));
    private final OptionInstance<Boolean> telemetryOptInExtra = OptionInstance.createBoolean("options.telemetry.button", OptionInstance.cachedConstantTooltip(TELEMETRY_TOOLTIP), ($$0, $$1) -> {
        Minecraft $$2 = Minecraft.getInstance();
        if (!$$2.allowsTelemetry()) {
            return Component.translatable("options.telemetry.state.none");
        }
        if ($$1.booleanValue() && $$2.extraTelemetryAvailable()) {
            return Component.translatable("options.telemetry.state.all");
        }
        return Component.translatable("options.telemetry.state.minimal");
    }, false, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = Component.translatable("options.screenEffectScale.tooltip");
    private final OptionInstance<Double> screenEffectScale = new OptionInstance<Double>("options.screenEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT = Component.translatable("options.fovEffectScale.tooltip");
    private final OptionInstance<Double> fovEffectScale = new OptionInstance<Double>("options.fovEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_FOV_EFFECT), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), Codec.doubleRange((double)0.0, (double)1.0), 1.0, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT = Component.translatable("options.darknessEffectScale.tooltip");
    private final OptionInstance<Double> darknessEffectScale = new OptionInstance<Double>("options.darknessEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), 1.0, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_GLINT_SPEED = Component.translatable("options.glintSpeed.tooltip");
    private final OptionInstance<Double> glintSpeed = new OptionInstance<Double>("options.glintSpeed", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_SPEED), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 0.5, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH = Component.translatable("options.glintStrength.tooltip");
    private final OptionInstance<Double> glintStrength = new OptionInstance<Double>("options.glintStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 0.75, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH = Component.translatable("options.damageTiltStrength.tooltip");
    private final OptionInstance<Double> damageTiltStrength = new OptionInstance<Double>("options.damageTiltStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> {});
    private final OptionInstance<Double> gamma = new OptionInstance<Double>("options.gamma", OptionInstance.noTooltip(), ($$0, $$1) -> {
        int $$2 = (int)($$1 * 100.0);
        if ($$2 == 0) {
            return Options.genericValueLabel($$0, Component.translatable("options.gamma.min"));
        }
        if ($$2 == 50) {
            return Options.genericValueLabel($$0, Component.translatable("options.gamma.default"));
        }
        if ($$2 == 100) {
            return Options.genericValueLabel($$0, Component.translatable("options.gamma.max"));
        }
        return Options.genericValueLabel($$0, $$2);
    }, OptionInstance.UnitDouble.INSTANCE, 0.5, $$0 -> {});
    public static final int AUTO_GUI_SCALE = 0;
    private static final int MAX_GUI_SCALE_INCLUSIVE = 0x7FFFFFFE;
    private final OptionInstance<Integer> guiScale = new OptionInstance<Integer>("options.guiScale", OptionInstance.noTooltip(), ($$0, $$1) -> $$1 == 0 ? Component.translatable("options.guiScale.auto") : Component.literal(Integer.toString($$1)), new OptionInstance.ClampingLazyMaxIntRange(0, () -> {
        Minecraft $$0 = Minecraft.getInstance();
        if (!$$0.isRunning()) {
            return 0x7FFFFFFE;
        }
        return $$0.getWindow().calculateScale(0, $$0.isEnforceUnicode());
    }, 0x7FFFFFFE), 0, $$0 -> this.minecraft.resizeDisplay());
    private final OptionInstance<ParticleStatus> particles = new OptionInstance<ParticleStatus>("options.particles", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum<ParticleStatus>(Arrays.asList(ParticleStatus.values()), Codec.INT.xmap(ParticleStatus::byId, ParticleStatus::getId)), ParticleStatus.ALL, $$0 -> {});
    private final OptionInstance<NarratorStatus> narrator = new OptionInstance<NarratorStatus>("options.narrator", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if (this.minecraft.getNarrator().isActive()) {
            return $$1.getName();
        }
        return Component.translatable("options.narrator.notavailable");
    }, new OptionInstance.Enum<NarratorStatus>(Arrays.asList(NarratorStatus.values()), Codec.INT.xmap(NarratorStatus::byId, NarratorStatus::getId)), NarratorStatus.OFF, $$0 -> this.minecraft.getNarrator().updateNarratorStatus((NarratorStatus)((Object)$$0)));
    public String languageCode = "en_us";
    private final OptionInstance<String> soundDevice = new OptionInstance<String>("options.audioDevice", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if (DEFAULT_SOUND_DEVICE.equals($$1)) {
            return Component.translatable("options.audioDevice.default");
        }
        if ($$1.startsWith("OpenAL Soft on ")) {
            return Component.literal($$1.substring(SoundEngine.OPEN_AL_SOFT_PREFIX_LENGTH));
        }
        return Component.literal($$1);
    }, new OptionInstance.LazyEnum<String>(() -> Stream.concat(Stream.of(DEFAULT_SOUND_DEVICE), Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().stream()).toList(), (Function<String, Optional<String>>)((Function<String, Optional>)$$0 -> {
        if (!Minecraft.getInstance().isRunning() || $$0 == DEFAULT_SOUND_DEVICE || Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().contains($$0)) {
            return Optional.of($$0);
        }
        return Optional.empty();
    }), (Codec<String>)Codec.STRING), "", $$0 -> {
        SoundManager $$1 = Minecraft.getInstance().getSoundManager();
        $$1.reload();
        $$1.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    });
    public boolean onboardAccessibility = true;
    private static final Component MUSIC_FREQUENCY_TOOLTIP = Component.translatable("options.music_frequency.tooltip");
    private final OptionInstance<MusicManager.MusicFrequency> musicFrequency = new OptionInstance<MusicManager.MusicFrequency>("options.music_frequency", OptionInstance.cachedConstantTooltip(MUSIC_FREQUENCY_TOOLTIP), OptionInstance.forOptionEnum(), new OptionInstance.Enum<MusicManager.MusicFrequency>(Arrays.asList(MusicManager.MusicFrequency.values()), MusicManager.MusicFrequency.CODEC), MusicManager.MusicFrequency.DEFAULT, $$0 -> Minecraft.getInstance().getMusicManager().setMinutesBetweenSongs((MusicManager.MusicFrequency)$$0));
    private static final Component NOW_PLAYING_TOAST_TOOLTIP = Component.translatable("options.showNowPlayingToast.tooltip");
    private final OptionInstance<Boolean> showNowPlayingToast = OptionInstance.createBoolean("options.showNowPlayingToast", OptionInstance.cachedConstantTooltip(NOW_PLAYING_TOAST_TOOLTIP), false, $$0 -> {
        if ($$0.booleanValue()) {
            this.minecraft.getToastManager().createNowPlayingToast();
        } else {
            this.minecraft.getToastManager().removeNowPlayingToast();
        }
    });
    public boolean syncWrites;
    public boolean startedCleanly = true;

    public OptionInstance<Boolean> darkMojangStudiosBackground() {
        return this.darkMojangStudiosBackground;
    }

    public OptionInstance<Boolean> hideLightningFlash() {
        return this.hideLightningFlash;
    }

    public OptionInstance<Boolean> hideSplashTexts() {
        return this.hideSplashTexts;
    }

    public OptionInstance<Double> sensitivity() {
        return this.sensitivity;
    }

    public OptionInstance<Integer> renderDistance() {
        return this.renderDistance;
    }

    public OptionInstance<Integer> simulationDistance() {
        return this.simulationDistance;
    }

    public OptionInstance<Double> entityDistanceScaling() {
        return this.entityDistanceScaling;
    }

    public OptionInstance<Integer> framerateLimit() {
        return this.framerateLimit;
    }

    public OptionInstance<InactivityFpsLimit> inactivityFpsLimit() {
        return this.inactivityFpsLimit;
    }

    public OptionInstance<CloudStatus> cloudStatus() {
        return this.cloudStatus;
    }

    public OptionInstance<Integer> cloudRange() {
        return this.cloudRange;
    }

    public OptionInstance<GraphicsStatus> graphicsMode() {
        return this.graphicsMode;
    }

    public OptionInstance<Boolean> ambientOcclusion() {
        return this.ambientOcclusion;
    }

    public OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates() {
        return this.prioritizeChunkUpdates;
    }

    public void updateResourcePacks(PackRepository $$0) {
        ImmutableList<String> $$1 = ImmutableList.copyOf(this.resourcePacks);
        this.resourcePacks.clear();
        this.incompatibleResourcePacks.clear();
        for (Pack $$2 : $$0.getSelectedPacks()) {
            if ($$2.isFixedPosition()) continue;
            this.resourcePacks.add($$2.getId());
            if ($$2.getCompatibility().isCompatible()) continue;
            this.incompatibleResourcePacks.add($$2.getId());
        }
        this.save();
        ImmutableList<String> $$3 = ImmutableList.copyOf(this.resourcePacks);
        if (!$$3.equals($$1)) {
            this.minecraft.reloadResourcePacks();
        }
    }

    public OptionInstance<ChatVisiblity> chatVisibility() {
        return this.chatVisibility;
    }

    public OptionInstance<Double> chatOpacity() {
        return this.chatOpacity;
    }

    public OptionInstance<Double> chatLineSpacing() {
        return this.chatLineSpacing;
    }

    public OptionInstance<Integer> menuBackgroundBlurriness() {
        return this.menuBackgroundBlurriness;
    }

    public int getMenuBackgroundBlurriness() {
        return this.menuBackgroundBlurriness().get();
    }

    public OptionInstance<Double> textBackgroundOpacity() {
        return this.textBackgroundOpacity;
    }

    public OptionInstance<Double> panoramaSpeed() {
        return this.panoramaSpeed;
    }

    public OptionInstance<Boolean> highContrast() {
        return this.highContrast;
    }

    public OptionInstance<Boolean> highContrastBlockOutline() {
        return this.highContrastBlockOutline;
    }

    public OptionInstance<Boolean> narratorHotkey() {
        return this.narratorHotkey;
    }

    public OptionInstance<HumanoidArm> mainHand() {
        return this.mainHand;
    }

    public OptionInstance<Double> chatScale() {
        return this.chatScale;
    }

    public OptionInstance<Double> chatWidth() {
        return this.chatWidth;
    }

    public OptionInstance<Double> chatHeightUnfocused() {
        return this.chatHeightUnfocused;
    }

    public OptionInstance<Double> chatHeightFocused() {
        return this.chatHeightFocused;
    }

    public OptionInstance<Double> chatDelay() {
        return this.chatDelay;
    }

    public OptionInstance<Double> notificationDisplayTime() {
        return this.notificationDisplayTime;
    }

    public OptionInstance<Integer> mipmapLevels() {
        return this.mipmapLevels;
    }

    public OptionInstance<AttackIndicatorStatus> attackIndicator() {
        return this.attackIndicator;
    }

    public OptionInstance<Integer> biomeBlendRadius() {
        return this.biomeBlendRadius;
    }

    private static double logMouse(int $$0) {
        return Math.pow(10.0, (double)$$0 / 100.0);
    }

    private static int unlogMouse(double $$0) {
        return Mth.floor(Math.log10($$0) * 100.0);
    }

    public OptionInstance<Double> mouseWheelSensitivity() {
        return this.mouseWheelSensitivity;
    }

    public OptionInstance<Boolean> rawMouseInput() {
        return this.rawMouseInput;
    }

    public OptionInstance<Boolean> autoJump() {
        return this.autoJump;
    }

    public OptionInstance<Boolean> rotateWithMinecart() {
        return this.rotateWithMinecart;
    }

    public OptionInstance<Boolean> operatorItemsTab() {
        return this.operatorItemsTab;
    }

    public OptionInstance<Boolean> autoSuggestions() {
        return this.autoSuggestions;
    }

    public OptionInstance<Boolean> chatColors() {
        return this.chatColors;
    }

    public OptionInstance<Boolean> chatLinks() {
        return this.chatLinks;
    }

    public OptionInstance<Boolean> chatLinksPrompt() {
        return this.chatLinksPrompt;
    }

    public OptionInstance<Boolean> enableVsync() {
        return this.enableVsync;
    }

    public OptionInstance<Boolean> entityShadows() {
        return this.entityShadows;
    }

    private static void updateFontOptions() {
        Minecraft $$0 = Minecraft.getInstance();
        if ($$0.getWindow() != null) {
            $$0.updateFontOptions();
            $$0.resizeDisplay();
        }
    }

    public OptionInstance<Boolean> forceUnicodeFont() {
        return this.forceUnicodeFont;
    }

    private static boolean japaneseGlyphVariantsDefault() {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("ja");
    }

    public OptionInstance<Boolean> japaneseGlyphVariants() {
        return this.japaneseGlyphVariants;
    }

    public OptionInstance<Boolean> invertYMouse() {
        return this.invertYMouse;
    }

    public OptionInstance<Boolean> discreteMouseScroll() {
        return this.discreteMouseScroll;
    }

    public OptionInstance<Boolean> realmsNotifications() {
        return this.realmsNotifications;
    }

    public OptionInstance<Boolean> allowServerListing() {
        return this.allowServerListing;
    }

    public OptionInstance<Boolean> reducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public final float getFinalSoundSourceVolume(SoundSource $$0) {
        if ($$0 == SoundSource.MASTER) {
            return this.getSoundSourceVolume($$0);
        }
        return this.getSoundSourceVolume($$0) * this.getSoundSourceVolume(SoundSource.MASTER);
    }

    public final float getSoundSourceVolume(SoundSource $$0) {
        return this.getSoundSourceOptionInstance($$0).get().floatValue();
    }

    public final OptionInstance<Double> getSoundSourceOptionInstance(SoundSource $$0) {
        return Objects.requireNonNull(this.soundSourceVolumes.get((Object)$$0));
    }

    private OptionInstance<Double> createSoundSliderOptionInstance(String $$0, SoundSource $$12) {
        return new OptionInstance<Double>($$0, OptionInstance.noTooltip(), Options::percentValueOrOffLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, $$1 -> Minecraft.getInstance().getSoundManager().updateSourceVolume($$12, $$1.floatValue()));
    }

    public OptionInstance<Boolean> showSubtitles() {
        return this.showSubtitles;
    }

    public OptionInstance<Boolean> directionalAudio() {
        return this.directionalAudio;
    }

    public OptionInstance<Boolean> backgroundForChatOnly() {
        return this.backgroundForChatOnly;
    }

    public OptionInstance<Boolean> touchscreen() {
        return this.touchscreen;
    }

    public OptionInstance<Boolean> fullscreen() {
        return this.fullscreen;
    }

    public OptionInstance<Boolean> bobView() {
        return this.bobView;
    }

    public OptionInstance<Boolean> toggleCrouch() {
        return this.toggleCrouch;
    }

    public OptionInstance<Boolean> toggleSprint() {
        return this.toggleSprint;
    }

    public OptionInstance<Boolean> hideMatchedNames() {
        return this.hideMatchedNames;
    }

    public OptionInstance<Boolean> showAutosaveIndicator() {
        return this.showAutosaveIndicator;
    }

    public OptionInstance<Boolean> onlyShowSecureChat() {
        return this.onlyShowSecureChat;
    }

    public OptionInstance<Integer> fov() {
        return this.fov;
    }

    public OptionInstance<Boolean> telemetryOptInExtra() {
        return this.telemetryOptInExtra;
    }

    public OptionInstance<Double> screenEffectScale() {
        return this.screenEffectScale;
    }

    public OptionInstance<Double> fovEffectScale() {
        return this.fovEffectScale;
    }

    public OptionInstance<Double> darknessEffectScale() {
        return this.darknessEffectScale;
    }

    public OptionInstance<Double> glintSpeed() {
        return this.glintSpeed;
    }

    public OptionInstance<Double> glintStrength() {
        return this.glintStrength;
    }

    public OptionInstance<Double> damageTiltStrength() {
        return this.damageTiltStrength;
    }

    public OptionInstance<Double> gamma() {
        return this.gamma;
    }

    public OptionInstance<Integer> guiScale() {
        return this.guiScale;
    }

    public OptionInstance<ParticleStatus> particles() {
        return this.particles;
    }

    public OptionInstance<NarratorStatus> narrator() {
        return this.narrator;
    }

    public OptionInstance<String> soundDevice() {
        return this.soundDevice;
    }

    public void onboardingAccessibilityFinished() {
        this.onboardAccessibility = false;
        this.save();
    }

    public OptionInstance<MusicManager.MusicFrequency> musicFrequency() {
        return this.musicFrequency;
    }

    public OptionInstance<Boolean> showNowPlayingToast() {
        return this.showNowPlayingToast;
    }

    public Options(Minecraft $$02, File $$12) {
        this.minecraft = $$02;
        this.optionsFile = new File($$12, "options.txt");
        boolean $$2 = Runtime.getRuntime().maxMemory() >= 1000000000L;
        this.renderDistance = new OptionInstance<Integer>("options.renderDistance", OptionInstance.noTooltip(), ($$0, $$1) -> Options.genericValueLabel($$0, Component.a("options.chunks", $$1)), new OptionInstance.IntRange(2, $$2 ? 32 : 16, false), 12, $$0 -> Minecraft.getInstance().levelRenderer.needsUpdate());
        this.simulationDistance = new OptionInstance<Integer>("options.simulationDistance", OptionInstance.noTooltip(), ($$0, $$1) -> Options.genericValueLabel($$0, Component.a("options.chunks", $$1)), new OptionInstance.IntRange(5, $$2 ? 32 : 16, false), 12, $$0 -> {});
        this.syncWrites = Util.getPlatform() == Util.OS.WINDOWS;
        this.load();
    }

    public float getBackgroundOpacity(float $$0) {
        return this.backgroundForChatOnly.get() != false ? $$0 : this.textBackgroundOpacity().get().floatValue();
    }

    public int getBackgroundColor(float $$0) {
        return ARGB.colorFromFloat(this.getBackgroundOpacity($$0), 0.0f, 0.0f, 0.0f);
    }

    public int getBackgroundColor(int $$0) {
        return this.backgroundForChatOnly.get() != false ? $$0 : ARGB.colorFromFloat(this.textBackgroundOpacity.get().floatValue(), 0.0f, 0.0f, 0.0f);
    }

    private void processDumpedOptions(OptionAccess $$0) {
        $$0.process("ao", this.ambientOcclusion);
        $$0.process("biomeBlendRadius", this.biomeBlendRadius);
        $$0.process("enableVsync", this.enableVsync);
        $$0.process("entityDistanceScaling", this.entityDistanceScaling);
        $$0.process("entityShadows", this.entityShadows);
        $$0.process("forceUnicodeFont", this.forceUnicodeFont);
        $$0.process("japaneseGlyphVariants", this.japaneseGlyphVariants);
        $$0.process("fov", this.fov);
        $$0.process("fovEffectScale", this.fovEffectScale);
        $$0.process("darknessEffectScale", this.darknessEffectScale);
        $$0.process("glintSpeed", this.glintSpeed);
        $$0.process("glintStrength", this.glintStrength);
        $$0.process("prioritizeChunkUpdates", this.prioritizeChunkUpdates);
        $$0.process("fullscreen", this.fullscreen);
        $$0.process("gamma", this.gamma);
        $$0.process("graphicsMode", this.graphicsMode);
        $$0.process("guiScale", this.guiScale);
        $$0.process("maxFps", this.framerateLimit);
        $$0.process("inactivityFpsLimit", this.inactivityFpsLimit);
        $$0.process("mipmapLevels", this.mipmapLevels);
        $$0.process("narrator", this.narrator);
        $$0.process("particles", this.particles);
        $$0.process("reducedDebugInfo", this.reducedDebugInfo);
        $$0.process("renderClouds", this.cloudStatus);
        $$0.process("cloudRange", this.cloudRange);
        $$0.process("renderDistance", this.renderDistance);
        $$0.process("simulationDistance", this.simulationDistance);
        $$0.process("screenEffectScale", this.screenEffectScale);
        $$0.process("soundDevice", this.soundDevice);
    }

    private void processOptions(FieldAccess $$0) {
        this.processDumpedOptions($$0);
        $$0.process("autoJump", this.autoJump);
        $$0.process("rotateWithMinecart", this.rotateWithMinecart);
        $$0.process("operatorItemsTab", this.operatorItemsTab);
        $$0.process("autoSuggestions", this.autoSuggestions);
        $$0.process("chatColors", this.chatColors);
        $$0.process("chatLinks", this.chatLinks);
        $$0.process("chatLinksPrompt", this.chatLinksPrompt);
        $$0.process("discrete_mouse_scroll", this.discreteMouseScroll);
        $$0.process("invertYMouse", this.invertYMouse);
        $$0.process("realmsNotifications", this.realmsNotifications);
        $$0.process("showSubtitles", this.showSubtitles);
        $$0.process("directionalAudio", this.directionalAudio);
        $$0.process("touchscreen", this.touchscreen);
        $$0.process("bobView", this.bobView);
        $$0.process("toggleCrouch", this.toggleCrouch);
        $$0.process("toggleSprint", this.toggleSprint);
        $$0.process("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
        $$0.process("hideLightningFlashes", this.hideLightningFlash);
        $$0.process("hideSplashTexts", this.hideSplashTexts);
        $$0.process("mouseSensitivity", this.sensitivity);
        $$0.process("damageTiltStrength", this.damageTiltStrength);
        $$0.process("highContrast", this.highContrast);
        $$0.process("highContrastBlockOutline", this.highContrastBlockOutline);
        $$0.process("narratorHotkey", this.narratorHotkey);
        this.resourcePacks = $$0.process("resourcePacks", this.resourcePacks, Options::readListOfStrings, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.incompatibleResourcePacks = $$0.process("incompatibleResourcePacks", this.incompatibleResourcePacks, Options::readListOfStrings, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.lastMpIp = $$0.process("lastServer", this.lastMpIp);
        this.languageCode = $$0.process("lang", this.languageCode);
        $$0.process("chatVisibility", this.chatVisibility);
        $$0.process("chatOpacity", this.chatOpacity);
        $$0.process("chatLineSpacing", this.chatLineSpacing);
        $$0.process("textBackgroundOpacity", this.textBackgroundOpacity);
        $$0.process("backgroundForChatOnly", this.backgroundForChatOnly);
        this.hideServerAddress = $$0.process("hideServerAddress", this.hideServerAddress);
        this.advancedItemTooltips = $$0.process("advancedItemTooltips", this.advancedItemTooltips);
        this.pauseOnLostFocus = $$0.process("pauseOnLostFocus", this.pauseOnLostFocus);
        this.overrideWidth = $$0.process("overrideWidth", this.overrideWidth);
        this.overrideHeight = $$0.process("overrideHeight", this.overrideHeight);
        $$0.process("chatHeightFocused", this.chatHeightFocused);
        $$0.process("chatDelay", this.chatDelay);
        $$0.process("chatHeightUnfocused", this.chatHeightUnfocused);
        $$0.process("chatScale", this.chatScale);
        $$0.process("chatWidth", this.chatWidth);
        $$0.process("notificationDisplayTime", this.notificationDisplayTime);
        this.useNativeTransport = $$0.process("useNativeTransport", this.useNativeTransport);
        $$0.process("mainHand", this.mainHand);
        $$0.process("attackIndicator", this.attackIndicator);
        this.tutorialStep = $$0.process("tutorialStep", this.tutorialStep, TutorialSteps::getByName, TutorialSteps::getName);
        $$0.process("mouseWheelSensitivity", this.mouseWheelSensitivity);
        $$0.process("rawMouseInput", this.rawMouseInput);
        this.glDebugVerbosity = $$0.process("glDebugVerbosity", this.glDebugVerbosity);
        this.skipMultiplayerWarning = $$0.process("skipMultiplayerWarning", this.skipMultiplayerWarning);
        $$0.process("hideMatchedNames", this.hideMatchedNames);
        this.joinedFirstServer = $$0.process("joinedFirstServer", this.joinedFirstServer);
        this.syncWrites = $$0.process("syncChunkWrites", this.syncWrites);
        $$0.process("showAutosaveIndicator", this.showAutosaveIndicator);
        $$0.process("allowServerListing", this.allowServerListing);
        $$0.process("onlyShowSecureChat", this.onlyShowSecureChat);
        $$0.process("panoramaScrollSpeed", this.panoramaSpeed);
        $$0.process("telemetryOptInExtra", this.telemetryOptInExtra);
        this.onboardAccessibility = $$0.process("onboardAccessibility", this.onboardAccessibility);
        $$0.process("menuBackgroundBlurriness", this.menuBackgroundBlurriness);
        this.startedCleanly = $$0.process("startedCleanly", this.startedCleanly);
        $$0.process("showNowPlayingToast", this.showNowPlayingToast);
        $$0.process("musicFrequency", this.musicFrequency);
        for (KeyMapping keyMapping : this.keyMappings) {
            String $$3;
            String $$2 = keyMapping.saveString();
            if ($$2.equals($$3 = $$0.process("key_" + keyMapping.getName(), $$2))) continue;
            keyMapping.setKey(InputConstants.getKey($$3));
        }
        for (SoundSource soundSource : SoundSource.values()) {
            $$0.process("soundCategory_" + soundSource.getName(), this.soundSourceVolumes.get((Object)soundSource));
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            boolean $$6 = this.modelParts.contains((Object)playerModelPart);
            boolean $$7 = $$0.process("modelPart_" + playerModelPart.getId(), $$6);
            if ($$7 == $$6) continue;
            this.setModelPart(playerModelPart, $$7);
        }
    }

    public void load() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            CompoundTag $$02 = new CompoundTag();
            try (BufferedReader $$12 = Files.newReader(this.optionsFile, Charsets.UTF_8);){
                $$12.lines().forEach($$1 -> {
                    try {
                        Iterator<String> $$2 = OPTION_SPLITTER.split((CharSequence)$$1).iterator();
                        $$02.putString($$2.next(), $$2.next());
                    } catch (Exception $$3) {
                        LOGGER.warn("Skipping bad option: {}", $$1);
                    }
                });
            }
            final CompoundTag $$2 = this.dataFix($$02);
            Optional<String> $$3 = $$2.getString("fancyGraphics");
            if ($$3.isPresent() && !$$2.contains("graphicsMode")) {
                this.graphicsMode.set(Options.isTrue($$3.get()) ? GraphicsStatus.FANCY : GraphicsStatus.FAST);
            }
            this.processOptions(new FieldAccess(){

                /*
                 * Enabled force condition propagation
                 * Lifted jumps to return sites
                 */
                @Nullable
                private String getValue(String $$0) {
                    Tag $$1 = $$2.get($$0);
                    if ($$1 == null) {
                        return null;
                    }
                    if (!($$1 instanceof StringTag)) throw new IllegalStateException("Cannot read field of wrong type, expected string: " + String.valueOf($$1));
                    StringTag stringTag = (StringTag)$$1;
                    try {
                        String string = stringTag.value();
                        return string;
                    } catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                }

                @Override
                public <T> void process(String $$0, OptionInstance<T> $$1) {
                    String $$22 = this.getValue($$0);
                    if ($$22 != null) {
                        JsonElement $$3 = LenientJsonParser.parse($$22.isEmpty() ? "\"\"" : $$22);
                        $$1.codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)$$3).ifError($$2 -> LOGGER.error("Error parsing option value {} for option {}: {}", $$22, $$1, $$2.message())).ifSuccess($$1::set);
                    }
                }

                @Override
                public int process(String $$0, int $$1) {
                    String $$22 = this.getValue($$0);
                    if ($$22 != null) {
                        try {
                            return Integer.parseInt($$22);
                        } catch (NumberFormatException $$3) {
                            LOGGER.warn("Invalid integer value for option {} = {}", $$0, $$22, $$3);
                        }
                    }
                    return $$1;
                }

                @Override
                public boolean process(String $$0, boolean $$1) {
                    String $$22 = this.getValue($$0);
                    return $$22 != null ? Options.isTrue($$22) : $$1;
                }

                @Override
                public String process(String $$0, String $$1) {
                    return MoreObjects.firstNonNull(this.getValue($$0), $$1);
                }

                @Override
                public float process(String $$0, float $$1) {
                    String $$22 = this.getValue($$0);
                    if ($$22 != null) {
                        if (Options.isTrue($$22)) {
                            return 1.0f;
                        }
                        if (Options.isFalse($$22)) {
                            return 0.0f;
                        }
                        try {
                            return Float.parseFloat($$22);
                        } catch (NumberFormatException $$3) {
                            LOGGER.warn("Invalid floating point value for option {} = {}", $$0, $$22, $$3);
                        }
                    }
                    return $$1;
                }

                @Override
                public <T> T process(String $$0, T $$1, Function<String, T> $$22, Function<T, String> $$3) {
                    String $$4 = this.getValue($$0);
                    return $$4 == null ? $$1 : $$22.apply($$4);
                }
            });
            $$2.getString("fullscreenResolution").ifPresent($$0 -> {
                this.fullscreenVideoModeString = $$0;
            });
            KeyMapping.resetMapping();
        } catch (Exception $$4) {
            LOGGER.error("Failed to load options", $$4);
        }
    }

    static boolean isTrue(String $$0) {
        return "true".equals($$0);
    }

    static boolean isFalse(String $$0) {
        return "false".equals($$0);
    }

    private CompoundTag dataFix(CompoundTag $$0) {
        int $$1 = 0;
        try {
            $$1 = $$0.getString("version").map(Integer::parseInt).orElse(0);
        } catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return DataFixTypes.OPTIONS.updateToCurrentVersion(this.minecraft.getFixerUpper(), $$0, $$1);
    }

    public void save() {
        try (final PrintWriter $$0 = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));){
            $$0.println("version:" + SharedConstants.getCurrentVersion().dataVersion().version());
            this.processOptions(new FieldAccess(){

                public void writePrefix(String $$02) {
                    $$0.print($$02);
                    $$0.print(':');
                }

                @Override
                public <T> void process(String $$02, OptionInstance<T> $$12) {
                    $$12.codec().encodeStart((DynamicOps)JsonOps.INSTANCE, $$12.get()).ifError($$1 -> LOGGER.error("Error saving option " + String.valueOf($$12) + ": " + String.valueOf($$1))).ifSuccess($$2 -> {
                        this.writePrefix($$02);
                        $$0.println(GSON.toJson($$2));
                    });
                }

                @Override
                public int process(String $$02, int $$1) {
                    this.writePrefix($$02);
                    $$0.println($$1);
                    return $$1;
                }

                @Override
                public boolean process(String $$02, boolean $$1) {
                    this.writePrefix($$02);
                    $$0.println($$1);
                    return $$1;
                }

                @Override
                public String process(String $$02, String $$1) {
                    this.writePrefix($$02);
                    $$0.println($$1);
                    return $$1;
                }

                @Override
                public float process(String $$02, float $$1) {
                    this.writePrefix($$02);
                    $$0.println($$1);
                    return $$1;
                }

                @Override
                public <T> T process(String $$02, T $$1, Function<String, T> $$2, Function<T, String> $$3) {
                    this.writePrefix($$02);
                    $$0.println($$3.apply($$1));
                    return $$1;
                }
            });
            String $$1 = this.getFullscreenVideoModeString();
            if ($$1 != null) {
                $$0.println("fullscreenResolution:" + $$1);
            }
        } catch (Exception $$2) {
            LOGGER.error("Failed to save options", $$2);
        }
        this.broadcastOptions();
    }

    @Nullable
    private String getFullscreenVideoModeString() {
        Window $$0 = this.minecraft.getWindow();
        if ($$0 == null) {
            return this.fullscreenVideoModeString;
        }
        if ($$0.getPreferredFullscreenVideoMode().isPresent()) {
            return $$0.getPreferredFullscreenVideoMode().get().write();
        }
        return null;
    }

    public ClientInformation buildPlayerInformation() {
        int $$0 = 0;
        for (PlayerModelPart $$1 : this.modelParts) {
            $$0 |= $$1.getMask();
        }
        return new ClientInformation(this.languageCode, this.renderDistance.get(), this.chatVisibility.get(), this.chatColors.get(), $$0, this.mainHand.get(), this.minecraft.isTextFilteringEnabled(), this.allowServerListing.get(), this.particles.get());
    }

    public void broadcastOptions() {
        if (this.minecraft.player != null) {
            this.minecraft.player.connection.broadcastClientInformation(this.buildPlayerInformation());
        }
    }

    public void setModelPart(PlayerModelPart $$0, boolean $$1) {
        if ($$1) {
            this.modelParts.add($$0);
        } else {
            this.modelParts.remove((Object)$$0);
        }
    }

    public boolean isModelPartEnabled(PlayerModelPart $$0) {
        return this.modelParts.contains((Object)$$0);
    }

    public CloudStatus getCloudsType() {
        return this.cloudStatus.get();
    }

    public boolean useNativeTransport() {
        return this.useNativeTransport;
    }

    public void loadSelectedResourcePacks(PackRepository $$0) {
        LinkedHashSet<String> $$1 = Sets.newLinkedHashSet();
        Iterator<String> $$2 = this.resourcePacks.iterator();
        while ($$2.hasNext()) {
            String $$3 = $$2.next();
            Pack $$4 = $$0.getPack($$3);
            if ($$4 == null && !$$3.startsWith("file/")) {
                $$4 = $$0.getPack("file/" + $$3);
            }
            if ($$4 == null) {
                LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)$$3);
                $$2.remove();
                continue;
            }
            if (!$$4.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains($$3)) {
                LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)$$3);
                $$2.remove();
                continue;
            }
            if ($$4.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains($$3)) {
                LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)$$3);
                this.incompatibleResourcePacks.remove($$3);
                continue;
            }
            $$1.add($$4.getId());
        }
        $$0.setSelected($$1);
    }

    public CameraType getCameraType() {
        return this.cameraType;
    }

    public void setCameraType(CameraType $$0) {
        this.cameraType = $$0;
    }

    private static List<String> readListOfStrings(String $$0) {
        List<String> $$1 = GsonHelper.fromNullableJson(GSON, $$0, LIST_OF_STRINGS_TYPE);
        return $$1 != null ? $$1 : Lists.newArrayList();
    }

    public File getFile() {
        return this.optionsFile;
    }

    public String dumpOptionsForReport() {
        final ArrayList<Pair> $$02 = new ArrayList<Pair>();
        this.processDumpedOptions(new OptionAccess(){

            @Override
            public <T> void process(String $$0, OptionInstance<T> $$1) {
                $$02.add(Pair.of((Object)$$0, $$1.get()));
            }
        });
        $$02.add(Pair.of((Object)"fullscreenResolution", (Object)String.valueOf(this.fullscreenVideoModeString)));
        $$02.add(Pair.of((Object)"glDebugVerbosity", (Object)this.glDebugVerbosity));
        $$02.add(Pair.of((Object)"overrideHeight", (Object)this.overrideHeight));
        $$02.add(Pair.of((Object)"overrideWidth", (Object)this.overrideWidth));
        $$02.add(Pair.of((Object)"syncChunkWrites", (Object)this.syncWrites));
        $$02.add(Pair.of((Object)"useNativeTransport", (Object)this.useNativeTransport));
        $$02.add(Pair.of((Object)"resourcePacks", this.resourcePacks));
        return $$02.stream().sorted(Comparator.comparing(Pair::getFirst)).map($$0 -> (String)$$0.getFirst() + ": " + String.valueOf($$0.getSecond())).collect(Collectors.joining(System.lineSeparator()));
    }

    public void setServerRenderDistance(int $$0) {
        this.serverRenderDistance = $$0;
    }

    public int getEffectiveRenderDistance() {
        return this.serverRenderDistance > 0 ? Math.min(this.renderDistance.get(), this.serverRenderDistance) : this.renderDistance.get();
    }

    private static Component pixelValueLabel(Component $$0, int $$1) {
        return Component.a("options.pixel_value", $$0, $$1);
    }

    private static Component percentValueLabel(Component $$0, double $$1) {
        return Component.a("options.percent_value", $$0, (int)($$1 * 100.0));
    }

    public static Component genericValueLabel(Component $$0, Component $$1) {
        return Component.a("options.generic_value", $$0, $$1);
    }

    public static Component genericValueLabel(Component $$0, int $$1) {
        return Options.genericValueLabel($$0, Component.literal(Integer.toString($$1)));
    }

    public static Component genericValueOrOffLabel(Component $$0, int $$1) {
        if ($$1 == 0) {
            return Options.genericValueLabel($$0, CommonComponents.OPTION_OFF);
        }
        return Options.genericValueLabel($$0, $$1);
    }

    private static Component percentValueOrOffLabel(Component $$0, double $$1) {
        if ($$1 == 0.0) {
            return Options.genericValueLabel($$0, CommonComponents.OPTION_OFF);
        }
        return Options.percentValueLabel($$0, $$1);
    }

    static interface OptionAccess {
        public <T> void process(String var1, OptionInstance<T> var2);
    }

    static interface FieldAccess
    extends OptionAccess {
        public int process(String var1, int var2);

        public boolean process(String var1, boolean var2);

        public String process(String var1, String var2);

        public float process(String var1, float var2);

        public <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4);
    }
}

