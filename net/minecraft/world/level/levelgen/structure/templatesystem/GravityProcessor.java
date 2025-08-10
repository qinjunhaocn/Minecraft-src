/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class GravityProcessor
extends StructureProcessor {
    public static final MapCodec<GravityProcessor> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)Heightmap.Types.CODEC.fieldOf("heightmap").orElse((Object)Heightmap.Types.WORLD_SURFACE_WG).forGetter($$0 -> $$0.heightmap), (App)Codec.INT.fieldOf("offset").orElse((Object)0).forGetter($$0 -> $$0.offset)).apply((Applicative)$$02, GravityProcessor::new));
    private final Heightmap.Types heightmap;
    private final int offset;

    public GravityProcessor(Heightmap.Types $$0, int $$1) {
        this.heightmap = $$0;
        this.offset = $$1;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader $$0, BlockPos $$1, BlockPos $$2, StructureTemplate.StructureBlockInfo $$3, StructureTemplate.StructureBlockInfo $$4, StructurePlaceSettings $$5) {
        Heightmap.Types $$9;
        if ($$0 instanceof ServerLevel) {
            if (this.heightmap == Heightmap.Types.WORLD_SURFACE_WG) {
                Heightmap.Types $$6 = Heightmap.Types.WORLD_SURFACE;
            } else if (this.heightmap == Heightmap.Types.OCEAN_FLOOR_WG) {
                Heightmap.Types $$7 = Heightmap.Types.OCEAN_FLOOR;
            } else {
                Heightmap.Types $$8 = this.heightmap;
            }
        } else {
            $$9 = this.heightmap;
        }
        BlockPos $$10 = $$4.pos();
        int $$11 = $$0.getHeight($$9, $$10.getX(), $$10.getZ()) + this.offset;
        int $$12 = $$3.pos().getY();
        return new StructureTemplate.StructureBlockInfo(new BlockPos($$10.getX(), $$11 + $$12, $$10.getZ()), $$4.state(), $$4.nbt());
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.GRAVITY;
    }
}

