/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.toasts;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.color.ColorLerper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class NowPlayingToast
implements Toast {
    private static final ResourceLocation NOW_PLAYING_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/now_playing");
    private static final ResourceLocation MUSIC_NOTES_SPRITE = ResourceLocation.parse("icon/music_notes");
    private static final int PADDING = 7;
    private static final int MUSIC_NOTES_SIZE = 16;
    private static final int HEIGHT = 30;
    private static final int MUSIC_NOTES_SPACE = 30;
    private static final int VISIBILITY_DURATION = 5000;
    private static final int TEXT_COLOR = DyeColor.LIGHT_GRAY.getTextColor();
    private static final long MUSIC_COLOR_CHANGE_FREQUENCY_MS = 25L;
    private static int musicNoteColorTick;
    private static long lastMusicNoteColorChange;
    private static int musicNoteColor;
    private boolean updateToast;
    private double notificationDisplayTimeMultiplier;
    @Nullable
    private static String currentSong;
    private final Minecraft minecraft;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

    public NowPlayingToast() {
        this.minecraft = Minecraft.getInstance();
    }

    public static void renderToast(GuiGraphics $$0, Font $$1) {
        if (currentSong != null) {
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, NOW_PLAYING_BACKGROUND_SPRITE, 0, 0, NowPlayingToast.getWidth(currentSong, $$1), 30);
            int $$2 = 7;
            $$0.blitSprite(RenderPipelines.GUI_TEXTURED, MUSIC_NOTES_SPRITE, 7, 7, 16, 16, musicNoteColor);
            $$0.drawString($$1, NowPlayingToast.getNowPlayingString(currentSong), 30, 15 - $$1.lineHeight / 2, TEXT_COLOR);
        }
    }

    public static void tickMusicNotes() {
        long $$0;
        currentSong = Minecraft.getInstance().getMusicManager().getCurrentMusicTranslationKey();
        if (currentSong != null && ($$0 = System.currentTimeMillis()) > lastMusicNoteColorChange + 25L) {
            lastMusicNoteColorChange = $$0;
            musicNoteColor = ColorLerper.getLerpedColor(ColorLerper.Type.MUSIC_NOTE, ++musicNoteColorTick);
        }
    }

    private static Component getNowPlayingString(@Nullable String $$0) {
        if ($$0 == null) {
            return Component.empty();
        }
        return Component.translatable($$0.replace("/", "."));
    }

    public void showToast(Options $$0) {
        this.updateToast = true;
        this.notificationDisplayTimeMultiplier = $$0.notificationDisplayTime().get();
        this.setWantedVisibility(Toast.Visibility.SHOW);
    }

    @Override
    public void update(ToastManager $$0, long $$1) {
        if (this.updateToast) {
            this.wantedVisibility = (double)$$1 < 5000.0 * this.notificationDisplayTimeMultiplier ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
            NowPlayingToast.tickMusicNotes();
        }
    }

    @Override
    public void render(GuiGraphics $$0, Font $$1, long $$2) {
        NowPlayingToast.renderToast($$0, $$1);
    }

    @Override
    public void onFinishedRendering() {
        this.updateToast = false;
    }

    @Override
    public int width() {
        return NowPlayingToast.getWidth(currentSong, this.minecraft.font);
    }

    private static int getWidth(@Nullable String $$0, Font $$1) {
        return 30 + $$1.width(NowPlayingToast.getNowPlayingString($$0)) + 7;
    }

    @Override
    public int height() {
        return 30;
    }

    @Override
    public float xPos(int $$0, float $$1) {
        return (float)this.width() * $$1 - (float)this.width();
    }

    @Override
    public float yPos(int $$0) {
        return 0.0f;
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    public void setWantedVisibility(Toast.Visibility $$0) {
        this.wantedVisibility = $$0;
    }

    static {
        musicNoteColor = -1;
    }
}

