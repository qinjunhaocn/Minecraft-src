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
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredFallingBlock
extends FallingBlock {
    public static final MapCodec<ColoredFallingBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)ColorRGBA.CODEC.fieldOf("falling_dust_color").forGetter($$0 -> $$0.dustColor), ColoredFallingBlock.propertiesCodec()).apply((Applicative)$$02, ColoredFallingBlock::new));
    protected final ColorRGBA dustColor;

    public MapCodec<? extends ColoredFallingBlock> codec() {
        return CODEC;
    }

    public ColoredFallingBlock(ColorRGBA $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.dustColor = $$0;
    }

    @Override
    public int getDustColor(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return this.dustColor.rgba();
    }
}

