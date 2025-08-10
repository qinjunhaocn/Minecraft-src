/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.toasts;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public interface Toast {
    public static final Object NO_TOKEN = new Object();
    public static final int DEFAULT_WIDTH = 160;
    public static final int SLOT_HEIGHT = 32;

    public Visibility getWantedVisibility();

    public void update(ToastManager var1, long var2);

    @Nullable
    default public SoundEvent getSoundEvent() {
        return null;
    }

    public void render(GuiGraphics var1, Font var2, long var3);

    default public Object getToken() {
        return NO_TOKEN;
    }

    default public float xPos(int $$0, float $$1) {
        return (float)$$0 - (float)this.width() * $$1;
    }

    default public float yPos(int $$0) {
        return $$0 * this.height();
    }

    default public int width() {
        return 160;
    }

    default public int height() {
        return 32;
    }

    default public int occcupiedSlotCount() {
        return Mth.positiveCeilDiv(this.height(), 32);
    }

    default public void onFinishedRendering() {
    }

    public static final class Visibility
    extends Enum<Visibility> {
        public static final /* enum */ Visibility SHOW = new Visibility(SoundEvents.UI_TOAST_IN);
        public static final /* enum */ Visibility HIDE = new Visibility(SoundEvents.UI_TOAST_OUT);
        private final SoundEvent soundEvent;
        private static final /* synthetic */ Visibility[] $VALUES;

        public static Visibility[] values() {
            return (Visibility[])$VALUES.clone();
        }

        public static Visibility valueOf(String $$0) {
            return Enum.valueOf(Visibility.class, $$0);
        }

        private Visibility(SoundEvent $$0) {
            this.soundEvent = $$0;
        }

        public void playSound(SoundManager $$0) {
            $$0.play(SimpleSoundInstance.forUI(this.soundEvent, 1.0f, 1.0f));
        }

        private static /* synthetic */ Visibility[] a() {
            return new Visibility[]{SHOW, HIDE};
        }

        static {
            $VALUES = Visibility.a();
        }
    }
}

