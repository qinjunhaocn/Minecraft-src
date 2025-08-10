/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.Window;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.client.gui.contextualbar.JumpableVehicleBarRenderer;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.apache.commons.lang3.tuple.Pair;

public class Gui {
    private static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_full");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_background");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_progress");
    private static final ResourceLocation EFFECT_BACKGROUND_AMBIENT_SPRITE = ResourceLocation.withDefaultNamespace("hud/effect_background_ambient");
    private static final ResourceLocation EFFECT_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/effect_background");
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_right");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_background");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_progress");
    private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_empty");
    private static final ResourceLocation ARMOR_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_half");
    private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");
    private static final ResourceLocation FOOD_EMPTY_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_empty_hunger");
    private static final ResourceLocation FOOD_HALF_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_half_hunger");
    private static final ResourceLocation FOOD_FULL_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_full_hunger");
    private static final ResourceLocation FOOD_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_empty");
    private static final ResourceLocation FOOD_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_half");
    private static final ResourceLocation FOOD_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_full");
    private static final ResourceLocation AIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/air");
    private static final ResourceLocation AIR_POPPING_SPRITE = ResourceLocation.withDefaultNamespace("hud/air_bursting");
    private static final ResourceLocation AIR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/air_empty");
    private static final ResourceLocation HEART_VEHICLE_CONTAINER_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_container");
    private static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_full");
    private static final ResourceLocation HEART_VEHICLE_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_half");
    private static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");
    public static final ResourceLocation NAUSEA_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/nausea.png");
    private static final ResourceLocation SPYGLASS_SCOPE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/spyglass_scope.png");
    private static final ResourceLocation POWDER_SNOW_OUTLINE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/powder_snow_outline.png");
    private static final Comparator<PlayerScoreEntry> SCORE_DISPLAY_ORDER = Comparator.comparing(PlayerScoreEntry::value).reversed().thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
    private static final Component DEMO_EXPIRED_TEXT = Component.translatable("demo.demoExpired");
    private static final Component SAVING_TEXT = Component.translatable("menu.savingLevel");
    private static final float MIN_CROSSHAIR_ATTACK_SPEED = 5.0f;
    private static final int EXPERIENCE_BAR_DISPLAY_TICKS = 100;
    private static final int NUM_HEARTS_PER_ROW = 10;
    private static final int LINE_HEIGHT = 10;
    private static final String SPACER = ": ";
    private static final float PORTAL_OVERLAY_ALPHA_MIN = 0.2f;
    private static final int HEART_SIZE = 9;
    private static final int HEART_SEPARATION = 8;
    private static final int NUM_AIR_BUBBLES = 10;
    private static final int AIR_BUBBLE_SIZE = 9;
    private static final int AIR_BUBBLE_SEPERATION = 8;
    private static final int AIR_BUBBLE_POPPING_DURATION = 2;
    private static final int EMPTY_AIR_BUBBLE_DELAY_DURATION = 1;
    private static final float AIR_BUBBLE_POP_SOUND_VOLUME_BASE = 0.5f;
    private static final float AIR_BUBBLE_POP_SOUND_VOLUME_INCREMENT = 0.1f;
    private static final float AIR_BUBBLE_POP_SOUND_PITCH_BASE = 1.0f;
    private static final float AIR_BUBBLE_POP_SOUND_PITCH_INCREMENT = 0.1f;
    private static final int NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_VOLUME_INCREASE = 3;
    private static final int NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_PITCH_INCREASE = 5;
    private static final float AUTOSAVE_FADE_SPEED_FACTOR = 0.2f;
    private static final int SAVING_INDICATOR_WIDTH_PADDING_RIGHT = 5;
    private static final int SAVING_INDICATOR_HEIGHT_PADDING_BOTTOM = 5;
    private final RandomSource random = RandomSource.create();
    private final Minecraft minecraft;
    private final ChatComponent chat;
    private int tickCount;
    @Nullable
    private Component overlayMessageString;
    private int overlayMessageTime;
    private boolean animateOverlayMessageColor;
    private boolean chatDisabledByPlayerShown;
    public float vignetteBrightness = 1.0f;
    private int toolHighlightTimer;
    private ItemStack lastToolHighlight = ItemStack.EMPTY;
    private final DebugScreenOverlay debugOverlay;
    private final SubtitleOverlay subtitleOverlay;
    private final SpectatorGui spectatorGui;
    private final PlayerTabOverlay tabList;
    private final BossHealthOverlay bossOverlay;
    private int titleTime;
    @Nullable
    private Component title;
    @Nullable
    private Component subtitle;
    private int titleFadeInTime;
    private int titleStayTime;
    private int titleFadeOutTime;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;
    private int lastBubblePopSoundPlayed;
    private float autosaveIndicatorValue;
    private float lastAutosaveIndicatorValue;
    private Pair<ContextualInfo, ContextualBarRenderer> contextualInfoBar = Pair.of(ContextualInfo.EMPTY, ContextualBarRenderer.EMPTY);
    private final Map<ContextualInfo, Supplier<ContextualBarRenderer>> contextualInfoBarRenderers;
    private float scopeScale;

    public Gui(Minecraft $$0) {
        this.minecraft = $$0;
        this.debugOverlay = new DebugScreenOverlay($$0);
        this.spectatorGui = new SpectatorGui($$0);
        this.chat = new ChatComponent($$0);
        this.tabList = new PlayerTabOverlay($$0, this);
        this.bossOverlay = new BossHealthOverlay($$0);
        this.subtitleOverlay = new SubtitleOverlay($$0);
        this.contextualInfoBarRenderers = ImmutableMap.of(ContextualInfo.EMPTY, () -> ContextualBarRenderer.EMPTY, ContextualInfo.EXPERIENCE, () -> new ExperienceBarRenderer($$0), ContextualInfo.LOCATOR, () -> new LocatorBarRenderer($$0), ContextualInfo.JUMPABLE_VEHICLE, () -> new JumpableVehicleBarRenderer($$0));
        this.resetTitleTimes();
    }

    public void resetTitleTimes() {
        this.titleFadeInTime = 10;
        this.titleStayTime = 70;
        this.titleFadeOutTime = 20;
    }

    public void render(GuiGraphics $$0, DeltaTracker $$1) {
        if (this.minecraft.screen != null && this.minecraft.screen instanceof ReceivingLevelScreen) {
            return;
        }
        if (!this.minecraft.options.hideGui) {
            this.renderCameraOverlays($$0, $$1);
            this.renderCrosshair($$0, $$1);
            $$0.nextStratum();
            this.renderHotbarAndDecorations($$0, $$1);
            this.renderEffects($$0, $$1);
            this.renderBossOverlay($$0, $$1);
        }
        this.renderSleepOverlay($$0, $$1);
        if (!this.minecraft.options.hideGui) {
            this.renderDemoOverlay($$0, $$1);
            this.renderDebugOverlay($$0, $$1);
            this.renderScoreboardSidebar($$0, $$1);
            this.renderOverlayMessage($$0, $$1);
            this.renderTitle($$0, $$1);
            this.renderChat($$0, $$1);
            this.renderTabList($$0, $$1);
            this.renderSubtitleOverlay($$0, $$1);
        }
    }

    private void renderBossOverlay(GuiGraphics $$0, DeltaTracker $$1) {
        this.bossOverlay.render($$0);
    }

    private void renderDebugOverlay(GuiGraphics $$0, DeltaTracker $$1) {
        if (this.debugOverlay.showDebugScreen()) {
            $$0.nextStratum();
            this.debugOverlay.render($$0);
        }
    }

    private void renderSubtitleOverlay(GuiGraphics $$0, DeltaTracker $$1) {
        this.subtitleOverlay.render($$0);
    }

    private void renderCameraOverlays(GuiGraphics $$02, DeltaTracker $$1) {
        float $$10;
        if (Minecraft.useFancyGraphics()) {
            this.renderVignette($$02, this.minecraft.getCameraEntity());
        }
        LocalPlayer $$2 = this.minecraft.player;
        float $$3 = $$1.getGameTimeDeltaTicks();
        this.scopeScale = Mth.lerp(0.5f * $$3, this.scopeScale, 1.125f);
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            if ($$2.isScoping()) {
                this.renderSpyglassOverlay($$02, this.scopeScale);
            } else {
                this.scopeScale = 0.5f;
                for (EquipmentSlot $$4 : EquipmentSlot.values()) {
                    ItemStack $$5 = $$2.getItemBySlot($$4);
                    Equippable $$6 = $$5.get(DataComponents.EQUIPPABLE);
                    if ($$6 == null || $$6.slot() != $$4 || !$$6.cameraOverlay().isPresent()) continue;
                    this.renderTextureOverlay($$02, $$6.cameraOverlay().get().withPath($$0 -> "textures/" + $$0 + ".png"), 1.0f);
                }
            }
        }
        if ($$2.getTicksFrozen() > 0) {
            this.renderTextureOverlay($$02, POWDER_SNOW_OUTLINE_LOCATION, $$2.getPercentFrozen());
        }
        float $$7 = $$1.getGameTimeDeltaPartialTick(false);
        float $$8 = Mth.lerp($$7, $$2.oPortalEffectIntensity, $$2.portalEffectIntensity);
        float $$9 = $$2.getEffectBlendFactor(MobEffects.NAUSEA, $$7);
        if ($$8 > 0.0f) {
            this.renderPortalOverlay($$02, $$8);
        } else if ($$9 > 0.0f && ($$10 = this.minecraft.options.screenEffectScale().get().floatValue()) < 1.0f) {
            float $$11 = $$9 * (1.0f - $$10);
            this.renderConfusionOverlay($$02, $$11);
        }
    }

    private void renderSleepOverlay(GuiGraphics $$0, DeltaTracker $$1) {
        if (this.minecraft.player.getSleepTimer() <= 0) {
            return;
        }
        Profiler.get().push("sleep");
        $$0.nextStratum();
        float $$2 = this.minecraft.player.getSleepTimer();
        float $$3 = $$2 / 100.0f;
        if ($$3 > 1.0f) {
            $$3 = 1.0f - ($$2 - 100.0f) / 10.0f;
        }
        int $$4 = (int)(220.0f * $$3) << 24 | 0x101020;
        $$0.fill(0, 0, $$0.guiWidth(), $$0.guiHeight(), $$4);
        Profiler.get().pop();
    }

    private void renderOverlayMessage(GuiGraphics $$0, DeltaTracker $$1) {
        Font $$2 = this.getFont();
        if (this.overlayMessageString == null || this.overlayMessageTime <= 0) {
            return;
        }
        Profiler.get().push("overlayMessage");
        float $$3 = (float)this.overlayMessageTime - $$1.getGameTimeDeltaPartialTick(false);
        int $$4 = (int)($$3 * 255.0f / 20.0f);
        if ($$4 > 255) {
            $$4 = 255;
        }
        if ($$4 > 0) {
            int $$6;
            $$0.nextStratum();
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)($$0.guiWidth() / 2), (float)($$0.guiHeight() - 68));
            if (this.animateOverlayMessageColor) {
                int $$5 = Mth.hsvToArgb($$3 / 50.0f, 0.7f, 0.6f, $$4);
            } else {
                $$6 = ARGB.color($$4, -1);
            }
            int $$7 = $$2.width(this.overlayMessageString);
            $$0.drawStringWithBackdrop($$2, this.overlayMessageString, -$$7 / 2, -4, $$7, $$6);
            $$0.pose().popMatrix();
        }
        Profiler.get().pop();
    }

    private void renderTitle(GuiGraphics $$0, DeltaTracker $$1) {
        if (this.title == null || this.titleTime <= 0) {
            return;
        }
        Font $$2 = this.getFont();
        Profiler.get().push("titleAndSubtitle");
        float $$3 = (float)this.titleTime - $$1.getGameTimeDeltaPartialTick(false);
        int $$4 = 255;
        if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
            float $$5 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - $$3;
            $$4 = (int)($$5 * 255.0f / (float)this.titleFadeInTime);
        }
        if (this.titleTime <= this.titleFadeOutTime) {
            $$4 = (int)($$3 * 255.0f / (float)this.titleFadeOutTime);
        }
        if (($$4 = Mth.clamp($$4, 0, 255)) > 0) {
            $$0.nextStratum();
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)($$0.guiWidth() / 2), (float)($$0.guiHeight() / 2));
            $$0.pose().pushMatrix();
            $$0.pose().scale(4.0f, 4.0f);
            int $$6 = $$2.width(this.title);
            int $$7 = ARGB.color($$4, -1);
            $$0.drawStringWithBackdrop($$2, this.title, -$$6 / 2, -10, $$6, $$7);
            $$0.pose().popMatrix();
            if (this.subtitle != null) {
                $$0.pose().pushMatrix();
                $$0.pose().scale(2.0f, 2.0f);
                int $$8 = $$2.width(this.subtitle);
                $$0.drawStringWithBackdrop($$2, this.subtitle, -$$8 / 2, 5, $$8, $$7);
                $$0.pose().popMatrix();
            }
            $$0.pose().popMatrix();
        }
        Profiler.get().pop();
    }

    private void renderChat(GuiGraphics $$0, DeltaTracker $$1) {
        if (!this.chat.isChatFocused()) {
            Window $$2 = this.minecraft.getWindow();
            int $$3 = Mth.floor(this.minecraft.mouseHandler.getScaledXPos($$2));
            int $$4 = Mth.floor(this.minecraft.mouseHandler.getScaledYPos($$2));
            $$0.nextStratum();
            this.chat.render($$0, this.tickCount, $$3, $$4, false);
        }
    }

    private void renderScoreboardSidebar(GuiGraphics $$0, DeltaTracker $$1) {
        Objective $$6;
        DisplaySlot $$5;
        Scoreboard $$2 = this.minecraft.level.getScoreboard();
        Objective $$3 = null;
        PlayerTeam $$4 = $$2.getPlayersTeam(this.minecraft.player.getScoreboardName());
        if ($$4 != null && ($$5 = DisplaySlot.teamColorToSlot($$4.getColor())) != null) {
            $$3 = $$2.getDisplayObjective($$5);
        }
        Objective objective = $$6 = $$3 != null ? $$3 : $$2.getDisplayObjective(DisplaySlot.SIDEBAR);
        if ($$6 != null) {
            $$0.nextStratum();
            this.displayScoreboardSidebar($$0, $$6);
        }
    }

    private void renderTabList(GuiGraphics $$0, DeltaTracker $$1) {
        Scoreboard $$2 = this.minecraft.level.getScoreboard();
        Objective $$3 = $$2.getDisplayObjective(DisplaySlot.LIST);
        if (this.minecraft.options.keyPlayerList.isDown() && (!this.minecraft.isLocalServer() || this.minecraft.player.connection.getListedOnlinePlayers().size() > 1 || $$3 != null)) {
            this.tabList.setVisible(true);
            $$0.nextStratum();
            this.tabList.render($$0, $$0.guiWidth(), $$2, $$3);
        } else {
            this.tabList.setVisible(false);
        }
    }

    private void renderCrosshair(GuiGraphics $$0, DeltaTracker $$1) {
        Options $$2 = this.minecraft.options;
        if (!$$2.getCameraType().isFirstPerson()) {
            return;
        }
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR && !this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            return;
        }
        if (!this.shouldRenderDebugCrosshair()) {
            $$0.nextStratum();
            int $$3 = 15;
            $$0.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SPRITE, ($$0.guiWidth() - 15) / 2, ($$0.guiHeight() - 15) / 2, 15, 15);
            if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                float $$4 = this.minecraft.player.getAttackStrengthScale(0.0f);
                boolean $$5 = false;
                if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && $$4 >= 1.0f) {
                    $$5 = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0f;
                    $$5 &= this.minecraft.crosshairPickEntity.isAlive();
                }
                int $$6 = $$0.guiHeight() / 2 - 7 + 16;
                int $$7 = $$0.guiWidth() / 2 - 8;
                if ($$5) {
                    $$0.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, $$7, $$6, 16, 16);
                } else if ($$4 < 1.0f) {
                    int $$8 = (int)($$4 * 17.0f);
                    $$0.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, $$7, $$6, 16, 4);
                    $$0.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, $$7, $$6, $$8, 4);
                }
            }
        }
    }

    public boolean shouldRenderDebugCrosshair() {
        return this.debugOverlay.showDebugScreen() && this.minecraft.options.getCameraType() == CameraType.FIRST_PERSON && !this.minecraft.player.isReducedDebugInfo() && this.minecraft.options.reducedDebugInfo().get() == false;
    }

    private boolean canRenderCrosshairForSpectator(@Nullable HitResult $$0) {
        if ($$0 == null) {
            return false;
        }
        if ($$0.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)$$0).getEntity() instanceof MenuProvider;
        }
        if ($$0.getType() == HitResult.Type.BLOCK) {
            ClientLevel $$2 = this.minecraft.level;
            BlockPos $$1 = ((BlockHitResult)$$0).getBlockPos();
            return $$2.getBlockState($$1).getMenuProvider($$2, $$1) != null;
        }
        return false;
    }

    private void renderEffects(GuiGraphics $$0, DeltaTracker $$1) {
        Collection<MobEffectInstance> $$2 = this.minecraft.player.getActiveEffects();
        if ($$2.isEmpty() || this.minecraft.screen != null && this.minecraft.screen.showsActiveEffects()) {
            return;
        }
        int $$3 = 0;
        int $$4 = 0;
        for (MobEffectInstance $$5 : Ordering.natural().reverse().sortedCopy($$2)) {
            Holder<MobEffect> $$6 = $$5.getEffect();
            if (!$$5.showIcon()) continue;
            int $$7 = $$0.guiWidth();
            int $$8 = 1;
            if (this.minecraft.isDemo()) {
                $$8 += 15;
            }
            if ($$6.value().isBeneficial()) {
                $$7 -= 25 * ++$$3;
            } else {
                $$7 -= 25 * ++$$4;
                $$8 += 26;
            }
            float $$9 = 1.0f;
            if ($$5.isAmbient()) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_AMBIENT_SPRITE, $$7, $$8, 24, 24);
            } else {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SPRITE, $$7, $$8, 24, 24);
                if ($$5.endsWithin(200)) {
                    int $$10 = $$5.getDuration();
                    int $$11 = 10 - $$10 / 20;
                    $$9 = Mth.clamp((float)$$10 / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + Mth.cos((float)$$10 * (float)Math.PI / 5.0f) * Mth.clamp((float)$$11 / 10.0f * 0.25f, 0.0f, 0.25f);
                    $$9 = Mth.clamp($$9, 0.0f, 1.0f);
                }
            }
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite($$6), $$7 + 3, $$8 + 3, 18, 18, ARGB.white($$9));
        }
    }

    public static ResourceLocation getMobEffectSprite(Holder<MobEffect> $$02) {
        return $$02.unwrapKey().map(ResourceKey::location).map($$0 -> $$0.withPrefix("mob_effect/")).orElseGet(MissingTextureAtlasSprite::getLocation);
    }

    private void renderHotbarAndDecorations(GuiGraphics $$0, DeltaTracker $$1) {
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            this.spectatorGui.renderHotbar($$0);
        } else {
            this.renderItemHotbar($$0, $$1);
        }
        if (this.minecraft.gameMode.canHurtPlayer()) {
            this.renderPlayerHealth($$0);
        }
        this.renderVehicleHealth($$0);
        ContextualInfo $$2 = this.nextContextualInfoState();
        if ($$2 != this.contextualInfoBar.getKey()) {
            this.contextualInfoBar = Pair.of($$2, this.contextualInfoBarRenderers.get((Object)$$2).get());
        }
        this.contextualInfoBar.getValue().renderBackground($$0, $$1);
        if (this.minecraft.gameMode.hasExperience() && this.minecraft.player.experienceLevel > 0) {
            ContextualBarRenderer.renderExperienceLevel($$0, this.minecraft.font, this.minecraft.player.experienceLevel);
        }
        this.contextualInfoBar.getValue().render($$0, $$1);
        if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.renderSelectedItemName($$0);
        } else if (this.minecraft.player.isSpectator()) {
            this.spectatorGui.renderAction($$0);
        }
    }

    private void renderItemHotbar(GuiGraphics $$0, DeltaTracker $$1) {
        float $$13;
        Player $$2 = this.getCameraPlayer();
        if ($$2 == null) {
            return;
        }
        ItemStack $$3 = $$2.getOffhandItem();
        HumanoidArm $$4 = $$2.getMainArm().getOpposite();
        int $$5 = $$0.guiWidth() / 2;
        int $$6 = 182;
        int $$7 = 91;
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, $$5 - 91, $$0.guiHeight() - 22, 182, 22);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_SPRITE, $$5 - 91 - 1 + $$2.getInventory().getSelectedSlot() * 20, $$0.guiHeight() - 22 - 1, 24, 23);
        if (!$$3.isEmpty()) {
            if ($$4 == HumanoidArm.LEFT) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, $$5 - 91 - 29, $$0.guiHeight() - 23, 29, 24);
            } else {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_RIGHT_SPRITE, $$5 + 91, $$0.guiHeight() - 23, 29, 24);
            }
        }
        int $$8 = 1;
        for (int $$9 = 0; $$9 < 9; ++$$9) {
            int $$10 = $$5 - 90 + $$9 * 20 + 2;
            int $$11 = $$0.guiHeight() - 16 - 3;
            this.renderSlot($$0, $$10, $$11, $$1, $$2, $$2.getInventory().getItem($$9), $$8++);
        }
        if (!$$3.isEmpty()) {
            int $$12 = $$0.guiHeight() - 16 - 3;
            if ($$4 == HumanoidArm.LEFT) {
                this.renderSlot($$0, $$5 - 91 - 26, $$12, $$1, $$2, $$3, $$8++);
            } else {
                this.renderSlot($$0, $$5 + 91 + 10, $$12, $$1, $$2, $$3, $$8++);
            }
        }
        if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR && ($$13 = this.minecraft.player.getAttackStrengthScale(0.0f)) < 1.0f) {
            int $$14 = $$0.guiHeight() - 20;
            int $$15 = $$5 + 91 + 6;
            if ($$4 == HumanoidArm.RIGHT) {
                $$15 = $$5 - 91 - 22;
            }
            int $$16 = (int)($$13 * 19.0f);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, $$15, $$14, 18, 18);
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - $$16, $$15, $$14 + 18 - $$16, 18, $$16);
        }
    }

    private void renderSelectedItemName(GuiGraphics $$0) {
        Profiler.get().push("selectedItemName");
        if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
            int $$5;
            MutableComponent $$1 = Component.empty().append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().color());
            if (this.lastToolHighlight.has(DataComponents.CUSTOM_NAME)) {
                $$1.withStyle(ChatFormatting.ITALIC);
            }
            int $$2 = this.getFont().width($$1);
            int $$3 = ($$0.guiWidth() - $$2) / 2;
            int $$4 = $$0.guiHeight() - 59;
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                $$4 += 14;
            }
            if (($$5 = (int)((float)this.toolHighlightTimer * 256.0f / 10.0f)) > 255) {
                $$5 = 255;
            }
            if ($$5 > 0) {
                $$0.drawStringWithBackdrop(this.getFont(), $$1, $$3, $$4, $$2, ARGB.color($$5, -1));
            }
        }
        Profiler.get().pop();
    }

    private void renderDemoOverlay(GuiGraphics $$0, DeltaTracker $$1) {
        MutableComponent $$3;
        if (!this.minecraft.isDemo()) {
            return;
        }
        Profiler.get().push("demo");
        $$0.nextStratum();
        if (this.minecraft.level.getGameTime() >= 120500L) {
            Component $$2 = DEMO_EXPIRED_TEXT;
        } else {
            $$3 = Component.a("demo.remainingTime", StringUtil.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime()), this.minecraft.level.tickRateManager().tickrate()));
        }
        int $$4 = this.getFont().width($$3);
        int $$5 = $$0.guiWidth() - $$4 - 10;
        int $$6 = 5;
        $$0.drawStringWithBackdrop(this.getFont(), $$3, $$5, 5, $$4, -1);
        Profiler.get().pop();
    }

    private void displayScoreboardSidebar(GuiGraphics $$02, Objective $$1) {
        int $$6;
        Scoreboard $$22 = $$1.getScoreboard();
        NumberFormat $$3 = $$1.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
        final class DisplayEntry
        extends Record {
            final Component name;
            final Component score;
            final int scoreWidth;

            DisplayEntry(Component $$0, Component $$1, int $$2) {
                this.name = $$0;
                this.score = $$1;
                this.scoreWidth = $$2;
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{DisplayEntry.class, "name;score;scoreWidth", "name", "score", "scoreWidth"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DisplayEntry.class, "name;score;scoreWidth", "name", "score", "scoreWidth"}, this);
            }

            public final boolean equals(Object $$0) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DisplayEntry.class, "name;score;scoreWidth", "name", "score", "scoreWidth"}, this, $$0);
            }

            public Component name() {
                return this.name;
            }

            public Component score() {
                return this.score;
            }

            public int scoreWidth() {
                return this.scoreWidth;
            }
        }
        DisplayEntry[] $$4 = (DisplayEntry[])$$22.listPlayerScores($$1).stream().filter($$0 -> !$$0.isHidden()).sorted(SCORE_DISPLAY_ORDER).limit(15L).map($$2 -> {
            PlayerTeam $$3 = $$22.getPlayersTeam($$2.owner());
            Component $$4 = $$2.ownerName();
            MutableComponent $$5 = PlayerTeam.formatNameForTeam($$3, $$4);
            MutableComponent $$6 = $$2.formatValue($$3);
            int $$7 = this.getFont().width($$6);
            return new DisplayEntry($$5, $$6, $$7);
        }).toArray($$0 -> new DisplayEntry[$$0]);
        Component $$5 = $$1.getDisplayName();
        int $$7 = $$6 = this.getFont().width($$5);
        int $$8 = this.getFont().width(SPACER);
        for (DisplayEntry $$9 : $$4) {
            $$7 = Math.max($$7, this.getFont().width($$9.name) + ($$9.scoreWidth > 0 ? $$8 + $$9.scoreWidth : 0));
        }
        int $$10 = $$7;
        int $$11 = $$4.length;
        int $$12 = $$11 * this.getFont().lineHeight;
        int $$13 = $$02.guiHeight() / 2 + $$12 / 3;
        int $$14 = 3;
        int $$15 = $$02.guiWidth() - $$10 - 3;
        int $$16 = $$02.guiWidth() - 3 + 2;
        int $$17 = this.minecraft.options.getBackgroundColor(0.3f);
        int $$18 = this.minecraft.options.getBackgroundColor(0.4f);
        int $$19 = $$13 - $$11 * this.getFont().lineHeight;
        $$02.fill($$15 - 2, $$19 - this.getFont().lineHeight - 1, $$16, $$19 - 1, $$18);
        $$02.fill($$15 - 2, $$19 - 1, $$16, $$13, $$17);
        $$02.drawString(this.getFont(), $$5, $$15 + $$10 / 2 - $$6 / 2, $$19 - this.getFont().lineHeight, -1, false);
        for (int $$20 = 0; $$20 < $$11; ++$$20) {
            DisplayEntry $$21 = $$4[$$20];
            int $$222 = $$13 - ($$11 - $$20) * this.getFont().lineHeight;
            $$02.drawString(this.getFont(), $$21.name, $$15, $$222, -1, false);
            $$02.drawString(this.getFont(), $$21.score, $$16 - $$21.scoreWidth, $$222, -1, false);
        }
    }

    @Nullable
    private Player getCameraPlayer() {
        Player $$0;
        Entity entity = this.minecraft.getCameraEntity();
        return entity instanceof Player ? ($$0 = (Player)entity) : null;
    }

    @Nullable
    private LivingEntity getPlayerVehicleWithHealth() {
        Player $$0 = this.getCameraPlayer();
        if ($$0 != null) {
            Entity $$1 = $$0.getVehicle();
            if ($$1 == null) {
                return null;
            }
            if ($$1 instanceof LivingEntity) {
                return (LivingEntity)$$1;
            }
        }
        return null;
    }

    private int getVehicleMaxHearts(@Nullable LivingEntity $$0) {
        if ($$0 == null || !$$0.showVehicleHealth()) {
            return 0;
        }
        float $$1 = $$0.getMaxHealth();
        int $$2 = (int)($$1 + 0.5f) / 2;
        if ($$2 > 30) {
            $$2 = 30;
        }
        return $$2;
    }

    private int getVisibleVehicleHeartRows(int $$0) {
        return (int)Math.ceil((double)$$0 / 10.0);
    }

    private void renderPlayerHealth(GuiGraphics $$0) {
        Player $$1 = this.getCameraPlayer();
        if ($$1 == null) {
            return;
        }
        int $$2 = Mth.ceil($$1.getHealth());
        boolean $$3 = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
        long $$4 = Util.getMillis();
        if ($$2 < this.lastHealth && $$1.invulnerableTime > 0) {
            this.lastHealthTime = $$4;
            this.healthBlinkTime = this.tickCount + 20;
        } else if ($$2 > this.lastHealth && $$1.invulnerableTime > 0) {
            this.lastHealthTime = $$4;
            this.healthBlinkTime = this.tickCount + 10;
        }
        if ($$4 - this.lastHealthTime > 1000L) {
            this.displayHealth = $$2;
            this.lastHealthTime = $$4;
        }
        this.lastHealth = $$2;
        int $$5 = this.displayHealth;
        this.random.setSeed(this.tickCount * 312871);
        int $$6 = $$0.guiWidth() / 2 - 91;
        int $$7 = $$0.guiWidth() / 2 + 91;
        int $$8 = $$0.guiHeight() - 39;
        float $$9 = Math.max((float)$$1.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max($$5, $$2));
        int $$10 = Mth.ceil($$1.getAbsorptionAmount());
        int $$11 = Mth.ceil(($$9 + (float)$$10) / 2.0f / 10.0f);
        int $$12 = Math.max(10 - ($$11 - 2), 3);
        int $$13 = $$8 - 10;
        int $$14 = -1;
        if ($$1.hasEffect(MobEffects.REGENERATION)) {
            $$14 = this.tickCount % Mth.ceil($$9 + 5.0f);
        }
        Profiler.get().push("armor");
        Gui.renderArmor($$0, $$1, $$8, $$11, $$12, $$6);
        Profiler.get().popPush("health");
        this.renderHearts($$0, $$1, $$6, $$8, $$12, $$14, $$9, $$2, $$5, $$10, $$3);
        LivingEntity $$15 = this.getPlayerVehicleWithHealth();
        int $$16 = this.getVehicleMaxHearts($$15);
        if ($$16 == 0) {
            Profiler.get().popPush("food");
            this.renderFood($$0, $$1, $$8, $$7);
            $$13 -= 10;
        }
        Profiler.get().popPush("air");
        this.renderAirBubbles($$0, $$1, $$16, $$13, $$7);
        Profiler.get().pop();
    }

    private static void renderArmor(GuiGraphics $$0, Player $$1, int $$2, int $$3, int $$4, int $$5) {
        int $$6 = $$1.getArmorValue();
        if ($$6 <= 0) {
            return;
        }
        int $$7 = $$2 - ($$3 - 1) * $$4 - 10;
        for (int $$8 = 0; $$8 < 10; ++$$8) {
            int $$9 = $$5 + $$8 * 8;
            if ($$8 * 2 + 1 < $$6) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_FULL_SPRITE, $$9, $$7, 9, 9);
            }
            if ($$8 * 2 + 1 == $$6) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_HALF_SPRITE, $$9, $$7, 9, 9);
            }
            if ($$8 * 2 + 1 <= $$6) continue;
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, ARMOR_EMPTY_SPRITE, $$9, $$7, 9, 9);
        }
    }

    private void renderHearts(GuiGraphics $$0, Player $$1, int $$2, int $$3, int $$4, int $$5, float $$6, int $$7, int $$8, int $$9, boolean $$10) {
        HeartType $$11 = HeartType.forPlayer($$1);
        boolean $$12 = $$1.level().getLevelData().isHardcore();
        int $$13 = Mth.ceil((double)$$6 / 2.0);
        int $$14 = Mth.ceil((double)$$9 / 2.0);
        int $$15 = $$13 * 2;
        for (int $$16 = $$13 + $$14 - 1; $$16 >= 0; --$$16) {
            int $$23;
            boolean $$22;
            int $$17 = $$16 / 10;
            int $$18 = $$16 % 10;
            int $$19 = $$2 + $$18 * 8;
            int $$20 = $$3 - $$17 * $$4;
            if ($$7 + $$9 <= 4) {
                $$20 += this.random.nextInt(2);
            }
            if ($$16 < $$13 && $$16 == $$5) {
                $$20 -= 2;
            }
            this.renderHeart($$0, HeartType.CONTAINER, $$19, $$20, $$12, $$10, false);
            int $$21 = $$16 * 2;
            boolean bl = $$22 = $$16 >= $$13;
            if ($$22 && ($$23 = $$21 - $$15) < $$9) {
                boolean $$24 = $$23 + 1 == $$9;
                this.renderHeart($$0, $$11 == HeartType.WITHERED ? $$11 : HeartType.ABSORBING, $$19, $$20, $$12, false, $$24);
            }
            if ($$10 && $$21 < $$8) {
                boolean $$25 = $$21 + 1 == $$8;
                this.renderHeart($$0, $$11, $$19, $$20, $$12, true, $$25);
            }
            if ($$21 >= $$7) continue;
            boolean $$26 = $$21 + 1 == $$7;
            this.renderHeart($$0, $$11, $$19, $$20, $$12, false, $$26);
        }
    }

    private void renderHeart(GuiGraphics $$0, HeartType $$1, int $$2, int $$3, boolean $$4, boolean $$5, boolean $$6) {
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$1.getSprite($$4, $$6, $$5), $$2, $$3, 9, 9);
    }

    private void renderAirBubbles(GuiGraphics $$0, Player $$1, int $$2, int $$3, int $$4) {
        int $$5 = $$1.getMaxAirSupply();
        int $$6 = Math.clamp((long)$$1.getAirSupply(), (int)0, (int)$$5);
        boolean $$7 = $$1.isEyeInFluid(FluidTags.WATER);
        if ($$7 || $$6 < $$5) {
            boolean $$11;
            $$3 = this.getAirBubbleYLine($$2, $$3);
            int $$8 = Gui.getCurrentAirSupplyBubble($$6, $$5, -2);
            int $$9 = Gui.getCurrentAirSupplyBubble($$6, $$5, 0);
            int $$10 = 10 - Gui.getCurrentAirSupplyBubble($$6, $$5, Gui.getEmptyBubbleDelayDuration($$6, $$7));
            boolean bl = $$11 = $$8 != $$9;
            if (!$$7) {
                this.lastBubblePopSoundPlayed = 0;
            }
            for (int $$12 = 1; $$12 <= 10; ++$$12) {
                int $$13 = $$4 - ($$12 - 1) * 8 - 9;
                if ($$12 <= $$8) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, AIR_SPRITE, $$13, $$3, 9, 9);
                    continue;
                }
                if ($$11 && $$12 == $$9 && $$7) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, AIR_POPPING_SPRITE, $$13, $$3, 9, 9);
                    this.playAirBubblePoppedSound($$12, $$1, $$10);
                    continue;
                }
                if ($$12 <= 10 - $$10) continue;
                int $$14 = $$10 == 10 && this.tickCount % 2 == 0 ? this.random.nextInt(2) : 0;
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, AIR_EMPTY_SPRITE, $$13, $$3 + $$14, 9, 9);
            }
        }
    }

    private int getAirBubbleYLine(int $$0, int $$1) {
        int $$2 = this.getVisibleVehicleHeartRows($$0) - 1;
        return $$1 -= $$2 * 10;
    }

    private static int getCurrentAirSupplyBubble(int $$0, int $$1, int $$2) {
        return Mth.ceil((float)(($$0 + $$2) * 10) / (float)$$1);
    }

    private static int getEmptyBubbleDelayDuration(int $$0, boolean $$1) {
        return $$0 == 0 || !$$1 ? 0 : 1;
    }

    private void playAirBubblePoppedSound(int $$0, Player $$1, int $$2) {
        if (this.lastBubblePopSoundPlayed != $$0) {
            float $$3 = 0.5f + 0.1f * (float)Math.max(0, $$2 - 3 + 1);
            float $$4 = 1.0f + 0.1f * (float)Math.max(0, $$2 - 5 + 1);
            $$1.playSound(SoundEvents.BUBBLE_POP, $$3, $$4);
            this.lastBubblePopSoundPlayed = $$0;
        }
    }

    private void renderFood(GuiGraphics $$0, Player $$1, int $$2, int $$3) {
        FoodData $$4 = $$1.getFoodData();
        int $$5 = $$4.getFoodLevel();
        for (int $$6 = 0; $$6 < 10; ++$$6) {
            ResourceLocation $$13;
            ResourceLocation $$12;
            ResourceLocation $$11;
            int $$7 = $$2;
            if ($$1.hasEffect(MobEffects.HUNGER)) {
                ResourceLocation $$8 = FOOD_EMPTY_HUNGER_SPRITE;
                ResourceLocation $$9 = FOOD_HALF_HUNGER_SPRITE;
                ResourceLocation $$10 = FOOD_FULL_HUNGER_SPRITE;
            } else {
                $$11 = FOOD_EMPTY_SPRITE;
                $$12 = FOOD_HALF_SPRITE;
                $$13 = FOOD_FULL_SPRITE;
            }
            if ($$1.getFoodData().getSaturationLevel() <= 0.0f && this.tickCount % ($$5 * 3 + 1) == 0) {
                $$7 += this.random.nextInt(3) - 1;
            }
            int $$14 = $$3 - $$6 * 8 - 9;
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$11, $$14, $$7, 9, 9);
            if ($$6 * 2 + 1 < $$5) {
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$13, $$14, $$7, 9, 9);
            }
            if ($$6 * 2 + 1 != $$5) continue;
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$12, $$14, $$7, 9, 9);
        }
    }

    private void renderVehicleHealth(GuiGraphics $$0) {
        LivingEntity $$1 = this.getPlayerVehicleWithHealth();
        if ($$1 == null) {
            return;
        }
        int $$2 = this.getVehicleMaxHearts($$1);
        if ($$2 == 0) {
            return;
        }
        int $$3 = (int)Math.ceil($$1.getHealth());
        Profiler.get().popPush("mountHealth");
        int $$4 = $$0.guiHeight() - 39;
        int $$5 = $$0.guiWidth() / 2 + 91;
        int $$6 = $$4;
        int $$7 = 0;
        while ($$2 > 0) {
            int $$8 = Math.min($$2, 10);
            $$2 -= $$8;
            for (int $$9 = 0; $$9 < $$8; ++$$9) {
                int $$10 = $$5 - $$9 * 8 - 9;
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_VEHICLE_CONTAINER_SPRITE, $$10, $$6, 9, 9);
                if ($$9 * 2 + 1 + $$7 < $$3) {
                    $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_VEHICLE_FULL_SPRITE, $$10, $$6, 9, 9);
                }
                if ($$9 * 2 + 1 + $$7 != $$3) continue;
                $$0.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_VEHICLE_HALF_SPRITE, $$10, $$6, 9, 9);
            }
            $$6 -= 10;
            $$7 += 20;
        }
    }

    private void renderTextureOverlay(GuiGraphics $$0, ResourceLocation $$1, float $$2) {
        int $$3 = ARGB.white($$2);
        $$0.blit(RenderPipelines.GUI_TEXTURED, $$1, 0, 0, 0.0f, 0.0f, $$0.guiWidth(), $$0.guiHeight(), $$0.guiWidth(), $$0.guiHeight(), $$3);
    }

    private void renderSpyglassOverlay(GuiGraphics $$0, float $$1) {
        float $$2;
        float $$3 = $$2 = (float)Math.min($$0.guiWidth(), $$0.guiHeight());
        float $$4 = Math.min((float)$$0.guiWidth() / $$2, (float)$$0.guiHeight() / $$3) * $$1;
        int $$5 = Mth.floor($$2 * $$4);
        int $$6 = Mth.floor($$3 * $$4);
        int $$7 = ($$0.guiWidth() - $$5) / 2;
        int $$8 = ($$0.guiHeight() - $$6) / 2;
        int $$9 = $$7 + $$5;
        int $$10 = $$8 + $$6;
        $$0.blit(RenderPipelines.GUI_TEXTURED, SPYGLASS_SCOPE_LOCATION, $$7, $$8, 0.0f, 0.0f, $$5, $$6, $$5, $$6);
        $$0.fill(RenderPipelines.GUI, 0, $$10, $$0.guiWidth(), $$0.guiHeight(), -16777216);
        $$0.fill(RenderPipelines.GUI, 0, 0, $$0.guiWidth(), $$8, -16777216);
        $$0.fill(RenderPipelines.GUI, 0, $$8, $$7, $$10, -16777216);
        $$0.fill(RenderPipelines.GUI, $$9, $$8, $$0.guiWidth(), $$10, -16777216);
    }

    private void updateVignetteBrightness(Entity $$0) {
        BlockPos $$1 = BlockPos.containing($$0.getX(), $$0.getEyeY(), $$0.getZ());
        float $$2 = LightTexture.getBrightness($$0.level().dimensionType(), $$0.level().getMaxLocalRawBrightness($$1));
        float $$3 = Mth.clamp(1.0f - $$2, 0.0f, 1.0f);
        this.vignetteBrightness += ($$3 - this.vignetteBrightness) * 0.01f;
    }

    private void renderVignette(GuiGraphics $$0, @Nullable Entity $$1) {
        int $$9;
        WorldBorder $$2 = this.minecraft.level.getWorldBorder();
        float $$3 = 0.0f;
        if ($$1 != null) {
            float $$4 = (float)$$2.getDistanceToBorder($$1);
            double $$5 = Math.min($$2.getLerpSpeed() * (double)$$2.getWarningTime() * 1000.0, Math.abs($$2.getLerpTarget() - $$2.getSize()));
            double $$6 = Math.max((double)$$2.getWarningBlocks(), $$5);
            if ((double)$$4 < $$6) {
                $$3 = 1.0f - (float)((double)$$4 / $$6);
            }
        }
        if ($$3 > 0.0f) {
            $$3 = Mth.clamp($$3, 0.0f, 1.0f);
            int $$7 = ARGB.colorFromFloat(1.0f, 0.0f, $$3, $$3);
        } else {
            float $$8 = this.vignetteBrightness;
            $$8 = Mth.clamp($$8, 0.0f, 1.0f);
            $$9 = ARGB.colorFromFloat(1.0f, $$8, $$8, $$8);
        }
        $$0.blit(RenderPipelines.VIGNETTE, VIGNETTE_LOCATION, 0, 0, 0.0f, 0.0f, $$0.guiWidth(), $$0.guiHeight(), $$0.guiWidth(), $$0.guiHeight(), $$9);
    }

    private void renderPortalOverlay(GuiGraphics $$0, float $$1) {
        if ($$1 < 1.0f) {
            $$1 *= $$1;
            $$1 *= $$1;
            $$1 = $$1 * 0.8f + 0.2f;
        }
        int $$2 = ARGB.white($$1);
        TextureAtlasSprite $$3 = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$3, 0, 0, $$0.guiWidth(), $$0.guiHeight(), $$2);
    }

    private void renderConfusionOverlay(GuiGraphics $$0, float $$1) {
        int $$2 = $$0.guiWidth();
        int $$3 = $$0.guiHeight();
        $$0.pose().pushMatrix();
        float $$4 = Mth.lerp($$1, 2.0f, 1.0f);
        $$0.pose().translate((float)$$2 / 2.0f, (float)$$3 / 2.0f);
        $$0.pose().scale($$4, $$4);
        $$0.pose().translate((float)(-$$2) / 2.0f, (float)(-$$3) / 2.0f);
        float $$5 = 0.2f * $$1;
        float $$6 = 0.4f * $$1;
        float $$7 = 0.2f * $$1;
        $$0.blit(RenderPipelines.GUI_NAUSEA_OVERLAY, NAUSEA_LOCATION, 0, 0, 0.0f, 0.0f, $$2, $$3, $$2, $$3, ARGB.colorFromFloat(1.0f, $$5, $$6, $$7));
        $$0.pose().popMatrix();
    }

    private void renderSlot(GuiGraphics $$0, int $$1, int $$2, DeltaTracker $$3, Player $$4, ItemStack $$5, int $$6) {
        if ($$5.isEmpty()) {
            return;
        }
        float $$7 = (float)$$5.getPopTime() - $$3.getGameTimeDeltaPartialTick(false);
        if ($$7 > 0.0f) {
            float $$8 = 1.0f + $$7 / 5.0f;
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)($$1 + 8), (float)($$2 + 12));
            $$0.pose().scale(1.0f / $$8, ($$8 + 1.0f) / 2.0f);
            $$0.pose().translate((float)(-($$1 + 8)), (float)(-($$2 + 12)));
        }
        $$0.renderItem($$4, $$5, $$1, $$2, $$6);
        if ($$7 > 0.0f) {
            $$0.pose().popMatrix();
        }
        $$0.renderItemDecorations(this.minecraft.font, $$5, $$1, $$2);
    }

    public void tick(boolean $$0) {
        this.tickAutosaveIndicator();
        if (!$$0) {
            this.tick();
        }
    }

    private void tick() {
        if (this.overlayMessageTime > 0) {
            --this.overlayMessageTime;
        }
        if (this.titleTime > 0) {
            --this.titleTime;
            if (this.titleTime <= 0) {
                this.title = null;
                this.subtitle = null;
            }
        }
        ++this.tickCount;
        Entity $$0 = this.minecraft.getCameraEntity();
        if ($$0 != null) {
            this.updateVignetteBrightness($$0);
        }
        if (this.minecraft.player != null) {
            ItemStack $$1 = this.minecraft.player.getInventory().getSelectedItem();
            if ($$1.isEmpty()) {
                this.toolHighlightTimer = 0;
            } else if (this.lastToolHighlight.isEmpty() || !$$1.is(this.lastToolHighlight.getItem()) || !$$1.getHoverName().equals(this.lastToolHighlight.getHoverName())) {
                this.toolHighlightTimer = (int)(40.0 * this.minecraft.options.notificationDisplayTime().get());
            } else if (this.toolHighlightTimer > 0) {
                --this.toolHighlightTimer;
            }
            this.lastToolHighlight = $$1;
        }
        this.chat.tick();
    }

    private void tickAutosaveIndicator() {
        IntegratedServer $$0 = this.minecraft.getSingleplayerServer();
        boolean $$1 = $$0 != null && $$0.isCurrentlySaving();
        this.lastAutosaveIndicatorValue = this.autosaveIndicatorValue;
        this.autosaveIndicatorValue = Mth.lerp(0.2f, this.autosaveIndicatorValue, $$1 ? 1.0f : 0.0f);
    }

    public void setNowPlaying(Component $$0) {
        MutableComponent $$1 = Component.a("record.nowPlaying", $$0);
        this.setOverlayMessage($$1, true);
        this.minecraft.getNarrator().saySystemNow($$1);
    }

    public void setOverlayMessage(Component $$0, boolean $$1) {
        this.setChatDisabledByPlayerShown(false);
        this.overlayMessageString = $$0;
        this.overlayMessageTime = 60;
        this.animateOverlayMessageColor = $$1;
    }

    public void setChatDisabledByPlayerShown(boolean $$0) {
        this.chatDisabledByPlayerShown = $$0;
    }

    public boolean isShowingChatDisabledByPlayer() {
        return this.chatDisabledByPlayerShown && this.overlayMessageTime > 0;
    }

    public void setTimes(int $$0, int $$1, int $$2) {
        if ($$0 >= 0) {
            this.titleFadeInTime = $$0;
        }
        if ($$1 >= 0) {
            this.titleStayTime = $$1;
        }
        if ($$2 >= 0) {
            this.titleFadeOutTime = $$2;
        }
        if (this.titleTime > 0) {
            this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
        }
    }

    public void setSubtitle(Component $$0) {
        this.subtitle = $$0;
    }

    public void setTitle(Component $$0) {
        this.title = $$0;
        this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
    }

    public void clearTitles() {
        this.title = null;
        this.subtitle = null;
        this.titleTime = 0;
    }

    public ChatComponent getChat() {
        return this.chat;
    }

    public int getGuiTicks() {
        return this.tickCount;
    }

    public Font getFont() {
        return this.minecraft.font;
    }

    public SpectatorGui getSpectatorGui() {
        return this.spectatorGui;
    }

    public PlayerTabOverlay getTabList() {
        return this.tabList;
    }

    public void onDisconnected() {
        this.tabList.reset();
        this.bossOverlay.reset();
        this.minecraft.getToastManager().clear();
        this.debugOverlay.reset();
        this.chat.clearMessages(true);
        this.clearTitles();
        this.resetTitleTimes();
    }

    public BossHealthOverlay getBossOverlay() {
        return this.bossOverlay;
    }

    public DebugScreenOverlay getDebugOverlay() {
        return this.debugOverlay;
    }

    public void clearCache() {
        this.debugOverlay.clearChunkCache();
    }

    public void renderSavingIndicator(GuiGraphics $$0, DeltaTracker $$1) {
        int $$2;
        if (this.minecraft.options.showAutosaveIndicator().get().booleanValue() && (this.autosaveIndicatorValue > 0.0f || this.lastAutosaveIndicatorValue > 0.0f) && ($$2 = Mth.floor(255.0f * Mth.clamp(Mth.lerp($$1.getRealtimeDeltaTicks(), this.lastAutosaveIndicatorValue, this.autosaveIndicatorValue), 0.0f, 1.0f))) > 0) {
            Font $$3 = this.getFont();
            int $$4 = $$3.width(SAVING_TEXT);
            int $$5 = ARGB.color($$2, -1);
            int $$6 = $$0.guiWidth() - $$4 - 5;
            int $$7 = $$0.guiHeight() - $$3.lineHeight - 5;
            $$0.nextStratum();
            $$0.drawStringWithBackdrop($$3, SAVING_TEXT, $$6, $$7, $$4, $$5);
        }
    }

    private boolean willPrioritizeExperienceInfo() {
        return this.minecraft.player.experienceDisplayStartTick + 100 > this.minecraft.player.tickCount;
    }

    private boolean willPrioritizeJumpInfo() {
        return this.minecraft.player.getJumpRidingScale() > 0.0f || Optionull.mapOrDefault(this.minecraft.player.jumpableVehicle(), PlayerRideableJumping::getJumpCooldown, 0) > 0;
    }

    private ContextualInfo nextContextualInfoState() {
        boolean $$0 = this.minecraft.player.connection.getWaypointManager().hasWaypoints();
        boolean $$1 = this.minecraft.player.jumpableVehicle() != null;
        boolean $$2 = this.minecraft.gameMode.hasExperience();
        if ($$0) {
            if ($$1 && this.willPrioritizeJumpInfo()) {
                return ContextualInfo.JUMPABLE_VEHICLE;
            }
            if ($$2 && this.willPrioritizeExperienceInfo()) {
                return ContextualInfo.EXPERIENCE;
            }
            return ContextualInfo.LOCATOR;
        }
        if ($$1) {
            return ContextualInfo.JUMPABLE_VEHICLE;
        }
        if ($$2) {
            return ContextualInfo.EXPERIENCE;
        }
        return ContextualInfo.EMPTY;
    }

    static final class ContextualInfo
    extends Enum<ContextualInfo> {
        public static final /* enum */ ContextualInfo EMPTY = new ContextualInfo();
        public static final /* enum */ ContextualInfo EXPERIENCE = new ContextualInfo();
        public static final /* enum */ ContextualInfo LOCATOR = new ContextualInfo();
        public static final /* enum */ ContextualInfo JUMPABLE_VEHICLE = new ContextualInfo();
        private static final /* synthetic */ ContextualInfo[] $VALUES;

        public static ContextualInfo[] values() {
            return (ContextualInfo[])$VALUES.clone();
        }

        public static ContextualInfo valueOf(String $$0) {
            return Enum.valueOf(ContextualInfo.class, $$0);
        }

        private static /* synthetic */ ContextualInfo[] a() {
            return new ContextualInfo[]{EMPTY, EXPERIENCE, LOCATOR, JUMPABLE_VEHICLE};
        }

        static {
            $VALUES = ContextualInfo.a();
        }
    }

    static final class HeartType
    extends Enum<HeartType> {
        public static final /* enum */ HeartType CONTAINER = new HeartType(ResourceLocation.withDefaultNamespace("hud/heart/container"), ResourceLocation.withDefaultNamespace("hud/heart/container_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/container"), ResourceLocation.withDefaultNamespace("hud/heart/container_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore_blinking"));
        public static final /* enum */ HeartType NORMAL = new HeartType(ResourceLocation.withDefaultNamespace("hud/heart/full"), ResourceLocation.withDefaultNamespace("hud/heart/full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/half"), ResourceLocation.withDefaultNamespace("hud/heart/half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/hardcore_half_blinking"));
        public static final /* enum */ HeartType POISIONED = new HeartType(ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_half"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_half_blinking"));
        public static final /* enum */ HeartType WITHERED = new HeartType(ResourceLocation.withDefaultNamespace("hud/heart/withered_full"), ResourceLocation.withDefaultNamespace("hud/heart/withered_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/withered_half"), ResourceLocation.withDefaultNamespace("hud/heart/withered_half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_half_blinking"));
        public static final /* enum */ HeartType ABSORBING = new HeartType(ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_half_blinking"));
        public static final /* enum */ HeartType FROZEN = new HeartType(ResourceLocation.withDefaultNamespace("hud/heart/frozen_full"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_half"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_half_blinking"));
        private final ResourceLocation full;
        private final ResourceLocation fullBlinking;
        private final ResourceLocation half;
        private final ResourceLocation halfBlinking;
        private final ResourceLocation hardcoreFull;
        private final ResourceLocation hardcoreFullBlinking;
        private final ResourceLocation hardcoreHalf;
        private final ResourceLocation hardcoreHalfBlinking;
        private static final /* synthetic */ HeartType[] $VALUES;

        public static HeartType[] values() {
            return (HeartType[])$VALUES.clone();
        }

        public static HeartType valueOf(String $$0) {
            return Enum.valueOf(HeartType.class, $$0);
        }

        private HeartType(ResourceLocation $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3, ResourceLocation $$4, ResourceLocation $$5, ResourceLocation $$6, ResourceLocation $$7) {
            this.full = $$0;
            this.fullBlinking = $$1;
            this.half = $$2;
            this.halfBlinking = $$3;
            this.hardcoreFull = $$4;
            this.hardcoreFullBlinking = $$5;
            this.hardcoreHalf = $$6;
            this.hardcoreHalfBlinking = $$7;
        }

        public ResourceLocation getSprite(boolean $$0, boolean $$1, boolean $$2) {
            if (!$$0) {
                if ($$1) {
                    return $$2 ? this.halfBlinking : this.half;
                }
                return $$2 ? this.fullBlinking : this.full;
            }
            if ($$1) {
                return $$2 ? this.hardcoreHalfBlinking : this.hardcoreHalf;
            }
            return $$2 ? this.hardcoreFullBlinking : this.hardcoreFull;
        }

        static HeartType forPlayer(Player $$0) {
            HeartType $$4;
            if ($$0.hasEffect(MobEffects.POISON)) {
                HeartType $$1 = POISIONED;
            } else if ($$0.hasEffect(MobEffects.WITHER)) {
                HeartType $$2 = WITHERED;
            } else if ($$0.isFullyFrozen()) {
                HeartType $$3 = FROZEN;
            } else {
                $$4 = NORMAL;
            }
            return $$4;
        }

        private static /* synthetic */ HeartType[] a() {
            return new HeartType[]{CONTAINER, NORMAL, POISIONED, WITHERED, ABSORBING, FROZEN};
        }

        static {
            $VALUES = HeartType.a();
        }
    }

    public static interface RenderFunction {
        public void render(GuiGraphics var1, DeltaTracker var2);
    }
}

