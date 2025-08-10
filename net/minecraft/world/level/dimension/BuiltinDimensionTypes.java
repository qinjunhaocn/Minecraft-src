/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

public class BuiltinDimensionTypes {
    public static final ResourceKey<DimensionType> OVERWORLD = BuiltinDimensionTypes.register("overworld");
    public static final ResourceKey<DimensionType> NETHER = BuiltinDimensionTypes.register("the_nether");
    public static final ResourceKey<DimensionType> END = BuiltinDimensionTypes.register("the_end");
    public static final ResourceKey<DimensionType> OVERWORLD_CAVES = BuiltinDimensionTypes.register("overworld_caves");
    public static final ResourceLocation OVERWORLD_EFFECTS = ResourceLocation.withDefaultNamespace("overworld");
    public static final ResourceLocation NETHER_EFFECTS = ResourceLocation.withDefaultNamespace("the_nether");
    public static final ResourceLocation END_EFFECTS = ResourceLocation.withDefaultNamespace("the_end");

    private static ResourceKey<DimensionType> register(String $$0) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.withDefaultNamespace($$0));
    }
}

