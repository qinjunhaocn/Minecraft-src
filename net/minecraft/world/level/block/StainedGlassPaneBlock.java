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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class StainedGlassPaneBlock
extends IronBarsBlock
implements BeaconBeamBlock {
    public static final MapCodec<StainedGlassPaneBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DyeColor.CODEC.fieldOf("color").forGetter(StainedGlassPaneBlock::getColor), StainedGlassPaneBlock.propertiesCodec()).apply((Applicative)$$0, StainedGlassPaneBlock::new));
    private final DyeColor color;

    public MapCodec<StainedGlassPaneBlock> codec() {
        return CODEC;
    }

    public StainedGlassPaneBlock(DyeColor $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.color = $$0;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(WATERLOGGED, false));
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}

