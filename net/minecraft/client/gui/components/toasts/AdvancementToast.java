/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.toasts;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class AdvancementToast
implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    public static final int DISPLAY_TIME = 5000;
    private final AdvancementHolder advancement;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

    public AdvancementToast(AdvancementHolder $$0) {
        this.advancement = $$0;
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager $$0, long $$1) {
        DisplayInfo $$2 = this.advancement.value().display().orElse(null);
        if ($$2 == null) {
            this.wantedVisibility = Toast.Visibility.HIDE;
            return;
        }
        this.wantedVisibility = (double)$$1 >= 5000.0 * $$0.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    @Nullable
    public SoundEvent getSoundEvent() {
        return this.isChallengeAdvancement() ? SoundEvents.UI_TOAST_CHALLENGE_COMPLETE : null;
    }

    private boolean isChallengeAdvancement() {
        Optional<DisplayInfo> $$0 = this.advancement.value().display();
        return $$0.isPresent() && $$0.get().getType().equals(AdvancementType.CHALLENGE);
    }

    @Override
    public void render(GuiGraphics $$0, Font $$1, long $$2) {
        int $$5;
        DisplayInfo $$3 = this.advancement.value().display().orElse(null);
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        if ($$3 == null) {
            return;
        }
        List<FormattedCharSequence> $$4 = $$1.split($$3.getTitle(), 125);
        int n = $$5 = $$3.getType() == AdvancementType.CHALLENGE ? -30465 : -256;
        if ($$4.size() == 1) {
            $$0.drawString($$1, $$3.getType().getDisplayName(), 30, 7, $$5, false);
            $$0.drawString($$1, $$4.get(0), 30, 18, -1, false);
        } else {
            int $$6 = 1500;
            float $$7 = 300.0f;
            if ($$2 < 1500L) {
                int $$8 = Mth.floor(Mth.clamp((float)(1500L - $$2) / 300.0f, 0.0f, 1.0f) * 255.0f);
                $$0.drawString($$1, $$3.getType().getDisplayName(), 30, 11, ARGB.color($$8, $$5), false);
            } else {
                int $$9 = Mth.floor(Mth.clamp((float)($$2 - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f);
                int $$10 = this.height() / 2 - $$4.size() * $$1.lineHeight / 2;
                for (FormattedCharSequence $$11 : $$4) {
                    $$0.drawString($$1, $$11, 30, $$10, ARGB.color($$9, -1), false);
                    $$10 += $$1.lineHeight;
                }
            }
        }
        $$0.renderFakeItem($$3.getIcon(), 8, 8);
    }
}

