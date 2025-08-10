/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.data.models;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;

public class WaypointStyleProvider
implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public WaypointStyleProvider(PackOutput $$0) {
        this.pathProvider = $$0.createPathProvider(PackOutput.Target.RESOURCE_PACK, "waypoint_style");
    }

    private static void bootstrap(BiConsumer<ResourceKey<WaypointStyleAsset>, WaypointStyle> $$0) {
        $$0.accept(WaypointStyleAssets.DEFAULT, new WaypointStyle(128, 332, List.of((Object)ResourceLocation.withDefaultNamespace("default_0"), (Object)ResourceLocation.withDefaultNamespace("default_1"), (Object)ResourceLocation.withDefaultNamespace("default_2"), (Object)ResourceLocation.withDefaultNamespace("default_3"))));
        $$0.accept(WaypointStyleAssets.BOWTIE, new WaypointStyle(64, 332, List.of((Object)ResourceLocation.withDefaultNamespace("bowtie"), (Object)ResourceLocation.withDefaultNamespace("default_0"), (Object)ResourceLocation.withDefaultNamespace("default_1"), (Object)ResourceLocation.withDefaultNamespace("default_2"), (Object)ResourceLocation.withDefaultNamespace("default_3"))));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        HashMap $$12 = new HashMap();
        WaypointStyleProvider.bootstrap(($$1, $$2) -> {
            if ($$12.putIfAbsent($$1, $$2) != null) {
                throw new IllegalStateException("Tried to register waypoint style twice for id: " + String.valueOf($$1));
            }
        });
        return DataProvider.saveAll($$0, WaypointStyle.CODEC, this.pathProvider::json, $$12);
    }

    @Override
    public String getName() {
        return "Waypoint Style Definitions";
    }
}

