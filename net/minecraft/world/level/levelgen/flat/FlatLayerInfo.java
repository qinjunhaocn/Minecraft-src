/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.flat;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

public class FlatLayerInfo {
    public static final Codec<FlatLayerInfo> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.intRange((int)0, (int)DimensionType.Y_SIZE).fieldOf("height").forGetter(FlatLayerInfo::getHeight), (App)BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").orElse((Object)Blocks.AIR).forGetter($$0 -> $$0.getBlockState().getBlock())).apply((Applicative)$$02, FlatLayerInfo::new));
    private final Block block;
    private final int height;

    public FlatLayerInfo(int $$0, Block $$1) {
        this.height = $$0;
        this.block = $$1;
    }

    public int getHeight() {
        return this.height;
    }

    public BlockState getBlockState() {
        return this.block.defaultBlockState();
    }

    public FlatLayerInfo heightLimited(int $$0) {
        if (this.height > $$0) {
            return new FlatLayerInfo($$0, this.block);
        }
        return this;
    }

    public String toString() {
        return (String)(this.height != 1 ? this.height + "*" : "") + String.valueOf(BuiltInRegistries.BLOCK.getKey(this.block));
    }
}

