/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BlockPileConfiguration
implements FeatureConfiguration {
    public static final Codec<BlockPileConfiguration> CODEC = BlockStateProvider.CODEC.fieldOf("state_provider").xmap(BlockPileConfiguration::new, $$0 -> $$0.stateProvider).codec();
    public final BlockStateProvider stateProvider;

    public BlockPileConfiguration(BlockStateProvider $$0) {
        this.stateProvider = $$0;
    }
}

