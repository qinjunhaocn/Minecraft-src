/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class HugeFungusConfiguration
implements FeatureConfiguration {
    public static final Codec<HugeFungusConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockState.CODEC.fieldOf("valid_base_block").forGetter($$0 -> $$0.validBaseState), (App)BlockState.CODEC.fieldOf("stem_state").forGetter($$0 -> $$0.stemState), (App)BlockState.CODEC.fieldOf("hat_state").forGetter($$0 -> $$0.hatState), (App)BlockState.CODEC.fieldOf("decor_state").forGetter($$0 -> $$0.decorState), (App)BlockPredicate.CODEC.fieldOf("replaceable_blocks").forGetter($$0 -> $$0.replaceableBlocks), (App)Codec.BOOL.fieldOf("planted").orElse((Object)false).forGetter($$0 -> $$0.planted)).apply((Applicative)$$02, HugeFungusConfiguration::new));
    public final BlockState validBaseState;
    public final BlockState stemState;
    public final BlockState hatState;
    public final BlockState decorState;
    public final BlockPredicate replaceableBlocks;
    public final boolean planted;

    public HugeFungusConfiguration(BlockState $$0, BlockState $$1, BlockState $$2, BlockState $$3, BlockPredicate $$4, boolean $$5) {
        this.validBaseState = $$0;
        this.stemState = $$1;
        this.hatState = $$2;
        this.decorState = $$3;
        this.replaceableBlocks = $$4;
        this.planted = $$5;
    }
}

