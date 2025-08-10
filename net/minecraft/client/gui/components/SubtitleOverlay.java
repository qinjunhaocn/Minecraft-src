/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  java.lang.Record
 *  java.lang.runtime.ObjectMethods
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.audio.ListenerTransform;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SubtitleOverlay
implements SoundEventListener {
    private static final long DISPLAY_TIME = 3000L;
    private final Minecraft minecraft;
    private final List<Subtitle> subtitles = Lists.newArrayList();
    private boolean isListening;
    private final List<Subtitle> audibleSubtitles = new ArrayList<Subtitle>();

    public SubtitleOverlay(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void render(GuiGraphics $$0) {
        SoundManager $$1 = this.minecraft.getSoundManager();
        if (!this.isListening && this.minecraft.options.showSubtitles().get().booleanValue()) {
            $$1.addListener(this);
            this.isListening = true;
        } else if (this.isListening && !this.minecraft.options.showSubtitles().get().booleanValue()) {
            $$1.removeListener(this);
            this.isListening = false;
        }
        if (!this.isListening) {
            return;
        }
        ListenerTransform $$2 = $$1.getListenerTransform();
        Vec3 $$3 = $$2.position();
        Vec3 $$4 = $$2.forward();
        Vec3 $$5 = $$2.right();
        this.audibleSubtitles.clear();
        for (Subtitle $$6 : this.subtitles) {
            if (!$$6.isAudibleFrom($$3)) continue;
            this.audibleSubtitles.add($$6);
        }
        if (this.audibleSubtitles.isEmpty()) {
            return;
        }
        int $$7 = 0;
        int $$8 = 0;
        double $$9 = this.minecraft.options.notificationDisplayTime().get();
        Iterator<Subtitle> $$10 = this.audibleSubtitles.iterator();
        while ($$10.hasNext()) {
            Subtitle $$11 = $$10.next();
            $$11.purgeOldInstances(3000.0 * $$9);
            if (!$$11.isStillActive()) {
                $$10.remove();
                continue;
            }
            $$8 = Math.max($$8, this.minecraft.font.width($$11.getText()));
        }
        $$8 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");
        if (!this.audibleSubtitles.isEmpty()) {
            $$0.nextStratum();
        }
        for (Subtitle $$12 : this.audibleSubtitles) {
            int $$13 = 255;
            Component $$14 = $$12.getText();
            SoundPlayedAt $$15 = $$12.getClosest($$3);
            if ($$15 == null) continue;
            Vec3 $$16 = $$15.location.subtract($$3).normalize();
            double $$17 = $$5.dot($$16);
            double $$18 = $$4.dot($$16);
            boolean $$19 = $$18 > 0.5;
            int $$20 = $$8 / 2;
            int $$21 = this.minecraft.font.lineHeight;
            int $$22 = $$21 / 2;
            float $$23 = 1.0f;
            int $$24 = this.minecraft.font.width($$14);
            int $$25 = Mth.floor(Mth.clampedLerp(255.0f, 75.0f, (float)(Util.getMillis() - $$15.time) / (float)(3000.0 * $$9)));
            $$0.pose().pushMatrix();
            $$0.pose().translate((float)$$0.guiWidth() - (float)$$20 * 1.0f - 2.0f, (float)($$0.guiHeight() - 35) - (float)($$7 * ($$21 + 1)) * 1.0f);
            $$0.pose().scale(1.0f, 1.0f);
            $$0.fill(-$$20 - 1, -$$22 - 1, $$20 + 1, $$22 + 1, this.minecraft.options.getBackgroundColor(0.8f));
            int $$26 = ARGB.color(255, $$25, $$25, $$25);
            if (!$$19) {
                if ($$17 > 0.0) {
                    $$0.drawString(this.minecraft.font, ">", $$20 - this.minecraft.font.width(">"), -$$22, $$26);
                } else if ($$17 < 0.0) {
                    $$0.drawString(this.minecraft.font, "<", -$$20, -$$22, $$26);
                }
            }
            $$0.drawString(this.minecraft.font, $$14, -$$24 / 2, -$$22, $$26);
            $$0.pose().popMatrix();
            ++$$7;
        }
    }

    @Override
    public void onPlaySound(SoundInstance $$0, WeighedSoundEvents $$1, float $$2) {
        if ($$1.getSubtitle() == null) {
            return;
        }
        Component $$3 = $$1.getSubtitle();
        if (!this.subtitles.isEmpty()) {
            for (Subtitle $$4 : this.subtitles) {
                if (!$$4.getText().equals($$3)) continue;
                $$4.refresh(new Vec3($$0.getX(), $$0.getY(), $$0.getZ()));
                return;
            }
        }
        this.subtitles.add(new Subtitle($$3, $$2, new Vec3($$0.getX(), $$0.getY(), $$0.getZ())));
    }

    static class Subtitle {
        private final Component text;
        private final float range;
        private final List<SoundPlayedAt> playedAt = new ArrayList<SoundPlayedAt>();

        public Subtitle(Component $$0, float $$1, Vec3 $$2) {
            this.text = $$0;
            this.range = $$1;
            this.playedAt.add(new SoundPlayedAt($$2, Util.getMillis()));
        }

        public Component getText() {
            return this.text;
        }

        @Nullable
        public SoundPlayedAt getClosest(Vec3 $$0) {
            if (this.playedAt.isEmpty()) {
                return null;
            }
            if (this.playedAt.size() == 1) {
                return (SoundPlayedAt)((Object)this.playedAt.getFirst());
            }
            return this.playedAt.stream().min(Comparator.comparingDouble($$1 -> $$1.location().distanceTo($$0))).orElse(null);
        }

        public void refresh(Vec3 $$0) {
            this.playedAt.removeIf($$1 -> $$0.equals($$1.location()));
            this.playedAt.add(new SoundPlayedAt($$0, Util.getMillis()));
        }

        public boolean isAudibleFrom(Vec3 $$0) {
            if (Float.isInfinite(this.range)) {
                return true;
            }
            if (this.playedAt.isEmpty()) {
                return false;
            }
            SoundPlayedAt $$1 = this.getClosest($$0);
            if ($$1 == null) {
                return false;
            }
            return $$0.closerThan($$1.location, this.range);
        }

        public void purgeOldInstances(double $$0) {
            long $$1 = Util.getMillis();
            this.playedAt.removeIf($$2 -> (double)($$1 - $$2.time()) > $$0);
        }

        public boolean isStillActive() {
            return !this.playedAt.isEmpty();
        }
    }

    static final class SoundPlayedAt
    extends Record {
        final Vec3 location;
        final long time;

        SoundPlayedAt(Vec3 $$0, long $$1) {
            this.location = $$0;
            this.time = $$1;
        }

        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{SoundPlayedAt.class, "location;time", "location", "time"}, this);
        }

        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SoundPlayedAt.class, "location;time", "location", "time"}, this);
        }

        public final boolean equals(Object $$0) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SoundPlayedAt.class, "location;time", "location", "time"}, this, $$0);
        }

        public Vec3 location() {
            return this.location;
        }

        public long time() {
            return this.time;
        }
    }
}

