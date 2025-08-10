/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

public class CalibratedSculkSensorBlockEntity
extends SculkSensorBlockEntity {
    public CalibratedSculkSensorBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.CALIBRATED_SCULK_SENSOR, $$0, $$1);
    }

    @Override
    public VibrationSystem.User createVibrationUser() {
        return new VibrationUser(this.getBlockPos());
    }

    protected class VibrationUser
    extends SculkSensorBlockEntity.VibrationUser {
        public VibrationUser(BlockPos $$1) {
            super(CalibratedSculkSensorBlockEntity.this, $$1);
        }

        @Override
        public int getListenerRadius() {
            return 16;
        }

        @Override
        public boolean canReceiveVibration(ServerLevel $$0, BlockPos $$1, Holder<GameEvent> $$2, @Nullable GameEvent.Context $$3) {
            int $$4 = this.getBackSignal($$0, this.blockPos, CalibratedSculkSensorBlockEntity.this.getBlockState());
            if ($$4 != 0 && VibrationSystem.getGameEventFrequency($$2) != $$4) {
                return false;
            }
            return super.canReceiveVibration($$0, $$1, $$2, $$3);
        }

        private int getBackSignal(Level $$0, BlockPos $$1, BlockState $$2) {
            Direction $$3 = $$2.getValue(CalibratedSculkSensorBlock.FACING).getOpposite();
            return $$0.getSignal($$1.relative($$3), $$3);
        }
    }
}

