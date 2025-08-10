/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  ca.weblite.objc.Client
 *  ca.weblite.objc.NSObject
 *  com.sun.jna.Pointer
 *  org.lwjgl.glfw.GLFWNativeCocoa
 */
package com.mojang.blaze3d.platform;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.server.packs.resources.IoSupplier;
import org.lwjgl.glfw.GLFWNativeCocoa;

public class MacosUtil {
    public static final boolean IS_MACOS = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");
    private static final int NS_RESIZABLE_WINDOW_MASK = 8;
    private static final int NS_FULL_SCREEN_WINDOW_MASK = 16384;

    public static void exitNativeFullscreen(long $$0) {
        MacosUtil.getNsWindow($$0).filter(MacosUtil::isInNativeFullscreen).ifPresent(MacosUtil::toggleNativeFullscreen);
    }

    public static void clearResizableBit(long $$02) {
        MacosUtil.getNsWindow($$02).ifPresent($$0 -> {
            long $$1 = MacosUtil.getStyleMask($$0);
            $$0.send("setStyleMask:", new Object[]{$$1 & 0xFFFFFFFFFFFFFFF7L});
        });
    }

    private static Optional<NSObject> getNsWindow(long $$0) {
        long $$1 = GLFWNativeCocoa.glfwGetCocoaWindow((long)$$0);
        if ($$1 != 0L) {
            return Optional.of(new NSObject(new Pointer($$1)));
        }
        return Optional.empty();
    }

    private static boolean isInNativeFullscreen(NSObject $$0) {
        return (MacosUtil.getStyleMask($$0) & 0x4000L) != 0L;
    }

    private static long getStyleMask(NSObject $$0) {
        return (Long)$$0.sendRaw("styleMask", new Object[0]);
    }

    private static void toggleNativeFullscreen(NSObject $$0) {
        $$0.send("toggleFullScreen:", new Object[]{Pointer.NULL});
    }

    public static void loadIcon(IoSupplier<InputStream> $$0) throws IOException {
        try (InputStream $$1 = $$0.get();){
            String $$2 = Base64.getEncoder().encodeToString($$1.readAllBytes());
            Client $$3 = Client.getInstance();
            Object $$4 = $$3.sendProxy("NSData", "alloc", new Object[0]).send("initWithBase64Encoding:", new Object[]{$$2});
            Object $$5 = $$3.sendProxy("NSImage", "alloc", new Object[0]).send("initWithData:", new Object[]{$$4});
            $$3.sendProxy("NSApplication", "sharedApplication", new Object[0]).send("setApplicationIconImage:", new Object[]{$$5});
        }
    }
}

