/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record SimpleBlockConfiguration(BlockStateProvider toPlace, boolean scheduleTick) implements FeatureConfiguration
{
    public static final Codec<SimpleBlockConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockStateProvider.CODEC.fieldOf("to_place").forGetter($$0 -> $$0.toPlace), (App)Codec.BOOL.optionalFieldOf("schedule_tick", (Object)false).forGetter($$0 -> $$0.scheduleTick)).apply((Applicative)$$02, SimpleBlockConfiguration::new));

    public SimpleBlockConfiguration(BlockStateProvider $$0) {
        this($$0, false);
    }
}

