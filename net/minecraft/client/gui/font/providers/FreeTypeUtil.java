/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.freetype.FT_Vector
 *  org.lwjgl.util.freetype.FreeType
 */
package net.minecraft.client.gui.font.providers;

import com.mojang.logging.LogUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;

public class FreeTypeUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Object LIBRARY_LOCK = new Object();
    private static long library = 0L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static long getLibrary() {
        Object object = LIBRARY_LOCK;
        synchronized (object) {
            if (library == 0L) {
                try (MemoryStack $$0 = MemoryStack.stackPush();){
                    PointerBuffer $$1 = $$0.mallocPointer(1);
                    FreeTypeUtil.assertError(FreeType.FT_Init_FreeType((PointerBuffer)$$1), "Initializing FreeType library");
                    library = $$1.get();
                }
            }
            return library;
        }
    }

    public static void assertError(int $$0, String $$1) {
        if ($$0 != 0) {
            throw new IllegalStateException("FreeType error: " + FreeTypeUtil.describeError($$0) + " (" + $$1 + ")");
        }
    }

    public static boolean checkError(int $$0, String $$1) {
        if ($$0 != 0) {
            LOGGER.error("FreeType error: {} ({})", (Object)FreeTypeUtil.describeError($$0), (Object)$$1);
            return true;
        }
        return false;
    }

    private static String describeError(int $$0) {
        String $$1 = FreeType.FT_Error_String((int)$$0);
        if ($$1 != null) {
            return $$1;
        }
        return "Unrecognized error: 0x" + Integer.toHexString($$0);
    }

    public static FT_Vector setVector(FT_Vector $$0, float $$1, float $$2) {
        long $$3 = Math.round($$1 * 64.0f);
        long $$4 = Math.round($$2 * 64.0f);
        return $$0.set($$3, $$4);
    }

    public static float x(FT_Vector $$0) {
        return (float)$$0.x() / 64.0f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void destroy() {
        Object object = LIBRARY_LOCK;
        synchronized (object) {
            if (library != 0L) {
                FreeType.FT_Done_Library((long)library);
                library = 0L;
            }
        }
    }
}

