/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.toasts;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class TutorialToast
implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/tutorial");
    public static final int PROGRESS_BAR_WIDTH = 154;
    public static final int PROGRESS_BAR_HEIGHT = 1;
    public static final int PROGRESS_BAR_X = 3;
    public static final int PROGRESS_BAR_MARGIN_BOTTOM = 4;
    private static final int PADDING_TOP = 7;
    private static final int PADDING_BOTTOM = 3;
    private static final int LINE_SPACING = 11;
    private static final int TEXT_LEFT = 30;
    private static final int TEXT_WIDTH = 126;
    private final Icons icon;
    private final List<FormattedCharSequence> lines;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private long lastSmoothingTime;
    private float smoothedProgress;
    private float progress;
    private final boolean progressable;
    private final int timeToDisplayMs;

    public TutorialToast(Font $$0, Icons $$1, Component $$2, @Nullable Component $$3, boolean $$4, int $$5) {
        this.icon = $$1;
        this.lines = new ArrayList<FormattedCharSequence>(2);
        this.lines.addAll($$0.split($$2.copy().withColor(-11534256), 126));
        if ($$3 != null) {
            this.lines.addAll($$0.split($$3, 126));
        }
        this.progressable = $$4;
        this.timeToDisplayMs = $$5;
    }

    public TutorialToast(Font $$0, Icons $$1, Component $$2, @Nullable Component $$3, boolean $$4) {
        this($$0, $$1, $$2, $$3, $$4, 0);
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager $$0, long $$1) {
        if (this.timeToDisplayMs > 0) {
            this.smoothedProgress = this.progress = Math.min((float)$$1 / (float)this.timeToDisplayMs, 1.0f);
            this.lastSmoothingTime = $$1;
            if ($$1 > (long)this.timeToDisplayMs) {
                this.hide();
            }
        } else if (this.progressable) {
            this.smoothedProgress = Mth.clampedLerp(this.smoothedProgress, this.progress, (float)($$1 - this.lastSmoothingTime) / 100.0f);
            this.lastSmoothingTime = $$1;
        }
    }

    @Override
    public int height() {
        return 7 + this.contentHeight() + 3;
    }

    private int contentHeight() {
        return Math.max(this.lines.size(), 2) * 11;
    }

    @Override
    public void render(GuiGraphics $$0, Font $$1, long $$2) {
        int $$3 = this.height();
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), $$3);
        this.icon.render($$0, 6, 6);
        int $$4 = this.lines.size() * 11;
        int $$5 = 7 + (this.contentHeight() - $$4) / 2;
        for (int $$6 = 0; $$6 < this.lines.size(); ++$$6) {
            $$0.drawString($$1, this.lines.get($$6), 30, $$5 + $$6 * 11, -16777216, false);
        }
        if (this.progressable) {
            int $$9;
            int $$7 = $$3 - 4;
            $$0.fill(3, $$7, 157, $$7 + 1, -1);
            if (this.progress >= this.smoothedProgress) {
                int $$8 = -16755456;
            } else {
                $$9 = -11206656;
            }
            $$0.fill(3, $$7, (int)(3.0f + 154.0f * this.smoothedProgress), $$7 + 1, $$9);
        }
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    public void updateProgress(float $$0) {
        this.progress = $$0;
    }

    public static final class Icons
    extends Enum<Icons> {
        public static final /* enum */ Icons MOVEMENT_KEYS = new Icons(ResourceLocation.withDefaultNamespace("toast/movement_keys"));
        public static final /* enum */ Icons MOUSE = new Icons(ResourceLocation.withDefaultNamespace("toast/mouse"));
        public static final /* enum */ Icons TREE = new Icons(ResourceLocation.withDefaultNamespace("toast/tree"));
        public static final /* enum */ Icons RECIPE_BOOK = new Icons(ResourceLocation.withDefaultNamespace("toast/recipe_book"));
        public static final /* enum */ Icons WOODEN_PLANKS = new Icons(ResourceLocation.withDefaultNamespace("toast/wooden_planks"));
        public static final /* enum */ Icons SOCIAL_INTERACTIONS = new Icons(ResourceLocation.withDefaultNamespace("toast/social_interactions"));
        public static final /* enum */ Icons RIGHT_CLICK = new Icons(ResourceLocation.withDefaultNamespace("toast/right_click"));
        private final ResourceLocation sprite;
        private static final /* synthetic */ Icons[] $VALUES;

        public static Icons[] values() {
            return (Icons[])$VALUES.clone();
        }

        public static Icons valueOf(String $$0) {
            return Enum.valueOf(Icons.class, $$0);
        }

        private Icons(ResourceLocation $$0) {
            this.sprite = $$0;
        }

        public void render(GuiGraphics $$0, int $$1, int $$2) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, $$1, $$2, 20, 20);
        }

        private static /* synthetic */ Icons[] a() {
            return new Icons[]{MOVEMENT_KEYS, MOUSE, TREE, RECIPE_BOOK, WOODEN_PLANKS, SOCIAL_INTERACTIONS, RIGHT_CLICK};
        }

        static {
            $VALUES = Icons.a();
        }
    }
}

