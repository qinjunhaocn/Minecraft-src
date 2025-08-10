/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.MatchException
 */
package net.minecraft.world.level.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PressurePlateBlock
extends BasePressurePlateBlock {
    public static final MapCodec<PressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec($$02 -> $$02.group((App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter($$0 -> $$0.type), PressurePlateBlock.propertiesCodec()).apply((Applicative)$$02, PressurePlateBlock::new));
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public MapCodec<PressurePlateBlock> codec() {
        return CODEC;
    }

    protected PressurePlateBlock(BlockSetType $$0, BlockBehaviour.Properties $$1) {
        super($$1, $$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false));
    }

    @Override
    protected int getSignalForState(BlockState $$0) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    protected BlockState setSignalForState(BlockState $$0, int $$1) {
        return (BlockState)$$0.setValue(POWERED, $$1 > 0);
    }

    @Override
    protected int getSignalStrength(Level $$0, BlockPos $$1) {
        Class<Entity> $$2 = switch (this.type.pressurePlateSensitivity()) {
            default -> throw new MatchException(null, null);
            case BlockSetType.PressurePlateSensitivity.EVERYTHING -> Entity.class;
            case BlockSetType.PressurePlateSensitivity.MOBS -> LivingEntity.class;
        };
        return PressurePlateBlock.getEntityCount($$0, TOUCH_AABB.move($$1), $$2) > 0 ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(POWERED);
    }
}

