/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.SectionCopy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class RenderSectionRegion
implements BlockAndTintGetter {
    public static final int RADIUS = 1;
    public static final int SIZE = 3;
    private final int minSectionX;
    private final int minSectionY;
    private final int minSectionZ;
    private final SectionCopy[] sections;
    private final Level level;

    RenderSectionRegion(Level $$0, int $$1, int $$2, int $$3, SectionCopy[] $$4) {
        this.level = $$0;
        this.minSectionX = $$1;
        this.minSectionY = $$2;
        this.minSectionZ = $$3;
        this.sections = $$4;
    }

    @Override
    public BlockState getBlockState(BlockPos $$0) {
        return this.getSection(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getY()), SectionPos.blockToSectionCoord($$0.getZ())).getBlockState($$0);
    }

    @Override
    public FluidState getFluidState(BlockPos $$0) {
        return this.getSection(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getY()), SectionPos.blockToSectionCoord($$0.getZ())).getBlockState($$0).getFluidState();
    }

    @Override
    public float getShade(Direction $$0, boolean $$1) {
        return this.level.getShade($$0, $$1);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos $$0) {
        return this.getSection(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getY()), SectionPos.blockToSectionCoord($$0.getZ())).getBlockEntity($$0);
    }

    private SectionCopy getSection(int $$0, int $$1, int $$2) {
        return this.sections[RenderSectionRegion.index(this.minSectionX, this.minSectionY, this.minSectionZ, $$0, $$1, $$2)];
    }

    @Override
    public int getBlockTint(BlockPos $$0, ColorResolver $$1) {
        return this.level.getBlockTint($$0, $$1);
    }

    @Override
    public int getMinY() {
        return this.level.getMinY();
    }

    @Override
    public int getHeight() {
        return this.level.getHeight();
    }

    public static int index(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5) {
        return $$3 - $$0 + ($$4 - $$1) * 3 + ($$5 - $$2) * 3 * 3;
    }
}

