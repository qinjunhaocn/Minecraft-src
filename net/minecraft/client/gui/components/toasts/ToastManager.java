/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ToastManager {
    private static final int SLOT_COUNT = 5;
    private static final int ALL_SLOTS_OCCUPIED = -1;
    final Minecraft minecraft;
    private final List<ToastInstance<?>> visibleToasts = new ArrayList();
    private final BitSet occupiedSlots = new BitSet(5);
    private final Deque<Toast> queued = Queues.newArrayDeque();
    private final Set<SoundEvent> playedToastSounds = new HashSet<SoundEvent>();
    @Nullable
    private ToastInstance<NowPlayingToast> nowPlayingToast;

    public ToastManager(Minecraft $$0, Options $$1) {
        this.minecraft = $$0;
        if ($$1.showNowPlayingToast().get().booleanValue()) {
            this.createNowPlayingToast();
        }
    }

    public void update() {
        MutableBoolean $$02 = new MutableBoolean(false);
        this.visibleToasts.removeIf($$1 -> {
            Toast.Visibility $$2 = $$1.visibility;
            $$1.update();
            if ($$1.visibility != $$2 && $$02.isFalse()) {
                $$02.setTrue();
                $$1.visibility.playSound(this.minecraft.getSoundManager());
            }
            if ($$1.hasFinishedRendering()) {
                this.occupiedSlots.clear($$1.firstSlotIndex, $$1.firstSlotIndex + $$1.occupiedSlotCount);
                return true;
            }
            return false;
        });
        if (!this.queued.isEmpty() && this.freeSlotCount() > 0) {
            this.queued.removeIf($$0 -> {
                int $$1 = $$0.occcupiedSlotCount();
                int $$2 = this.findFreeSlotsIndex($$1);
                if ($$2 == -1) {
                    return false;
                }
                this.visibleToasts.add(new ToastInstance(this, $$0, $$2, $$1));
                this.occupiedSlots.set($$2, $$2 + $$1);
                SoundEvent $$3 = $$0.getSoundEvent();
                if ($$3 != null && this.playedToastSounds.add($$3)) {
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI($$3, 1.0f, 1.0f));
                }
                return true;
            });
        }
        this.playedToastSounds.clear();
        if (this.nowPlayingToast != null) {
            this.nowPlayingToast.update();
        }
    }

    public void render(GuiGraphics $$0) {
        if (this.minecraft.options.hideGui) {
            return;
        }
        int $$1 = $$0.guiWidth();
        if (!this.visibleToasts.isEmpty()) {
            $$0.nextStratum();
        }
        for (ToastInstance<?> $$2 : this.visibleToasts) {
            $$2.render($$0, $$1);
        }
        if (this.minecraft.options.showNowPlayingToast().get().booleanValue() && this.nowPlayingToast != null && (this.minecraft.screen == null || !(this.minecraft.screen instanceof PauseScreen))) {
            this.nowPlayingToast.render($$0, $$1);
        }
    }

    private int findFreeSlotsIndex(int $$0) {
        if (this.freeSlotCount() >= $$0) {
            int $$1 = 0;
            for (int $$2 = 0; $$2 < 5; ++$$2) {
                if (this.occupiedSlots.get($$2)) {
                    $$1 = 0;
                    continue;
                }
                if (++$$1 != $$0) continue;
                return $$2 + 1 - $$1;
            }
        }
        return -1;
    }

    private int freeSlotCount() {
        return 5 - this.occupiedSlots.cardinality();
    }

    @Nullable
    public <T extends Toast> T getToast(Class<? extends T> $$0, Object $$1) {
        for (ToastInstance<?> $$2 : this.visibleToasts) {
            if ($$2 == null || !$$0.isAssignableFrom($$2.getToast().getClass()) || !$$2.getToast().getToken().equals($$1)) continue;
            return (T)$$2.getToast();
        }
        for (Toast $$3 : this.queued) {
            if (!$$0.isAssignableFrom($$3.getClass()) || !$$3.getToken().equals($$1)) continue;
            return (T)$$3;
        }
        return null;
    }

    public void clear() {
        this.occupiedSlots.clear();
        this.visibleToasts.clear();
        this.queued.clear();
    }

    public void addToast(Toast $$0) {
        this.queued.add($$0);
    }

    public void showNowPlayingToast() {
        if (this.nowPlayingToast != null) {
            this.nowPlayingToast.resetToast();
            this.nowPlayingToast.getToast().showToast(this.minecraft.options);
        }
    }

    public void hideNowPlayingToast() {
        if (this.nowPlayingToast != null) {
            this.nowPlayingToast.getToast().setWantedVisibility(Toast.Visibility.HIDE);
        }
    }

    public void createNowPlayingToast() {
        this.nowPlayingToast = new ToastInstance(this, (Toast)new NowPlayingToast(), 0, 0);
    }

    public void removeNowPlayingToast() {
        this.nowPlayingToast = null;
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public double getNotificationDisplayTimeMultiplier() {
        return this.minecraft.options.notificationDisplayTime().get();
    }

    class ToastInstance<T extends Toast> {
        private static final long SLIDE_ANIMATION_DURATION_MS = 600L;
        private final T toast;
        final int firstSlotIndex;
        final int occupiedSlotCount;
        private long animationStartTime;
        private long becameFullyVisibleAt;
        Toast.Visibility visibility;
        private long fullyVisibleFor;
        private float visiblePortion;
        protected boolean hasFinishedRendering;
        final /* synthetic */ ToastManager this$0;

        /*
         * WARNING - Possible parameter corruption
         */
        ToastInstance(T $$0, int $$1, int $$2) {
            this.this$0 = (ToastManager)n;
            this.toast = $$0;
            this.firstSlotIndex = $$1;
            this.occupiedSlotCount = $$2;
            this.resetToast();
        }

        public T getToast() {
            return this.toast;
        }

        public void resetToast() {
            this.animationStartTime = -1L;
            this.becameFullyVisibleAt = -1L;
            this.visibility = Toast.Visibility.HIDE;
            this.fullyVisibleFor = 0L;
            this.visiblePortion = 0.0f;
            this.hasFinishedRendering = false;
        }

        public boolean hasFinishedRendering() {
            return this.hasFinishedRendering;
        }

        private void calculateVisiblePortion(long $$0) {
            float $$1 = Mth.clamp((float)($$0 - this.animationStartTime) / 600.0f, 0.0f, 1.0f);
            $$1 *= $$1;
            this.visiblePortion = this.visibility == Toast.Visibility.HIDE ? 1.0f - $$1 : $$1;
        }

        public void update() {
            long $$0 = Util.getMillis();
            if (this.animationStartTime == -1L) {
                this.animationStartTime = $$0;
                this.visibility = Toast.Visibility.SHOW;
            }
            if (this.visibility == Toast.Visibility.SHOW && $$0 - this.animationStartTime <= 600L) {
                this.becameFullyVisibleAt = $$0;
            }
            this.fullyVisibleFor = $$0 - this.becameFullyVisibleAt;
            this.calculateVisiblePortion($$0);
            this.toast.update(this.this$0, this.fullyVisibleFor);
            Toast.Visibility $$1 = this.toast.getWantedVisibility();
            if ($$1 != this.visibility) {
                this.animationStartTime = $$0 - (long)((int)((1.0f - this.visiblePortion) * 600.0f));
                this.visibility = $$1;
            }
            boolean $$2 = this.hasFinishedRendering;
            boolean bl = this.hasFinishedRendering = this.visibility == Toast.Visibility.HIDE && $$0 - this.animationStartTime > 600L;
            if (this.hasFinishedRendering && !$$2) {
                this.toast.onFinishedRendering();
            }
        }

        public void render(GuiGraphics $$0, int $$1) {
            if (this.hasFinishedRendering) {
                return;
            }
            $$0.pose().pushMatrix();
            $$0.pose().translate(this.toast.xPos($$1, this.visiblePortion), this.toast.yPos(this.firstSlotIndex));
            this.toast.render($$0, this.this$0.minecraft.font, this.fullyVisibleFor);
            $$0.pose().popMatrix();
        }
    }
}

