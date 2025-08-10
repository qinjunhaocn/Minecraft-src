/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GrassColor;

public class GrassColorReloadListener
extends SimplePreparableReloadListener<int[]> {
    private static final ResourceLocation LOCATION = ResourceLocation.withDefaultNamespace("textures/colormap/grass.png");

    protected int[] a(ResourceManager $$0, ProfilerFiller $$1) {
        try {
            return LegacyStuffWrapper.a($$0, LOCATION);
        } catch (IOException $$2) {
            throw new IllegalStateException("Failed to load grass color texture", $$2);
        }
    }

    protected void a(int[] $$0, ResourceManager $$1, ProfilerFiller $$2) {
        GrassColor.a($$0);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.a(resourceManager, profilerFiller);
    }
}

