/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropExperienceBlock
extends Block {
    public static final MapCodec<DropExperienceBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)IntProvider.codec(0, 10).fieldOf("experience").forGetter($$0 -> $$0.xpRange), DropExperienceBlock.propertiesCodec()).apply((Applicative)$$02, DropExperienceBlock::new));
    private final IntProvider xpRange;

    public MapCodec<? extends DropExperienceBlock> codec() {
        return CODEC;
    }

    public DropExperienceBlock(IntProvider $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.xpRange = $$0;
    }

    @Override
    protected void spawnAfterBreak(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3, boolean $$4) {
        super.spawnAfterBreak($$0, $$1, $$2, $$3, $$4);
        if ($$4) {
            this.tryDropExperience($$1, $$2, $$3, this.xpRange);
        }
    }
}

