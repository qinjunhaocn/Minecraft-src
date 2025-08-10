/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class LoadingOverlay
extends Overlay {
    public static final ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/title/mojangstudios.png");
    private static final int LOGO_BACKGROUND_COLOR = ARGB.color(255, 239, 50, 61);
    private static final int LOGO_BACKGROUND_COLOR_DARK = ARGB.color(255, 0, 0, 0);
    private static final IntSupplier BRAND_BACKGROUND = () -> Minecraft.getInstance().options.darkMojangStudiosBackground().get() != false ? LOGO_BACKGROUND_COLOR_DARK : LOGO_BACKGROUND_COLOR;
    private static final int LOGO_SCALE = 240;
    private static final float LOGO_QUARTER_FLOAT = 60.0f;
    private static final int LOGO_QUARTER = 60;
    private static final int LOGO_HALF = 120;
    private static final float LOGO_OVERLAP = 0.0625f;
    private static final float SMOOTHING = 0.95f;
    public static final long FADE_OUT_TIME = 1000L;
    public static final long FADE_IN_TIME = 500L;
    private final Minecraft minecraft;
    private final ReloadInstance reload;
    private final Consumer<Optional<Throwable>> onFinish;
    private final boolean fadeIn;
    private float currentProgress;
    private long fadeOutStart = -1L;
    private long fadeInStart = -1L;

    public LoadingOverlay(Minecraft $$0, ReloadInstance $$1, Consumer<Optional<Throwable>> $$2, boolean $$3) {
        this.minecraft = $$0;
        this.reload = $$1;
        this.onFinish = $$2;
        this.fadeIn = $$3;
    }

    public static void registerTextures(TextureManager $$0) {
        $$0.registerAndLoad(MOJANG_STUDIOS_LOGO_LOCATION, new LogoTexture());
    }

    private static int replaceAlpha(int $$0, int $$1) {
        return $$0 & 0xFFFFFF | $$1 << 24;
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        float $$14;
        float $$8;
        int $$4 = $$0.guiWidth();
        int $$5 = $$0.guiHeight();
        long $$6 = Util.getMillis();
        if (this.fadeIn && this.fadeInStart == -1L) {
            this.fadeInStart = $$6;
        }
        float $$7 = this.fadeOutStart > -1L ? (float)($$6 - this.fadeOutStart) / 1000.0f : -1.0f;
        float f = $$8 = this.fadeInStart > -1L ? (float)($$6 - this.fadeInStart) / 500.0f : -1.0f;
        if ($$7 >= 1.0f) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.renderWithTooltip($$0, 0, 0, $$3);
            }
            int $$9 = Mth.ceil((1.0f - Mth.clamp($$7 - 1.0f, 0.0f, 1.0f)) * 255.0f);
            $$0.nextStratum();
            $$0.fill(0, 0, $$4, $$5, LoadingOverlay.replaceAlpha(BRAND_BACKGROUND.getAsInt(), $$9));
            float $$10 = 1.0f - Mth.clamp($$7 - 1.0f, 0.0f, 1.0f);
        } else if (this.fadeIn) {
            if (this.minecraft.screen != null && $$8 < 1.0f) {
                this.minecraft.screen.renderWithTooltip($$0, $$1, $$2, $$3);
            }
            int $$11 = Mth.ceil(Mth.clamp((double)$$8, 0.15, 1.0) * 255.0);
            $$0.nextStratum();
            $$0.fill(0, 0, $$4, $$5, LoadingOverlay.replaceAlpha(BRAND_BACKGROUND.getAsInt(), $$11));
            float $$12 = Mth.clamp($$8, 0.0f, 1.0f);
        } else {
            int $$13 = BRAND_BACKGROUND.getAsInt();
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.minecraft.getMainRenderTarget().getColorTexture(), $$13);
            $$14 = 1.0f;
        }
        int $$15 = (int)((double)$$0.guiWidth() * 0.5);
        int $$16 = (int)((double)$$0.guiHeight() * 0.5);
        double $$17 = Math.min((double)$$0.guiWidth() * 0.75, (double)$$0.guiHeight()) * 0.25;
        int $$18 = (int)($$17 * 0.5);
        double $$19 = $$17 * 4.0;
        int $$20 = (int)($$19 * 0.5);
        int $$21 = ARGB.white($$14);
        $$0.blit(RenderPipelines.MOJANG_LOGO, MOJANG_STUDIOS_LOGO_LOCATION, $$15 - $$20, $$16 - $$18, -0.0625f, 0.0f, $$20, (int)$$17, 120, 60, 120, 120, $$21);
        $$0.blit(RenderPipelines.MOJANG_LOGO, MOJANG_STUDIOS_LOGO_LOCATION, $$15, $$16 - $$18, 0.0625f, 60.0f, $$20, (int)$$17, 120, 60, 120, 120, $$21);
        int $$22 = (int)((double)$$0.guiHeight() * 0.8325);
        float $$23 = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95f + $$23 * 0.050000012f, 0.0f, 1.0f);
        if ($$7 < 1.0f) {
            this.drawProgressBar($$0, $$4 / 2 - $$20, $$22 - 5, $$4 / 2 + $$20, $$22 + 5, 1.0f - Mth.clamp($$7, 0.0f, 1.0f));
        }
        if ($$7 >= 2.0f) {
            this.minecraft.setOverlay(null);
        }
        if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || $$8 >= 2.0f)) {
            try {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            } catch (Throwable $$24) {
                this.onFinish.accept(Optional.of($$24));
            }
            this.fadeOutStart = Util.getMillis();
            if (this.minecraft.screen != null) {
                this.minecraft.screen.init(this.minecraft, $$0.guiWidth(), $$0.guiHeight());
            }
        }
    }

    private void drawProgressBar(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, float $$5) {
        int $$6 = Mth.ceil((float)($$3 - $$1 - 2) * this.currentProgress);
        int $$7 = Math.round($$5 * 255.0f);
        int $$8 = ARGB.color($$7, 255, 255, 255);
        $$0.fill($$1 + 2, $$2 + 2, $$1 + $$6, $$4 - 2, $$8);
        $$0.fill($$1 + 1, $$2, $$3 - 1, $$2 + 1, $$8);
        $$0.fill($$1 + 1, $$4, $$3 - 1, $$4 - 1, $$8);
        $$0.fill($$1, $$2, $$1 + 1, $$4, $$8);
        $$0.fill($$3, $$2, $$3 - 1, $$4, $$8);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    static class LogoTexture
    extends ReloadableTexture {
        public LogoTexture() {
            super(MOJANG_STUDIOS_LOGO_LOCATION);
        }

        @Override
        public TextureContents loadContents(ResourceManager $$0) throws IOException {
            ResourceProvider $$1 = Minecraft.getInstance().getVanillaPackResources().asProvider();
            try (InputStream $$2 = $$1.open(MOJANG_STUDIOS_LOGO_LOCATION);){
                TextureContents textureContents = new TextureContents(NativeImage.read($$2), new TextureMetadataSection(true, true));
                return textureContents;
            }
        }
    }
}

