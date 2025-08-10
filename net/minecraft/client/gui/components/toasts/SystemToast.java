/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ChunkPos;

public class SystemToast
implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/system");
    private static final int MAX_LINE_SIZE = 200;
    private static final int LINE_SPACING = 12;
    private static final int MARGIN = 10;
    private final SystemToastId id;
    private Component title;
    private List<FormattedCharSequence> messageLines;
    private long lastChanged;
    private boolean changed;
    private final int width;
    private boolean forceHide;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

    public SystemToast(SystemToastId $$0, Component $$1, @Nullable Component $$2) {
        this($$0, $$1, SystemToast.nullToEmpty($$2), Math.max(160, 30 + Math.max(Minecraft.getInstance().font.width($$1), $$2 == null ? 0 : Minecraft.getInstance().font.width($$2))));
    }

    public static SystemToast multiline(Minecraft $$0, SystemToastId $$1, Component $$2, Component $$3) {
        Font $$4 = $$0.font;
        List<FormattedCharSequence> $$5 = $$4.split($$3, 200);
        int $$6 = Math.max(200, $$5.stream().mapToInt($$4::width).max().orElse(200));
        return new SystemToast($$1, $$2, $$5, $$6 + 30);
    }

    private SystemToast(SystemToastId $$0, Component $$1, List<FormattedCharSequence> $$2, int $$3) {
        this.id = $$0;
        this.title = $$1;
        this.messageLines = $$2;
        this.width = $$3;
    }

    private static ImmutableList<FormattedCharSequence> nullToEmpty(@Nullable Component $$0) {
        return $$0 == null ? ImmutableList.of() : ImmutableList.of($$0.getVisualOrderText());
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return 20 + Math.max(this.messageLines.size(), 1) * 12;
    }

    public void forceHide() {
        this.forceHide = true;
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager $$0, long $$1) {
        if (this.changed) {
            this.lastChanged = $$1;
            this.changed = false;
        }
        double $$2 = (double)this.id.displayTime * $$0.getNotificationDisplayTimeMultiplier();
        long $$3 = $$1 - this.lastChanged;
        this.wantedVisibility = !this.forceHide && (double)$$3 < $$2 ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    @Override
    public void render(GuiGraphics $$0, Font $$1, long $$2) {
        $$0.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        if (this.messageLines.isEmpty()) {
            $$0.drawString($$1, this.title, 18, 12, -256, false);
        } else {
            $$0.drawString($$1, this.title, 18, 7, -256, false);
            for (int $$3 = 0; $$3 < this.messageLines.size(); ++$$3) {
                $$0.drawString($$1, this.messageLines.get($$3), 18, 18 + $$3 * 12, -1, false);
            }
        }
    }

    public void reset(Component $$0, @Nullable Component $$1) {
        this.title = $$0;
        this.messageLines = SystemToast.nullToEmpty($$1);
        this.changed = true;
    }

    @Override
    public SystemToastId getToken() {
        return this.id;
    }

    public static void add(ToastManager $$0, SystemToastId $$1, Component $$2, @Nullable Component $$3) {
        $$0.addToast(new SystemToast($$1, $$2, $$3));
    }

    public static void addOrUpdate(ToastManager $$0, SystemToastId $$1, Component $$2, @Nullable Component $$3) {
        SystemToast $$4 = $$0.getToast(SystemToast.class, $$1);
        if ($$4 == null) {
            SystemToast.add($$0, $$1, $$2, $$3);
        } else {
            $$4.reset($$2, $$3);
        }
    }

    public static void forceHide(ToastManager $$0, SystemToastId $$1) {
        SystemToast $$2 = $$0.getToast(SystemToast.class, $$1);
        if ($$2 != null) {
            $$2.forceHide();
        }
    }

    public static void onWorldAccessFailure(Minecraft $$0, String $$1) {
        SystemToast.add($$0.getToastManager(), SystemToastId.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.access_failure"), Component.literal($$1));
    }

    public static void onWorldDeleteFailure(Minecraft $$0, String $$1) {
        SystemToast.add($$0.getToastManager(), SystemToastId.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.delete_failure"), Component.literal($$1));
    }

    public static void onPackCopyFailure(Minecraft $$0, String $$1) {
        SystemToast.add($$0.getToastManager(), SystemToastId.PACK_COPY_FAILURE, Component.translatable("pack.copyFailure"), Component.literal($$1));
    }

    public static void onFileDropFailure(Minecraft $$0, int $$1) {
        SystemToast.add($$0.getToastManager(), SystemToastId.FILE_DROP_FAILURE, Component.translatable("gui.fileDropFailure.title"), Component.a("gui.fileDropFailure.detail", $$1));
    }

    public static void onLowDiskSpace(Minecraft $$0) {
        SystemToast.addOrUpdate($$0.getToastManager(), SystemToastId.LOW_DISK_SPACE, Component.translatable("chunk.toast.lowDiskSpace"), Component.translatable("chunk.toast.lowDiskSpace.description"));
    }

    public static void onChunkLoadFailure(Minecraft $$0, ChunkPos $$1) {
        SystemToast.addOrUpdate($$0.getToastManager(), SystemToastId.CHUNK_LOAD_FAILURE, Component.a("chunk.toast.loadFailure", Component.translationArg($$1)).withStyle(ChatFormatting.RED), Component.translatable("chunk.toast.checkLog"));
    }

    public static void onChunkSaveFailure(Minecraft $$0, ChunkPos $$1) {
        SystemToast.addOrUpdate($$0.getToastManager(), SystemToastId.CHUNK_SAVE_FAILURE, Component.a("chunk.toast.saveFailure", Component.translationArg($$1)).withStyle(ChatFormatting.RED), Component.translatable("chunk.toast.checkLog"));
    }

    @Override
    public /* synthetic */ Object getToken() {
        return this.getToken();
    }

    public static class SystemToastId {
        public static final SystemToastId NARRATOR_TOGGLE = new SystemToastId();
        public static final SystemToastId WORLD_BACKUP = new SystemToastId();
        public static final SystemToastId PACK_LOAD_FAILURE = new SystemToastId();
        public static final SystemToastId WORLD_ACCESS_FAILURE = new SystemToastId();
        public static final SystemToastId PACK_COPY_FAILURE = new SystemToastId();
        public static final SystemToastId FILE_DROP_FAILURE = new SystemToastId();
        public static final SystemToastId PERIODIC_NOTIFICATION = new SystemToastId();
        public static final SystemToastId LOW_DISK_SPACE = new SystemToastId(10000L);
        public static final SystemToastId CHUNK_LOAD_FAILURE = new SystemToastId();
        public static final SystemToastId CHUNK_SAVE_FAILURE = new SystemToastId();
        public static final SystemToastId UNSECURE_SERVER_WARNING = new SystemToastId(10000L);
        final long displayTime;

        public SystemToastId(long $$0) {
            this.displayTime = $$0;
        }

        public SystemToastId() {
            this(5000L);
        }
    }
}

