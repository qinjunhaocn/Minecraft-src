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
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class BrushableBlock
extends BaseEntityBlock
implements Fallable {
    public static final MapCodec<BrushableBlock> CODEC = RecordCodecBuilder.mapCodec($$0 -> $$0.group((App)BuiltInRegistries.BLOCK.byNameCodec().fieldOf("turns_into").forGetter(BrushableBlock::getTurnsInto), (App)BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_sound").forGetter(BrushableBlock::getBrushSound), (App)BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("brush_completed_sound").forGetter(BrushableBlock::getBrushCompletedSound), BrushableBlock.propertiesCodec()).apply((Applicative)$$0, BrushableBlock::new));
    private static final IntegerProperty DUSTED = BlockStateProperties.DUSTED;
    public static final int TICK_DELAY = 2;
    private final Block turnsInto;
    private final SoundEvent brushSound;
    private final SoundEvent brushCompletedSound;

    public MapCodec<BrushableBlock> codec() {
        return CODEC;
    }

    public BrushableBlock(Block $$0, SoundEvent $$1, SoundEvent $$2, BlockBehaviour.Properties $$3) {
        super($$3);
        this.turnsInto = $$0;
        this.brushSound = $$1;
        this.brushCompletedSound = $$2;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DUSTED, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(DUSTED);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        $$1.scheduleTick($$2, this, 2);
    }

    @Override
    public BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        $$2.scheduleTick($$3, this, 2);
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (blockEntity instanceof BrushableBlockEntity) {
            BrushableBlockEntity $$4 = (BrushableBlockEntity)blockEntity;
            $$4.checkReset($$1);
        }
        if (!FallingBlock.isFree($$1.getBlockState($$2.below())) || $$2.getY() < $$1.getMinY()) {
            return;
        }
        FallingBlockEntity $$5 = FallingBlockEntity.fall($$1, $$2, $$0);
        $$5.disableDrop();
    }

    @Override
    public void onBrokenAfterFall(Level $$0, BlockPos $$1, FallingBlockEntity $$2) {
        Vec3 $$3 = $$2.getBoundingBox().getCenter();
        $$0.levelEvent(2001, BlockPos.containing($$3), Block.getId($$2.getBlockState()));
        $$0.gameEvent((Entity)$$2, GameEvent.BLOCK_DESTROY, $$3);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        BlockPos $$4;
        if ($$3.nextInt(16) == 0 && FallingBlock.isFree($$1.getBlockState($$4 = $$2.below()))) {
            double $$5 = (double)$$2.getX() + $$3.nextDouble();
            double $$6 = (double)$$2.getY() - 0.05;
            double $$7 = (double)$$2.getZ() + $$3.nextDouble();
            $$1.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, $$0), $$5, $$6, $$7, 0.0, 0.0, 0.0);
        }
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new BrushableBlockEntity($$0, $$1);
    }

    public Block getTurnsInto() {
        return this.turnsInto;
    }

    public SoundEvent getBrushSound() {
        return this.brushSound;
    }

    public SoundEvent getBrushCompletedSound() {
        return this.brushCompletedSound;
    }
}

