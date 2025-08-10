/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.BossEvent;

public class BossHealthOverlay {
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final ResourceLocation[] BAR_BACKGROUND_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("boss_bar/pink_background"), ResourceLocation.withDefaultNamespace("boss_bar/blue_background"), ResourceLocation.withDefaultNamespace("boss_bar/red_background"), ResourceLocation.withDefaultNamespace("boss_bar/green_background"), ResourceLocation.withDefaultNamespace("boss_bar/yellow_background"), ResourceLocation.withDefaultNamespace("boss_bar/purple_background"), ResourceLocation.withDefaultNamespace("boss_bar/white_background")};
    private static final ResourceLocation[] BAR_PROGRESS_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("boss_bar/pink_progress"), ResourceLocation.withDefaultNamespace("boss_bar/blue_progress"), ResourceLocation.withDefaultNamespace("boss_bar/red_progress"), ResourceLocation.withDefaultNamespace("boss_bar/green_progress"), ResourceLocation.withDefaultNamespace("boss_bar/yellow_progress"), ResourceLocation.withDefaultNamespace("boss_bar/purple_progress"), ResourceLocation.withDefaultNamespace("boss_bar/white_progress")};
    private static final ResourceLocation[] OVERLAY_BACKGROUND_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("boss_bar/notched_6_background"), ResourceLocation.withDefaultNamespace("boss_bar/notched_10_background"), ResourceLocation.withDefaultNamespace("boss_bar/notched_12_background"), ResourceLocation.withDefaultNamespace("boss_bar/notched_20_background")};
    private static final ResourceLocation[] OVERLAY_PROGRESS_SPRITES = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("boss_bar/notched_6_progress"), ResourceLocation.withDefaultNamespace("boss_bar/notched_10_progress"), ResourceLocation.withDefaultNamespace("boss_bar/notched_12_progress"), ResourceLocation.withDefaultNamespace("boss_bar/notched_20_progress")};
    private final Minecraft minecraft;
    final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();

    public BossHealthOverlay(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void render(GuiGraphics $$0) {
        if (this.events.isEmpty()) {
            return;
        }
        $$0.nextStratum();
        ProfilerFiller $$1 = Profiler.get();
        $$1.push("bossHealth");
        int $$2 = $$0.guiWidth();
        int $$3 = 12;
        for (LerpingBossEvent $$4 : this.events.values()) {
            int $$5 = $$2 / 2 - 91;
            int $$6 = $$3;
            this.drawBar($$0, $$5, $$6, $$4);
            Component $$7 = $$4.getName();
            int $$8 = this.minecraft.font.width($$7);
            int $$9 = $$2 / 2 - $$8 / 2;
            int $$10 = $$6 - 9;
            $$0.drawString(this.minecraft.font, $$7, $$9, $$10, -1);
            if (($$3 += 10 + this.minecraft.font.lineHeight) < $$0.guiHeight() / 3) continue;
            break;
        }
        $$1.pop();
    }

    private void drawBar(GuiGraphics $$0, int $$1, int $$2, BossEvent $$3) {
        this.a($$0, $$1, $$2, $$3, 182, BAR_BACKGROUND_SPRITES, OVERLAY_BACKGROUND_SPRITES);
        int $$4 = Mth.lerpDiscrete($$3.getProgress(), 0, 182);
        if ($$4 > 0) {
            this.a($$0, $$1, $$2, $$3, $$4, BAR_PROGRESS_SPRITES, OVERLAY_PROGRESS_SPRITES);
        }
    }

    private void a(GuiGraphics $$0, int $$1, int $$2, BossEvent $$3, int $$4, ResourceLocation[] $$5, ResourceLocation[] $$6) {
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$5[$$3.getColor().ordinal()], 182, 5, 0, 0, $$1, $$2, $$4, 5);
        if ($$3.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$6[$$3.getOverlay().ordinal() - 1], 182, 5, 0, 0, $$1, $$2, $$4, 5);
        }
    }

    public void update(ClientboundBossEventPacket $$0) {
        $$0.dispatch(new ClientboundBossEventPacket.Handler(){

            @Override
            public void add(UUID $$0, Component $$1, float $$2, BossEvent.BossBarColor $$3, BossEvent.BossBarOverlay $$4, boolean $$5, boolean $$6, boolean $$7) {
                BossHealthOverlay.this.events.put($$0, new LerpingBossEvent($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7));
            }

            @Override
            public void remove(UUID $$0) {
                BossHealthOverlay.this.events.remove($$0);
            }

            @Override
            public void updateProgress(UUID $$0, float $$1) {
                BossHealthOverlay.this.events.get($$0).setProgress($$1);
            }

            @Override
            public void updateName(UUID $$0, Component $$1) {
                BossHealthOverlay.this.events.get($$0).setName($$1);
            }

            @Override
            public void updateStyle(UUID $$0, BossEvent.BossBarColor $$1, BossEvent.BossBarOverlay $$2) {
                LerpingBossEvent $$3 = BossHealthOverlay.this.events.get($$0);
                $$3.setColor($$1);
                $$3.setOverlay($$2);
            }

            @Override
            public void updateProperties(UUID $$0, boolean $$1, boolean $$2, boolean $$3) {
                LerpingBossEvent $$4 = BossHealthOverlay.this.events.get($$0);
                $$4.setDarkenScreen($$1);
                $$4.setPlayBossMusic($$2);
                $$4.setCreateWorldFog($$3);
            }
        });
    }

    public void reset() {
        this.events.clear();
    }

    public boolean shouldPlayMusic() {
        if (!this.events.isEmpty()) {
            for (BossEvent bossEvent : this.events.values()) {
                if (!bossEvent.shouldPlayBossMusic()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldDarkenScreen() {
        if (!this.events.isEmpty()) {
            for (BossEvent bossEvent : this.events.values()) {
                if (!bossEvent.shouldDarkenScreen()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldCreateWorldFog() {
        if (!this.events.isEmpty()) {
            for (BossEvent bossEvent : this.events.values()) {
                if (!bossEvent.shouldCreateWorldFog()) continue;
                return true;
            }
        }
        return false;
    }
}

