/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.Callbacks
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.glfw.GLFWWindowCloseCallback
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.MacosUtil;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public final class Window
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int BASE_WIDTH = 320;
    public static final int BASE_HEIGHT = 240;
    private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
    private final WindowEventHandler eventHandler;
    private final ScreenManager screenManager;
    private final long window;
    private int windowedX;
    private int windowedY;
    private int windowedWidth;
    private int windowedHeight;
    private Optional<VideoMode> preferredFullscreenVideoMode;
    private boolean fullscreen;
    private boolean actuallyFullscreen;
    private int x;
    private int y;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int guiScaledWidth;
    private int guiScaledHeight;
    private int guiScale;
    private String errorSection = "";
    private boolean dirty;
    private boolean vsync;
    private boolean iconified;
    private boolean minimized;

    public Window(WindowEventHandler $$0, ScreenManager $$1, DisplayData $$2, @Nullable String $$3, String $$4) {
        this.screenManager = $$1;
        this.setBootErrorCallback();
        this.setErrorSection("Pre startup");
        this.eventHandler = $$0;
        Optional<VideoMode> $$5 = VideoMode.read($$3);
        this.preferredFullscreenVideoMode = $$5.isPresent() ? $$5 : ($$2.fullscreenWidth().isPresent() && $$2.fullscreenHeight().isPresent() ? Optional.of(new VideoMode($$2.fullscreenWidth().getAsInt(), $$2.fullscreenHeight().getAsInt(), 8, 8, 8, 60)) : Optional.empty());
        this.actuallyFullscreen = this.fullscreen = $$2.isFullscreen();
        Monitor $$6 = $$1.getMonitor(GLFW.glfwGetPrimaryMonitor());
        this.windowedWidth = this.width = Math.max($$2.width(), 1);
        this.windowedHeight = this.height = Math.max($$2.height(), 1);
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint((int)139265, (int)196609);
        GLFW.glfwWindowHint((int)139275, (int)221185);
        GLFW.glfwWindowHint((int)139266, (int)3);
        GLFW.glfwWindowHint((int)139267, (int)2);
        GLFW.glfwWindowHint((int)139272, (int)204801);
        GLFW.glfwWindowHint((int)139270, (int)1);
        this.window = GLFW.glfwCreateWindow((int)this.width, (int)this.height, (CharSequence)$$4, (long)(this.fullscreen && $$6 != null ? $$6.getMonitor() : 0L), (long)0L);
        if ($$6 != null) {
            VideoMode $$7 = $$6.getPreferredVidMode(this.fullscreen ? this.preferredFullscreenVideoMode : Optional.empty());
            this.windowedX = this.x = $$6.getX() + $$7.getWidth() / 2 - this.width / 2;
            this.windowedY = this.y = $$6.getY() + $$7.getHeight() / 2 - this.height / 2;
        } else {
            int[] $$8 = new int[1];
            int[] $$9 = new int[1];
            GLFW.glfwGetWindowPos((long)this.window, (int[])$$8, (int[])$$9);
            this.windowedX = this.x = $$8[0];
            this.windowedY = this.y = $$9[0];
        }
        this.setMode();
        this.refreshFramebufferSize();
        GLFW.glfwSetFramebufferSizeCallback((long)this.window, this::onFramebufferResize);
        GLFW.glfwSetWindowPosCallback((long)this.window, this::onMove);
        GLFW.glfwSetWindowSizeCallback((long)this.window, this::onResize);
        GLFW.glfwSetWindowFocusCallback((long)this.window, this::onFocus);
        GLFW.glfwSetCursorEnterCallback((long)this.window, this::onEnter);
        GLFW.glfwSetWindowIconifyCallback((long)this.window, this::onIconify);
    }

    public static String getPlatform() {
        int $$0 = GLFW.glfwGetPlatform();
        return switch ($$0) {
            case 0 -> "<error>";
            case 393217 -> "win32";
            case 393218 -> "cocoa";
            case 393219 -> "wayland";
            case 393220 -> "x11";
            case 393221 -> "null";
            default -> String.format(Locale.ROOT, "unknown (%08X)", $$0);
        };
    }

    public int getRefreshRate() {
        RenderSystem.assertOnRenderThread();
        return GLX._getRefreshRate(this);
    }

    public boolean shouldClose() {
        return GLX._shouldClose(this);
    }

    public static void checkGlfwError(BiConsumer<Integer, String> $$0) {
        try (MemoryStack $$1 = MemoryStack.stackPush();){
            PointerBuffer $$2 = $$1.mallocPointer(1);
            int $$3 = GLFW.glfwGetError((PointerBuffer)$$2);
            if ($$3 != 0) {
                long $$4 = $$2.get();
                String $$5 = $$4 == 0L ? "" : MemoryUtil.memUTF8((long)$$4);
                $$0.accept($$3, $$5);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setIcon(PackResources $$0, IconSet $$1) throws IOException {
        int $$2 = GLFW.glfwGetPlatform();
        switch ($$2) {
            case 393217: 
            case 393220: {
                List<IoSupplier<InputStream>> $$3 = $$1.getStandardIcons($$0);
                ArrayList<ByteBuffer> $$4 = new ArrayList<ByteBuffer>($$3.size());
                try (MemoryStack $$5 = MemoryStack.stackPush();){
                    GLFWImage.Buffer $$6 = GLFWImage.malloc((int)$$3.size(), (MemoryStack)$$5);
                    for (int $$7 = 0; $$7 < $$3.size(); ++$$7) {
                        try (NativeImage $$8 = NativeImage.read($$3.get($$7).get());){
                            ByteBuffer $$9 = MemoryUtil.memAlloc((int)($$8.getWidth() * $$8.getHeight() * 4));
                            $$4.add($$9);
                            $$9.asIntBuffer().put($$8.d());
                            $$6.position($$7);
                            $$6.width($$8.getWidth());
                            $$6.height($$8.getHeight());
                            $$6.pixels($$9);
                            continue;
                        }
                    }
                    GLFW.glfwSetWindowIcon((long)this.window, (GLFWImage.Buffer)((GLFWImage.Buffer)$$6.position(0)));
                    break;
                } finally {
                    $$4.forEach(MemoryUtil::memFree);
                }
            }
            case 393218: {
                MacosUtil.loadIcon($$1.getMacIcon($$0));
                break;
            }
            case 393219: 
            case 393221: {
                break;
            }
            default: {
                LOGGER.warn("Not setting icon for unrecognized platform: {}", (Object)$$2);
            }
        }
    }

    public void setErrorSection(String $$0) {
        this.errorSection = $$0;
    }

    private void setBootErrorCallback() {
        GLFW.glfwSetErrorCallback(Window::bootCrash);
    }

    private static void bootCrash(int $$0, long $$1) {
        String $$2 = "GLFW error " + $$0 + ": " + MemoryUtil.memUTF8((long)$$1);
        TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)($$2 + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions)."), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        throw new WindowInitFailed($$2);
    }

    public void defaultErrorCallback(int $$0, long $$1) {
        RenderSystem.assertOnRenderThread();
        String $$2 = MemoryUtil.memUTF8((long)$$1);
        LOGGER.error("########## GL ERROR ##########");
        LOGGER.error("@ {}", (Object)this.errorSection);
        LOGGER.error("{}: {}", (Object)$$0, (Object)$$2);
    }

    public void setDefaultErrorCallback() {
        GLFWErrorCallback $$0 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)this.defaultErrorCallback);
        if ($$0 != null) {
            $$0.free();
        }
    }

    public void updateVsync(boolean $$0) {
        RenderSystem.assertOnRenderThread();
        this.vsync = $$0;
        GLFW.glfwSwapInterval((int)($$0 ? 1 : 0));
    }

    @Override
    public void close() {
        RenderSystem.assertOnRenderThread();
        Callbacks.glfwFreeCallbacks((long)this.window);
        this.defaultErrorCallback.close();
        GLFW.glfwDestroyWindow((long)this.window);
        GLFW.glfwTerminate();
    }

    private void onMove(long $$0, int $$1, int $$2) {
        this.x = $$1;
        this.y = $$2;
    }

    private void onFramebufferResize(long $$0, int $$1, int $$2) {
        if ($$0 != this.window) {
            return;
        }
        int $$3 = this.getWidth();
        int $$4 = this.getHeight();
        if ($$1 == 0 || $$2 == 0) {
            this.minimized = true;
            return;
        }
        this.minimized = false;
        this.framebufferWidth = $$1;
        this.framebufferHeight = $$2;
        if (this.getWidth() != $$3 || this.getHeight() != $$4) {
            try {
                this.eventHandler.resizeDisplay();
            } catch (Exception $$5) {
                CrashReport $$6 = CrashReport.forThrowable($$5, "Window resize");
                CrashReportCategory $$7 = $$6.addCategory("Window Dimensions");
                $$7.setDetail("Old", $$3 + "x" + $$4);
                $$7.setDetail("New", $$1 + "x" + $$2);
                throw new ReportedException($$6);
            }
        }
    }

    private void refreshFramebufferSize() {
        int[] $$0 = new int[1];
        int[] $$1 = new int[1];
        GLFW.glfwGetFramebufferSize((long)this.window, (int[])$$0, (int[])$$1);
        this.framebufferWidth = $$0[0] > 0 ? $$0[0] : 1;
        this.framebufferHeight = $$1[0] > 0 ? $$1[0] : 1;
    }

    private void onResize(long $$0, int $$1, int $$2) {
        this.width = $$1;
        this.height = $$2;
    }

    private void onFocus(long $$0, boolean $$1) {
        if ($$0 == this.window) {
            this.eventHandler.setWindowActive($$1);
        }
    }

    private void onEnter(long $$0, boolean $$1) {
        if ($$1) {
            this.eventHandler.cursorEntered();
        }
    }

    private void onIconify(long $$0, boolean $$1) {
        this.iconified = $$1;
    }

    public void updateDisplay(@Nullable TracyFrameCapture $$0) {
        RenderSystem.flipFrame(this.window, $$0);
        if (this.fullscreen != this.actuallyFullscreen) {
            this.actuallyFullscreen = this.fullscreen;
            this.updateFullscreen(this.vsync, $$0);
        }
    }

    public Optional<VideoMode> getPreferredFullscreenVideoMode() {
        return this.preferredFullscreenVideoMode;
    }

    public void setPreferredFullscreenVideoMode(Optional<VideoMode> $$0) {
        boolean $$1 = !$$0.equals(this.preferredFullscreenVideoMode);
        this.preferredFullscreenVideoMode = $$0;
        if ($$1) {
            this.dirty = true;
        }
    }

    public void changeFullscreenVideoMode() {
        if (this.fullscreen && this.dirty) {
            this.dirty = false;
            this.setMode();
            this.eventHandler.resizeDisplay();
        }
    }

    private void setMode() {
        boolean $$0;
        boolean bl = $$0 = GLFW.glfwGetWindowMonitor((long)this.window) != 0L;
        if (this.fullscreen) {
            Monitor $$1 = this.screenManager.findBestMonitor(this);
            if ($$1 == null) {
                LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
                this.fullscreen = false;
            } else {
                if (MacosUtil.IS_MACOS) {
                    MacosUtil.exitNativeFullscreen(this.window);
                }
                VideoMode $$2 = $$1.getPreferredVidMode(this.preferredFullscreenVideoMode);
                if (!$$0) {
                    this.windowedX = this.x;
                    this.windowedY = this.y;
                    this.windowedWidth = this.width;
                    this.windowedHeight = this.height;
                }
                this.x = 0;
                this.y = 0;
                this.width = $$2.getWidth();
                this.height = $$2.getHeight();
                GLFW.glfwSetWindowMonitor((long)this.window, (long)$$1.getMonitor(), (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)$$2.getRefreshRate());
                if (MacosUtil.IS_MACOS) {
                    MacosUtil.clearResizableBit(this.window);
                }
            }
        } else {
            this.x = this.windowedX;
            this.y = this.windowedY;
            this.width = this.windowedWidth;
            this.height = this.windowedHeight;
            GLFW.glfwSetWindowMonitor((long)this.window, (long)0L, (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)-1);
        }
    }

    public void toggleFullScreen() {
        this.fullscreen = !this.fullscreen;
    }

    public void setWindowed(int $$0, int $$1) {
        this.windowedWidth = $$0;
        this.windowedHeight = $$1;
        this.fullscreen = false;
        this.setMode();
    }

    private void updateFullscreen(boolean $$0, @Nullable TracyFrameCapture $$1) {
        RenderSystem.assertOnRenderThread();
        try {
            this.setMode();
            this.eventHandler.resizeDisplay();
            this.updateVsync($$0);
            this.updateDisplay($$1);
        } catch (Exception $$2) {
            LOGGER.error("Couldn't toggle fullscreen", $$2);
        }
    }

    public int calculateScale(int $$0, boolean $$1) {
        int $$2;
        for ($$2 = 1; $$2 != $$0 && $$2 < this.framebufferWidth && $$2 < this.framebufferHeight && this.framebufferWidth / ($$2 + 1) >= 320 && this.framebufferHeight / ($$2 + 1) >= 240; ++$$2) {
        }
        if ($$1 && $$2 % 2 != 0) {
            ++$$2;
        }
        return $$2;
    }

    public void setGuiScale(int $$0) {
        this.guiScale = $$0;
        double $$1 = $$0;
        int $$2 = (int)((double)this.framebufferWidth / $$1);
        this.guiScaledWidth = (double)this.framebufferWidth / $$1 > (double)$$2 ? $$2 + 1 : $$2;
        int $$3 = (int)((double)this.framebufferHeight / $$1);
        this.guiScaledHeight = (double)this.framebufferHeight / $$1 > (double)$$3 ? $$3 + 1 : $$3;
    }

    public void setTitle(String $$0) {
        GLFW.glfwSetWindowTitle((long)this.window, (CharSequence)$$0);
    }

    public long getWindow() {
        return this.window;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public boolean isIconified() {
        return this.iconified;
    }

    public int getWidth() {
        return this.framebufferWidth;
    }

    public int getHeight() {
        return this.framebufferHeight;
    }

    public void setWidth(int $$0) {
        this.framebufferWidth = $$0;
    }

    public void setHeight(int $$0) {
        this.framebufferHeight = $$0;
    }

    public int getScreenWidth() {
        return this.width;
    }

    public int getScreenHeight() {
        return this.height;
    }

    public int getGuiScaledWidth() {
        return this.guiScaledWidth;
    }

    public int getGuiScaledHeight() {
        return this.guiScaledHeight;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getGuiScale() {
        return this.guiScale;
    }

    @Nullable
    public Monitor findBestMonitor() {
        return this.screenManager.findBestMonitor(this);
    }

    public void updateRawMouseInput(boolean $$0) {
        InputConstants.updateRawMouseInput(this.window, $$0);
    }

    public void setWindowCloseCallback(Runnable $$0) {
        GLFWWindowCloseCallback $$12 = GLFW.glfwSetWindowCloseCallback((long)this.window, $$1 -> $$0.run());
        if ($$12 != null) {
            $$12.free();
        }
    }

    public boolean isMinimized() {
        return this.minimized;
    }

    public static class WindowInitFailed
    extends SilentInitException {
        WindowInitFailed(String $$0) {
            super($$0);
        }
    }
}

