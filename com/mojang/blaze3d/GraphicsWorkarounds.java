/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.mojang.blaze3d;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public class GraphicsWorkarounds {
    private static final List<String> INTEL_GEN11_CORE = List.of((Object[])new String[]{"i3-1000g1", "i3-1000g4", "i3-1000ng4", "i3-1005g1", "i3-l13g4", "i5-1030g4", "i5-1030g7", "i5-1030ng7", "i5-1034g1", "i5-1035g1", "i5-1035g4", "i5-1035g7", "i5-1038ng7", "i5-l16g7", "i7-1060g7", "i7-1060ng7", "i7-1065g7", "i7-1068g7", "i7-1068ng7"});
    private static final List<String> INTEL_GEN11_ATOM = List.of((Object)"x6211e", (Object)"x6212re", (Object)"x6214re", (Object)"x6413e", (Object)"x6414re", (Object)"x6416re", (Object)"x6425e", (Object)"x6425re", (Object)"x6427fe");
    private static final List<String> INTEL_GEN11_CELERON = List.of((Object)"j6412", (Object)"j6413", (Object)"n4500", (Object)"n4505", (Object)"n5095", (Object)"n5095a", (Object)"n5100", (Object)"n5105", (Object)"n6210", (Object)"n6211");
    private static final List<String> INTEL_GEN11_PENTIUM = List.of((Object)"6805", (Object)"j6426", (Object)"n6415", (Object)"n6000", (Object)"n6005");
    @Nullable
    private static GraphicsWorkarounds instance;
    private final WeakReference<GpuDevice> gpuDevice;
    private final boolean alwaysCreateFreshImmediateBuffer;

    private GraphicsWorkarounds(GpuDevice $$0) {
        this.gpuDevice = new WeakReference<GpuDevice>($$0);
        this.alwaysCreateFreshImmediateBuffer = GraphicsWorkarounds.isIntelGen11($$0);
    }

    public static GraphicsWorkarounds get(GpuDevice $$0) {
        GraphicsWorkarounds $$1 = instance;
        if ($$1 == null || $$1.gpuDevice.get() != $$0) {
            instance = $$1 = new GraphicsWorkarounds($$0);
        }
        return $$1;
    }

    public boolean alwaysCreateFreshImmediateBuffer() {
        return this.alwaysCreateFreshImmediateBuffer;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean isIntelGen11(GpuDevice $$0) {
        String $$1 = GLX._getCpuInfo().toLowerCase(Locale.ROOT);
        String $$2 = $$0.getRenderer().toLowerCase(Locale.ROOT);
        if (!$$1.contains("intel")) return false;
        if (!$$2.contains("intel")) return false;
        if ($$2.contains("mesa")) {
            return false;
        }
        if ($$2.endsWith("gen11")) {
            return true;
        }
        if (!$$2.contains("uhd graphics") && !$$2.contains("iris")) {
            return false;
        }
        if ($$1.contains("atom")) {
            if (INTEL_GEN11_ATOM.stream().anyMatch($$1::contains)) return true;
        }
        if ($$1.contains("celeron")) {
            if (INTEL_GEN11_CELERON.stream().anyMatch($$1::contains)) return true;
        }
        if ($$1.contains("pentium")) {
            if (INTEL_GEN11_PENTIUM.stream().anyMatch($$1::contains)) return true;
        }
        if (!INTEL_GEN11_CORE.stream().anyMatch($$1::contains)) return false;
        return true;
    }
}

