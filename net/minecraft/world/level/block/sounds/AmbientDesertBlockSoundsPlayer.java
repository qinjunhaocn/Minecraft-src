/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class AmbientDesertBlockSoundsPlayer {
    private static final int IDLE_SOUND_CHANCE = 2100;
    private static final int DRY_GRASS_SOUND_CHANCE = 200;
    private static final int DEAD_BUSH_SOUND_CHANCE = 130;
    private static final int DEAD_BUSH_SOUND_BADLANDS_DECREASED_CHANCE = 3;
    private static final int SURROUNDING_BLOCKS_PLAY_SOUND_THRESHOLD = 3;
    private static final int SURROUNDING_BLOCKS_DISTANCE_HORIZONTAL_CHECK = 8;
    private static final int SURROUNDING_BLOCKS_DISTANCE_VERTICAL_CHECK = 5;
    private static final int HORIZONTAL_DIRECTIONS = 4;

    public static void playAmbientSandSounds(Level $$0, BlockPos $$1, RandomSource $$2) {
        if (!$$0.getBlockState($$1.above()).is(Blocks.AIR)) {
            return;
        }
        if ($$2.nextInt(2100) == 0 && AmbientDesertBlockSoundsPlayer.shouldPlayAmbientSandSound($$0, $$1)) {
            $$0.playLocalSound($$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.SAND_IDLE, SoundSource.AMBIENT, 1.0f, 1.0f, false);
        }
    }

    public static void playAmbientDryGrassSounds(Level $$0, BlockPos $$1, RandomSource $$2) {
        if ($$2.nextInt(200) == 0 && AmbientDesertBlockSoundsPlayer.shouldPlayDesertDryVegetationBlockSounds($$0, $$1.below())) {
            $$0.playPlayerSound(SoundEvents.DRY_GRASS, SoundSource.AMBIENT, 1.0f, 1.0f);
        }
    }

    public static void playAmbientDeadBushSounds(Level $$0, BlockPos $$1, RandomSource $$2) {
        if ($$2.nextInt(130) == 0) {
            BlockState $$3 = $$0.getBlockState($$1.below());
            if (($$3.is(Blocks.RED_SAND) || $$3.is(BlockTags.TERRACOTTA)) && $$2.nextInt(3) != 0) {
                return;
            }
            if (AmbientDesertBlockSoundsPlayer.shouldPlayDesertDryVegetationBlockSounds($$0, $$1.below())) {
                $$0.playLocalSound($$1.getX(), $$1.getY(), $$1.getZ(), SoundEvents.DEAD_BUSH_IDLE, SoundSource.AMBIENT, 1.0f, 1.0f, false);
            }
        }
    }

    public static boolean shouldPlayDesertDryVegetationBlockSounds(Level $$0, BlockPos $$1) {
        return $$0.getBlockState($$1).is(BlockTags.TRIGGERS_AMBIENT_DESERT_DRY_VEGETATION_BLOCK_SOUNDS) && $$0.getBlockState($$1.below()).is(BlockTags.TRIGGERS_AMBIENT_DESERT_DRY_VEGETATION_BLOCK_SOUNDS);
    }

    private static boolean shouldPlayAmbientSandSound(Level $$0, BlockPos $$1) {
        int $$2 = 0;
        int $$3 = 0;
        BlockPos.MutableBlockPos $$4 = $$1.mutable();
        for (Direction $$5 : Direction.Plane.HORIZONTAL) {
            int $$6;
            int $$7;
            boolean $$8;
            $$4.set($$1).move($$5, 8);
            if (AmbientDesertBlockSoundsPlayer.columnContainsTriggeringBlock($$0, $$4) && $$2++ >= 3) {
                return true;
            }
            if ($$8 = ($$7 = ($$6 = 4 - ++$$3) + $$2) >= 3) continue;
            return false;
        }
        return false;
    }

    private static boolean columnContainsTriggeringBlock(Level $$0, BlockPos.MutableBlockPos $$1) {
        int $$2 = $$0.getHeight(Heightmap.Types.WORLD_SURFACE, $$1) - 1;
        if (Math.abs($$2 - $$1.getY()) <= 5) {
            boolean $$3 = $$0.getBlockState($$1.setY($$2 + 1)).isAir();
            return $$3 && AmbientDesertBlockSoundsPlayer.canTriggerAmbientDesertSandSounds($$0.getBlockState($$1.setY($$2)));
        }
        $$1.move(Direction.UP, 6);
        BlockState $$4 = $$0.getBlockState($$1);
        $$1.move(Direction.DOWN);
        for (int $$5 = 0; $$5 < 10; ++$$5) {
            BlockState $$6 = $$0.getBlockState($$1);
            if ($$4.isAir() && AmbientDesertBlockSoundsPlayer.canTriggerAmbientDesertSandSounds($$6)) {
                return true;
            }
            $$4 = $$6;
            $$1.move(Direction.DOWN);
        }
        return false;
    }

    private static boolean canTriggerAmbientDesertSandSounds(BlockState $$0) {
        return $$0.is(BlockTags.TRIGGERS_AMBIENT_DESERT_SAND_BLOCK_SOUNDS);
    }
}

