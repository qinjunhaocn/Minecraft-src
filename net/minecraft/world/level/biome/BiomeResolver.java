/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

public interface BiomeResolver {
    public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4);
}

