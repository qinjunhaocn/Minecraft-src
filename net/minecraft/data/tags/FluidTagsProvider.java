/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidTagsProvider
extends IntrinsicHolderTagsProvider<Fluid> {
    public FluidTagsProvider(PackOutput $$02, CompletableFuture<HolderLookup.Provider> $$1) {
        super($$02, Registries.FLUID, $$1, (T $$0) -> $$0.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider $$0) {
        this.tag(FluidTags.WATER).a((Fluid[])new Fluid[]{Fluids.WATER, Fluids.FLOWING_WATER});
        this.tag(FluidTags.LAVA).a((Fluid[])new Fluid[]{Fluids.LAVA, Fluids.FLOWING_LAVA});
    }
}

