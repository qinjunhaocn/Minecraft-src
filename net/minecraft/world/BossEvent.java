/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world;

import com.mojang.serialization.Codec;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public abstract class BossEvent {
    private final UUID id;
    protected Component name;
    protected float progress;
    protected BossBarColor color;
    protected BossBarOverlay overlay;
    protected boolean darkenScreen;
    protected boolean playBossMusic;
    protected boolean createWorldFog;

    public BossEvent(UUID $$0, Component $$1, BossBarColor $$2, BossBarOverlay $$3) {
        this.id = $$0;
        this.name = $$1;
        this.color = $$2;
        this.overlay = $$3;
        this.progress = 1.0f;
    }

    public UUID getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    public void setName(Component $$0) {
        this.name = $$0;
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float $$0) {
        this.progress = $$0;
    }

    public BossBarColor getColor() {
        return this.color;
    }

    public void setColor(BossBarColor $$0) {
        this.color = $$0;
    }

    public BossBarOverlay getOverlay() {
        return this.overlay;
    }

    public void setOverlay(BossBarOverlay $$0) {
        this.overlay = $$0;
    }

    public boolean shouldDarkenScreen() {
        return this.darkenScreen;
    }

    public BossEvent setDarkenScreen(boolean $$0) {
        this.darkenScreen = $$0;
        return this;
    }

    public boolean shouldPlayBossMusic() {
        return this.playBossMusic;
    }

    public BossEvent setPlayBossMusic(boolean $$0) {
        this.playBossMusic = $$0;
        return this;
    }

    public BossEvent setCreateWorldFog(boolean $$0) {
        this.createWorldFog = $$0;
        return this;
    }

    public boolean shouldCreateWorldFog() {
        return this.createWorldFog;
    }

    public static final class BossBarColor
    extends Enum<BossBarColor>
    implements StringRepresentable {
        public static final /* enum */ BossBarColor PINK = new BossBarColor("pink", ChatFormatting.RED);
        public static final /* enum */ BossBarColor BLUE = new BossBarColor("blue", ChatFormatting.BLUE);
        public static final /* enum */ BossBarColor RED = new BossBarColor("red", ChatFormatting.DARK_RED);
        public static final /* enum */ BossBarColor GREEN = new BossBarColor("green", ChatFormatting.GREEN);
        public static final /* enum */ BossBarColor YELLOW = new BossBarColor("yellow", ChatFormatting.YELLOW);
        public static final /* enum */ BossBarColor PURPLE = new BossBarColor("purple", ChatFormatting.DARK_BLUE);
        public static final /* enum */ BossBarColor WHITE = new BossBarColor("white", ChatFormatting.WHITE);
        public static final Codec<BossBarColor> CODEC;
        private final String name;
        private final ChatFormatting formatting;
        private static final /* synthetic */ BossBarColor[] $VALUES;

        public static BossBarColor[] values() {
            return (BossBarColor[])$VALUES.clone();
        }

        public static BossBarColor valueOf(String $$0) {
            return Enum.valueOf(BossBarColor.class, $$0);
        }

        private BossBarColor(String $$0, ChatFormatting $$1) {
            this.name = $$0;
            this.formatting = $$1;
        }

        public ChatFormatting getFormatting() {
            return this.formatting;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ BossBarColor[] d() {
            return new BossBarColor[]{PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE};
        }

        static {
            $VALUES = BossBarColor.d();
            CODEC = StringRepresentable.fromEnum(BossBarColor::values);
        }
    }

    public static final class BossBarOverlay
    extends Enum<BossBarOverlay>
    implements StringRepresentable {
        public static final /* enum */ BossBarOverlay PROGRESS = new BossBarOverlay("progress");
        public static final /* enum */ BossBarOverlay NOTCHED_6 = new BossBarOverlay("notched_6");
        public static final /* enum */ BossBarOverlay NOTCHED_10 = new BossBarOverlay("notched_10");
        public static final /* enum */ BossBarOverlay NOTCHED_12 = new BossBarOverlay("notched_12");
        public static final /* enum */ BossBarOverlay NOTCHED_20 = new BossBarOverlay("notched_20");
        public static final Codec<BossBarOverlay> CODEC;
        private final String name;
        private static final /* synthetic */ BossBarOverlay[] $VALUES;

        public static BossBarOverlay[] values() {
            return (BossBarOverlay[])$VALUES.clone();
        }

        public static BossBarOverlay valueOf(String $$0) {
            return Enum.valueOf(BossBarOverlay.class, $$0);
        }

        private BossBarOverlay(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        private static /* synthetic */ BossBarOverlay[] b() {
            return new BossBarOverlay[]{PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20};
        }

        static {
            $VALUES = BossBarOverlay.b();
            CODEC = StringRepresentable.fromEnum(BossBarOverlay::values);
        }
    }
}

