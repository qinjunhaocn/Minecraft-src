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
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

public class FallenTreeConfiguration
implements FeatureConfiguration {
    public static final Codec<FallenTreeConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter($$0 -> $$0.trunkProvider), (App)IntProvider.codec(0, 16).fieldOf("log_length").forGetter($$0 -> $$0.logLength), (App)TreeDecorator.CODEC.listOf().fieldOf("stump_decorators").forGetter($$0 -> $$0.stumpDecorators), (App)TreeDecorator.CODEC.listOf().fieldOf("log_decorators").forGetter($$0 -> $$0.logDecorators)).apply((Applicative)$$02, FallenTreeConfiguration::new));
    public final BlockStateProvider trunkProvider;
    public final IntProvider logLength;
    public final List<TreeDecorator> stumpDecorators;
    public final List<TreeDecorator> logDecorators;

    protected FallenTreeConfiguration(BlockStateProvider $$0, IntProvider $$1, List<TreeDecorator> $$2, List<TreeDecorator> $$3) {
        this.trunkProvider = $$0;
        this.logLength = $$1;
        this.stumpDecorators = $$2;
        this.logDecorators = $$3;
    }

    public static class FallenTreeConfigurationBuilder {
        private final BlockStateProvider trunkProvider;
        private final IntProvider logLength;
        private List<TreeDecorator> stumpDecorators = new ArrayList<TreeDecorator>();
        private List<TreeDecorator> logDecorators = new ArrayList<TreeDecorator>();

        public FallenTreeConfigurationBuilder(BlockStateProvider $$0, IntProvider $$1) {
            this.trunkProvider = $$0;
            this.logLength = $$1;
        }

        public FallenTreeConfigurationBuilder stumpDecorators(List<TreeDecorator> $$0) {
            this.stumpDecorators = $$0;
            return this;
        }

        public FallenTreeConfigurationBuilder logDecorators(List<TreeDecorator> $$0) {
            this.logDecorators = $$0;
            return this;
        }

        public FallenTreeConfiguration build() {
            return new FallenTreeConfiguration(this.trunkProvider, this.logLength, this.stumpDecorators, this.logDecorators);
        }
    }
}

