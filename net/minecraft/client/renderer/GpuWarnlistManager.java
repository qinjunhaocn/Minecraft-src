/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import org.slf4j.Logger;

public class GpuWarnlistManager
extends SimplePreparableReloadListener<Preparations> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation GPU_WARNLIST_LOCATION = ResourceLocation.withDefaultNamespace("gpu_warnlist.json");
    private ImmutableMap<String, String> warnings = ImmutableMap.of();
    private boolean showWarning;
    private boolean warningDismissed;
    private boolean skipFabulous;

    public boolean hasWarnings() {
        return !this.warnings.isEmpty();
    }

    public boolean willShowWarning() {
        return this.hasWarnings() && !this.warningDismissed;
    }

    public void showWarning() {
        this.showWarning = true;
    }

    public void dismissWarning() {
        this.warningDismissed = true;
    }

    public void dismissWarningAndSkipFabulous() {
        this.warningDismissed = true;
        this.skipFabulous = true;
    }

    public boolean isShowingWarning() {
        return this.showWarning && !this.warningDismissed;
    }

    public boolean isSkippingFabulous() {
        return this.skipFabulous;
    }

    public void resetWarnings() {
        this.showWarning = false;
        this.warningDismissed = false;
        this.skipFabulous = false;
    }

    @Nullable
    public String getRendererWarnings() {
        return this.warnings.get("renderer");
    }

    @Nullable
    public String getVersionWarnings() {
        return this.warnings.get("version");
    }

    @Nullable
    public String getVendorWarnings() {
        return this.warnings.get("vendor");
    }

    @Nullable
    public String getAllWarnings() {
        StringBuilder $$0 = new StringBuilder();
        this.warnings.forEach(($$1, $$2) -> $$0.append((String)$$1).append(": ").append((String)$$2));
        return $$0.length() == 0 ? null : $$0.toString();
    }

    @Override
    protected Preparations prepare(ResourceManager $$0, ProfilerFiller $$1) {
        ArrayList<Pattern> $$2 = Lists.newArrayList();
        ArrayList<Pattern> $$3 = Lists.newArrayList();
        ArrayList<Pattern> $$4 = Lists.newArrayList();
        JsonObject $$5 = GpuWarnlistManager.parseJson($$0, $$1);
        if ($$5 != null) {
            try (Zone $$6 = $$1.zone("compile_regex");){
                GpuWarnlistManager.compilePatterns($$5.getAsJsonArray("renderer"), $$2);
                GpuWarnlistManager.compilePatterns($$5.getAsJsonArray("version"), $$3);
                GpuWarnlistManager.compilePatterns($$5.getAsJsonArray("vendor"), $$4);
            }
        }
        return new Preparations($$2, $$3, $$4);
    }

    @Override
    protected void apply(Preparations $$0, ResourceManager $$1, ProfilerFiller $$2) {
        this.warnings = $$0.apply();
    }

    private static void compilePatterns(JsonArray $$0, List<Pattern> $$12) {
        $$0.forEach($$1 -> $$12.add(Pattern.compile($$1.getAsString(), 2)));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private static JsonObject parseJson(ResourceManager $$0, ProfilerFiller $$1) {
        try (Zone $$2 = $$1.zone("parse_json");){
            JsonObject jsonObject;
            block14: {
                BufferedReader $$3 = $$0.openAsReader(GPU_WARNLIST_LOCATION);
                try {
                    jsonObject = StrictJsonParser.parse($$3).getAsJsonObject();
                    if ($$3 == null) break block14;
                } catch (Throwable throwable) {
                    if ($$3 != null) {
                        try {
                            ((Reader)$$3).close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ((Reader)$$3).close();
            }
            return jsonObject;
        } catch (JsonSyntaxException | IOException $$4) {
            LOGGER.warn("Failed to load GPU warnlist", $$4);
            return null;
        }
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }

    protected static final class Preparations {
        private final List<Pattern> rendererPatterns;
        private final List<Pattern> versionPatterns;
        private final List<Pattern> vendorPatterns;

        Preparations(List<Pattern> $$0, List<Pattern> $$1, List<Pattern> $$2) {
            this.rendererPatterns = $$0;
            this.versionPatterns = $$1;
            this.vendorPatterns = $$2;
        }

        private static String matchAny(List<Pattern> $$0, String $$1) {
            ArrayList<String> $$2 = Lists.newArrayList();
            for (Pattern $$3 : $$0) {
                Matcher $$4 = $$3.matcher($$1);
                while ($$4.find()) {
                    $$2.add($$4.group());
                }
            }
            return String.join((CharSequence)", ", $$2);
        }

        ImmutableMap<String, String> apply() {
            ImmutableMap.Builder<String, String> $$0 = new ImmutableMap.Builder<String, String>();
            GpuDevice $$1 = RenderSystem.getDevice();
            if ($$1.getBackendName().equals("OpenGL")) {
                String $$4;
                String $$3;
                String $$2 = Preparations.matchAny(this.rendererPatterns, $$1.getRenderer());
                if (!$$2.isEmpty()) {
                    $$0.put("renderer", $$2);
                }
                if (!($$3 = Preparations.matchAny(this.versionPatterns, $$1.getVersion())).isEmpty()) {
                    $$0.put("version", $$3);
                }
                if (!($$4 = Preparations.matchAny(this.vendorPatterns, $$1.getVendor())).isEmpty()) {
                    $$0.put("vendor", $$4);
                }
            }
            return $$0.build();
        }
    }
}

