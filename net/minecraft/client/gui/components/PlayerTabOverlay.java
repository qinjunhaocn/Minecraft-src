/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.components;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class PlayerTabOverlay {
    private static final ResourceLocation PING_UNKNOWN_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_unknown");
    private static final ResourceLocation PING_1_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_1");
    private static final ResourceLocation PING_2_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_2");
    private static final ResourceLocation PING_3_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_3");
    private static final ResourceLocation PING_4_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_4");
    private static final ResourceLocation PING_5_SPRITE = ResourceLocation.withDefaultNamespace("icon/ping_5");
    private static final ResourceLocation HEART_CONTAINER_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/container_blinking");
    private static final ResourceLocation HEART_CONTAINER_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/container");
    private static final ResourceLocation HEART_FULL_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/full_blinking");
    private static final ResourceLocation HEART_HALF_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/half_blinking");
    private static final ResourceLocation HEART_ABSORBING_FULL_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full_blinking");
    private static final ResourceLocation HEART_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/full");
    private static final ResourceLocation HEART_ABSORBING_HALF_BLINKING_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half_blinking");
    private static final ResourceLocation HEART_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/half");
    private static final Comparator<PlayerInfo> PLAYER_COMPARATOR = Comparator.comparingInt($$0 -> -$$0.getTabListOrder()).thenComparingInt($$0 -> $$0.getGameMode() == GameType.SPECTATOR ? 1 : 0).thenComparing($$0 -> Optionull.mapOrDefault($$0.getTeam(), PlayerTeam::getName, "")).thenComparing($$0 -> $$0.getProfile().getName(), String::compareToIgnoreCase);
    public static final int MAX_ROWS_PER_COL = 20;
    private final Minecraft minecraft;
    private final Gui gui;
    @Nullable
    private Component footer;
    @Nullable
    private Component header;
    private boolean visible;
    private final Map<UUID, HealthState> healthStates = new Object2ObjectOpenHashMap();

    public PlayerTabOverlay(Minecraft $$0, Gui $$1) {
        this.minecraft = $$0;
        this.gui = $$1;
    }

    public Component getNameForDisplay(PlayerInfo $$0) {
        if ($$0.getTabListDisplayName() != null) {
            return this.decorateName($$0, $$0.getTabListDisplayName().copy());
        }
        return this.decorateName($$0, PlayerTeam.formatNameForTeam($$0.getTeam(), Component.literal($$0.getProfile().getName())));
    }

    private Component decorateName(PlayerInfo $$0, MutableComponent $$1) {
        return $$0.getGameMode() == GameType.SPECTATOR ? $$1.withStyle(ChatFormatting.ITALIC) : $$1;
    }

    public void setVisible(boolean $$0) {
        if (this.visible != $$0) {
            this.healthStates.clear();
            this.visible = $$0;
            if ($$0) {
                MutableComponent $$1 = ComponentUtils.formatList(this.getPlayerInfos(), Component.literal(", "), this::getNameForDisplay);
                this.minecraft.getNarrator().saySystemNow(Component.a("multiplayer.player.list.narration", $$1));
            }
        }
    }

    private List<PlayerInfo> getPlayerInfos() {
        return this.minecraft.player.connection.getListedOnlinePlayers().stream().sorted(PLAYER_COMPARATOR).limit(80L).toList();
    }

    public void render(GuiGraphics $$02, int $$12, Scoreboard $$2, @Nullable Objective $$3) {
        int $$24;
        boolean $$21;
        int $$18;
        List<PlayerInfo> $$4 = this.getPlayerInfos();
        ArrayList<ScoreDisplayEntry> $$5 = new ArrayList<ScoreDisplayEntry>($$4.size());
        int $$6 = this.minecraft.font.width(" ");
        int $$7 = 0;
        int $$8 = 0;
        for (PlayerInfo $$9 : $$4) {
            Component $$10 = this.getNameForDisplay($$9);
            $$7 = Math.max($$7, this.minecraft.font.width($$10));
            int $$11 = 0;
            MutableComponent $$122 = null;
            int $$13 = 0;
            if ($$3 != null) {
                ScoreHolder $$14 = ScoreHolder.fromGameProfile($$9.getProfile());
                ReadOnlyScoreInfo $$15 = $$2.getPlayerScoreInfo($$14, $$3);
                if ($$15 != null) {
                    $$11 = $$15.value();
                }
                if ($$3.getRenderType() != ObjectiveCriteria.RenderType.HEARTS) {
                    NumberFormat $$16 = $$3.numberFormatOrDefault(StyledFormat.PLAYER_LIST_DEFAULT);
                    $$122 = ReadOnlyScoreInfo.safeFormatValue($$15, $$16);
                    $$13 = this.minecraft.font.width($$122);
                    $$8 = Math.max($$8, $$13 > 0 ? $$6 + $$13 : 0);
                }
            }
            $$5.add(new ScoreDisplayEntry($$10, $$11, $$122, $$13));
        }
        if (!this.healthStates.isEmpty()) {
            Set $$17 = $$4.stream().map($$0 -> $$0.getProfile().getId()).collect(Collectors.toSet());
            this.healthStates.keySet().removeIf($$1 -> !$$17.contains($$1));
        }
        int $$19 = $$18 = $$4.size();
        int $$20 = 1;
        while ($$19 > 20) {
            $$19 = ($$18 + ++$$20 - 1) / $$20;
        }
        boolean bl = $$21 = this.minecraft.isLocalServer() || this.minecraft.getConnection().getConnection().isEncrypted();
        if ($$3 != null) {
            if ($$3.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
                int $$22 = 90;
            } else {
                int $$23 = $$8;
            }
        } else {
            $$24 = 0;
        }
        int $$25 = Math.min($$20 * (($$21 ? 9 : 0) + $$7 + $$24 + 13), $$12 - 50) / $$20;
        int $$26 = $$12 / 2 - ($$25 * $$20 + ($$20 - 1) * 5) / 2;
        int $$27 = 10;
        int $$28 = $$25 * $$20 + ($$20 - 1) * 5;
        List<FormattedCharSequence> $$29 = null;
        if (this.header != null) {
            $$29 = this.minecraft.font.split(this.header, $$12 - 50);
            for (FormattedCharSequence formattedCharSequence : $$29) {
                $$28 = Math.max($$28, this.minecraft.font.width(formattedCharSequence));
            }
        }
        List<FormattedCharSequence> $$31 = null;
        if (this.footer != null) {
            $$31 = this.minecraft.font.split(this.footer, $$12 - 50);
            for (FormattedCharSequence $$32 : $$31) {
                $$28 = Math.max($$28, this.minecraft.font.width($$32));
            }
        }
        if ($$29 != null) {
            $$02.fill($$12 / 2 - $$28 / 2 - 1, $$27 - 1, $$12 / 2 + $$28 / 2 + 1, $$27 + $$29.size() * this.minecraft.font.lineHeight, Integer.MIN_VALUE);
            for (FormattedCharSequence $$33 : $$29) {
                int $$34 = this.minecraft.font.width($$33);
                $$02.drawString(this.minecraft.font, $$33, $$12 / 2 - $$34 / 2, $$27, -1);
                $$27 += this.minecraft.font.lineHeight;
            }
            ++$$27;
        }
        $$02.fill($$12 / 2 - $$28 / 2 - 1, $$27 - 1, $$12 / 2 + $$28 / 2 + 1, $$27 + $$19 * 9, Integer.MIN_VALUE);
        int n = this.minecraft.options.getBackgroundColor(0x20FFFFFF);
        for (int $$36 = 0; $$36 < $$18; ++$$36) {
            int $$46;
            int $$47;
            int $$37 = $$36 / $$19;
            int $$38 = $$36 % $$19;
            int $$39 = $$26 + $$37 * $$25 + $$37 * 5;
            int $$40 = $$27 + $$38 * 9;
            $$02.fill($$39, $$40, $$39 + $$25, $$40 + 8, n);
            if ($$36 >= $$4.size()) continue;
            PlayerInfo $$41 = $$4.get($$36);
            ScoreDisplayEntry $$42 = (ScoreDisplayEntry)((Object)$$5.get($$36));
            GameProfile $$43 = $$41.getProfile();
            if ($$21) {
                Player $$44 = this.minecraft.level.getPlayerByUUID($$43.getId());
                boolean $$45 = $$44 != null && LivingEntityRenderer.isEntityUpsideDown($$44);
                PlayerFaceRenderer.draw($$02, $$41.getSkin().texture(), $$39, $$40, 8, $$41.showHat(), $$45, -1);
                $$39 += 9;
            }
            $$02.drawString(this.minecraft.font, $$42.name, $$39, $$40, $$41.getGameMode() == GameType.SPECTATOR ? -1862270977 : -1);
            if ($$3 != null && $$41.getGameMode() != GameType.SPECTATOR && ($$47 = ($$46 = $$39 + $$7 + 1) + $$24) - $$46 > 5) {
                this.renderTablistScore($$3, $$40, $$42, $$46, $$47, $$43.getId(), $$02);
            }
            this.renderPingIcon($$02, $$25, $$39 - ($$21 ? 9 : 0), $$40, $$41);
        }
        if ($$31 != null) {
            $$02.fill($$12 / 2 - $$28 / 2 - 1, ($$27 += $$19 * 9 + 1) - 1, $$12 / 2 + $$28 / 2 + 1, $$27 + $$31.size() * this.minecraft.font.lineHeight, Integer.MIN_VALUE);
            for (FormattedCharSequence $$48 : $$31) {
                int $$49 = this.minecraft.font.width($$48);
                $$02.drawString(this.minecraft.font, $$48, $$12 / 2 - $$49 / 2, $$27, -1);
                $$27 += this.minecraft.font.lineHeight;
            }
        }
    }

    protected void renderPingIcon(GuiGraphics $$0, int $$1, int $$2, int $$3, PlayerInfo $$4) {
        ResourceLocation $$10;
        if ($$4.getLatency() < 0) {
            ResourceLocation $$5 = PING_UNKNOWN_SPRITE;
        } else if ($$4.getLatency() < 150) {
            ResourceLocation $$6 = PING_5_SPRITE;
        } else if ($$4.getLatency() < 300) {
            ResourceLocation $$7 = PING_4_SPRITE;
        } else if ($$4.getLatency() < 600) {
            ResourceLocation $$8 = PING_3_SPRITE;
        } else if ($$4.getLatency() < 1000) {
            ResourceLocation $$9 = PING_2_SPRITE;
        } else {
            $$10 = PING_1_SPRITE;
        }
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, $$10, $$2 + $$1 - 11, $$3, 10, 8);
    }

    private void renderTablistScore(Objective $$0, int $$1, ScoreDisplayEntry $$2, int $$3, int $$4, UUID $$5, GuiGraphics $$6) {
        if ($$0.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
            this.renderTablistHearts($$1, $$3, $$4, $$5, $$6, $$2.score);
        } else if ($$2.formattedScore != null) {
            $$6.drawString(this.minecraft.font, $$2.formattedScore, $$4 - $$2.scoreWidth, $$1, -1);
        }
    }

    private void renderTablistHearts(int $$0, int $$12, int $$2, UUID $$3, GuiGraphics $$4, int $$5) {
        HealthState $$6 = this.healthStates.computeIfAbsent($$3, $$1 -> new HealthState($$5));
        $$6.update($$5, this.gui.getGuiTicks());
        int $$7 = Mth.positiveCeilDiv(Math.max($$5, $$6.displayedValue()), 2);
        int $$8 = Math.max($$5, Math.max($$6.displayedValue(), 20)) / 2;
        boolean $$9 = $$6.isBlinking(this.gui.getGuiTicks());
        if ($$7 <= 0) {
            return;
        }
        int $$10 = Mth.floor(Math.min((float)($$2 - $$12 - 4) / (float)$$8, 9.0f));
        if ($$10 <= 3) {
            MutableComponent $$16;
            float $$11 = Mth.clamp((float)$$5 / 20.0f, 0.0f, 1.0f);
            int $$122 = (int)((1.0f - $$11) * 255.0f) << 16 | (int)($$11 * 255.0f) << 8;
            float $$13 = (float)$$5 / 2.0f;
            MutableComponent $$14 = Component.a("multiplayer.player.list.hp", Float.valueOf($$13));
            if ($$2 - this.minecraft.font.width($$14) >= $$12) {
                MutableComponent $$15 = $$14;
            } else {
                $$16 = Component.literal(Float.toString($$13));
            }
            $$4.drawString(this.minecraft.font, $$16, ($$2 + $$12 - this.minecraft.font.width($$16)) / 2, $$0, ARGB.opaque($$122));
            return;
        }
        ResourceLocation $$17 = $$9 ? HEART_CONTAINER_BLINKING_SPRITE : HEART_CONTAINER_SPRITE;
        for (int $$18 = $$7; $$18 < $$8; ++$$18) {
            $$4.blitSprite(RenderPipelines.GUI_TEXTURED, $$17, $$12 + $$18 * $$10, $$0, 9, 9);
        }
        for (int $$19 = 0; $$19 < $$7; ++$$19) {
            $$4.blitSprite(RenderPipelines.GUI_TEXTURED, $$17, $$12 + $$19 * $$10, $$0, 9, 9);
            if ($$9) {
                if ($$19 * 2 + 1 < $$6.displayedValue()) {
                    $$4.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_FULL_BLINKING_SPRITE, $$12 + $$19 * $$10, $$0, 9, 9);
                }
                if ($$19 * 2 + 1 == $$6.displayedValue()) {
                    $$4.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_HALF_BLINKING_SPRITE, $$12 + $$19 * $$10, $$0, 9, 9);
                }
            }
            if ($$19 * 2 + 1 < $$5) {
                $$4.blitSprite(RenderPipelines.GUI_TEXTURED, $$19 >= 10 ? HEART_ABSORBING_FULL_BLINKING_SPRITE : HEART_FULL_SPRITE, $$12 + $$19 * $$10, $$0, 9, 9);
            }
            if ($$19 * 2 + 1 != $$5) continue;
            $$4.blitSprite(RenderPipelines.GUI_TEXTURED, $$19 >= 10 ? HEART_ABSORBING_HALF_BLINKING_SPRITE : HEART_HALF_SPRITE, $$12 + $$19 * $$10, $$0, 9, 9);
        }
    }

    public void setFooter(@Nullable Component $$0) {
        this.footer = $$0;
    }

    public void setHeader(@Nullable Component $$0) {
        this.header = $$0;
    }

    public void reset() {
        this.header = null;
        this.footer = null;
    }

    static final class ScoreDisplayEntry
    extends Record {
        final Component name;
        final int score;
        @Nullable
        final Component formattedScore;
        final int scoreWidth;

        ScoreDisplayEntry(Component $$0, int $$1, @Nullable Component $$2, int $$3) {
            this.name = $$0;
            this.score = $$1;
            this.formattedScore = $$2;
            this.scoreWidth = $$3;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this, $$0);
        }

        public Component name() {
            return this.name;
        }

        public int score() {
            return this.score;
        }

        @Nullable
        public Component formattedScore() {
            return this.formattedScore;
        }

        public int scoreWidth() {
            return this.scoreWidth;
        }
    }

    static class HealthState {
        private static final long DISPLAY_UPDATE_DELAY = 20L;
        private static final long DECREASE_BLINK_DURATION = 20L;
        private static final long INCREASE_BLINK_DURATION = 10L;
        private int lastValue;
        private int displayedValue;
        private long lastUpdateTick;
        private long blinkUntilTick;

        public HealthState(int $$0) {
            this.displayedValue = $$0;
            this.lastValue = $$0;
        }

        public void update(int $$0, long $$1) {
            if ($$0 != this.lastValue) {
                long $$2 = $$0 < this.lastValue ? 20L : 10L;
                this.blinkUntilTick = $$1 + $$2;
                this.lastValue = $$0;
                this.lastUpdateTick = $$1;
            }
            if ($$1 - this.lastUpdateTick > 20L) {
                this.displayedValue = $$0;
            }
        }

        public int displayedValue() {
            return this.displayedValue;
        }

        public boolean isBlinking(long $$0) {
            return this.blinkUntilTick > $$0 && (this.blinkUntilTick - $$0) % 6L >= 3L;
        }
    }
}

