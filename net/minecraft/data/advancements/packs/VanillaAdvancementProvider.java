/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.advancements.packs;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.packs.VanillaAdventureAdvancements;
import net.minecraft.data.advancements.packs.VanillaHusbandryAdvancements;
import net.minecraft.data.advancements.packs.VanillaNetherAdvancements;
import net.minecraft.data.advancements.packs.VanillaStoryAdvancements;
import net.minecraft.data.advancements.packs.VanillaTheEndAdvancements;

public class VanillaAdvancementProvider {
    public static AdvancementProvider create(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        return new AdvancementProvider($$0, $$1, List.of((Object)new VanillaTheEndAdvancements(), (Object)new VanillaHusbandryAdvancements(), (Object)new VanillaAdventureAdvancements(), (Object)new VanillaNetherAdvancements(), (Object)new VanillaStoryAdvancements()));
    }
}

