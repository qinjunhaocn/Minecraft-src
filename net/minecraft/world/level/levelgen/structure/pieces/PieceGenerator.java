/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@FunctionalInterface
public interface PieceGenerator<C extends FeatureConfiguration> {
    public void generatePieces(StructurePiecesBuilder var1, Context<C> var2);

    public record Context<C extends FeatureConfiguration>(C config, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, ChunkPos chunkPos, LevelHeightAccessor heightAccessor, WorldgenRandom random, long seed) {
    }
}

