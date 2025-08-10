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
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StainedGlassBlock
extends TransparentBlock
implements BeaconBeamBlock {
    public static final MapCodec<StainedGlassBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)DyeColor.CODEC.fieldOf("color").forGetter(StainedGlassBlock::getColor), StainedGlassBlock.propertiesCodec()).apply((Applicative)$$0, StainedGlassBlock::new));
    private final DyeColor color;

    public MapCodec<StainedGlassBlock> codec() {
        return CODEC;
    }

    public StainedGlassBlock(DyeColor $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.color = $$0;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}

