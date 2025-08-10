/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.chunk.status;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;

public record ChunkPyramid(ImmutableList<ChunkStep> steps) {
    public static final ChunkPyramid GENERATION_PYRAMID = new Builder().step(ChunkStatus.EMPTY, $$0 -> $$0).step(ChunkStatus.STRUCTURE_STARTS, $$0 -> $$0.setTask(ChunkStatusTasks::generateStructureStarts)).step(ChunkStatus.STRUCTURE_REFERENCES, $$0 -> $$0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).setTask(ChunkStatusTasks::generateStructureReferences)).step(ChunkStatus.BIOMES, $$0 -> $$0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).setTask(ChunkStatusTasks::generateBiomes)).step(ChunkStatus.NOISE, $$0 -> $$0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.BIOMES, 1).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateNoise)).step(ChunkStatus.SURFACE, $$0 -> $$0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.BIOMES, 1).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateSurface)).step(ChunkStatus.CARVERS, $$0 -> $$0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateCarvers)).step(ChunkStatus.FEATURES, $$0 -> $$0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.CARVERS, 1).blockStateWriteRadius(1).setTask(ChunkStatusTasks::generateFeatures)).step(ChunkStatus.INITIALIZE_LIGHT, $$0 -> $$0.setTask(ChunkStatusTasks::initializeLight)).step(ChunkStatus.LIGHT, $$0 -> $$0.addRequirement(ChunkStatus.INITIALIZE_LIGHT, 1).setTask(ChunkStatusTasks::light)).step(ChunkStatus.SPAWN, $$0 -> $$0.addRequirement(ChunkStatus.BIOMES, 1).setTask(ChunkStatusTasks::generateSpawn)).step(ChunkStatus.FULL, $$0 -> $$0.setTask(ChunkStatusTasks::full)).build();
    public static final ChunkPyramid LOADING_PYRAMID = new Builder().step(ChunkStatus.EMPTY, $$0 -> $$0).step(ChunkStatus.STRUCTURE_STARTS, $$0 -> $$0.setTask(ChunkStatusTasks::loadStructureStarts)).step(ChunkStatus.STRUCTURE_REFERENCES, $$0 -> $$0).step(ChunkStatus.BIOMES, $$0 -> $$0).step(ChunkStatus.NOISE, $$0 -> $$0).step(ChunkStatus.SURFACE, $$0 -> $$0).step(ChunkStatus.CARVERS, $$0 -> $$0).step(ChunkStatus.FEATURES, $$0 -> $$0).step(ChunkStatus.INITIALIZE_LIGHT, $$0 -> $$0.setTask(ChunkStatusTasks::initializeLight)).step(ChunkStatus.LIGHT, $$0 -> $$0.addRequirement(ChunkStatus.INITIALIZE_LIGHT, 1).setTask(ChunkStatusTasks::light)).step(ChunkStatus.SPAWN, $$0 -> $$0).step(ChunkStatus.FULL, $$0 -> $$0.setTask(ChunkStatusTasks::full)).build();

    public ChunkStep getStepTo(ChunkStatus $$0) {
        return (ChunkStep)((Object)this.steps.get($$0.getIndex()));
    }

    public static class Builder {
        private final List<ChunkStep> steps = new ArrayList<ChunkStep>();

        public ChunkPyramid build() {
            return new ChunkPyramid(ImmutableList.copyOf(this.steps));
        }

        public Builder step(ChunkStatus $$0, UnaryOperator<ChunkStep.Builder> $$1) {
            ChunkStep.Builder $$3;
            if (this.steps.isEmpty()) {
                ChunkStep.Builder $$2 = new ChunkStep.Builder($$0);
            } else {
                $$3 = new ChunkStep.Builder($$0, (ChunkStep)((Object)this.steps.getLast()));
            }
            this.steps.add(((ChunkStep.Builder)$$1.apply($$3)).build());
            return this;
        }
    }
}

