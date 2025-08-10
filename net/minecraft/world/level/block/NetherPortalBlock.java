/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class NetherPortalBlock
extends Block
implements Portal {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<NetherPortalBlock> CODEC = NetherPortalBlock.simpleCodec(NetherPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    private static final Map<Direction.Axis, VoxelShape> SHAPES = Shapes.rotateHorizontalAxis(Block.column(4.0, 16.0, 0.0, 16.0));

    public MapCodec<NetherPortalBlock> codec() {
        return CODEC;
    }

    public NetherPortalBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPES.get($$0.getValue(AXIS));
    }

    @Override
    protected void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$1.dimensionType().natural() && $$1.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && $$3.nextInt(2000) < $$1.getDifficulty().getId() && $$1.anyPlayerCloseEnoughForSpawning($$2)) {
            ZombifiedPiglin $$4;
            while ($$1.getBlockState($$2).is(this)) {
                $$2 = $$2.below();
            }
            if ($$1.getBlockState($$2).isValidSpawn($$1, $$2, EntityType.ZOMBIFIED_PIGLIN) && ($$4 = EntityType.ZOMBIFIED_PIGLIN.spawn($$1, $$2.above(), EntitySpawnReason.STRUCTURE)) != null) {
                $$4.setPortalCooldown();
                Entity $$5 = $$4.getVehicle();
                if ($$5 != null) {
                    $$5.setPortalCooldown();
                }
            }
        }
    }

    @Override
    protected BlockState updateShape(BlockState $$0, LevelReader $$1, ScheduledTickAccess $$2, BlockPos $$3, Direction $$4, BlockPos $$5, BlockState $$6, RandomSource $$7) {
        boolean $$10;
        Direction.Axis $$8 = $$4.getAxis();
        Direction.Axis $$9 = $$0.getValue(AXIS);
        boolean bl = $$10 = $$9 != $$8 && $$8.isHorizontal();
        if ($$10 || $$6.is(this) || PortalShape.findAnyShape($$1, $$3, $$9).isComplete()) {
            return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if ($$3.canUsePortal(false)) {
            $$3.setAsInsidePortal(this, $$2);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel $$0, Entity $$1) {
        if ($$1 instanceof Player) {
            Player $$2 = (Player)$$1;
            return Math.max(0, $$0.getGameRules().getInt($$2.getAbilities().invulnerable ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY));
        }
        return 0;
    }

    @Override
    @Nullable
    public TeleportTransition getPortalDestination(ServerLevel $$0, Entity $$1, BlockPos $$2) {
        ResourceKey<Level> $$3 = $$0.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
        ServerLevel $$4 = $$0.getServer().getLevel($$3);
        if ($$4 == null) {
            return null;
        }
        boolean $$5 = $$4.dimension() == Level.NETHER;
        WorldBorder $$6 = $$4.getWorldBorder();
        double $$7 = DimensionType.getTeleportationScale($$0.dimensionType(), $$4.dimensionType());
        BlockPos $$8 = $$6.clampToBounds($$1.getX() * $$7, $$1.getY(), $$1.getZ() * $$7);
        return this.getExitPortal($$4, $$1, $$2, $$8, $$5, $$6);
    }

    @Nullable
    private TeleportTransition getExitPortal(ServerLevel $$0, Entity $$12, BlockPos $$22, BlockPos $$3, boolean $$4, WorldBorder $$5) {
        TeleportTransition.PostTeleportTransition $$14;
        BlockUtil.FoundRectangle $$13;
        Optional<BlockPos> $$6 = $$0.getPortalForcer().findClosestPortalPosition($$3, $$4, $$5);
        if ($$6.isPresent()) {
            BlockPos $$7 = $$6.get();
            BlockState $$8 = $$0.getBlockState($$7);
            BlockUtil.FoundRectangle $$9 = BlockUtil.getLargestRectangleAround($$7, $$8.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, $$2 -> $$0.getBlockState((BlockPos)$$2) == $$8);
            TeleportTransition.PostTeleportTransition $$10 = TeleportTransition.PLAY_PORTAL_SOUND.then($$1 -> $$1.placePortalTicket($$7));
        } else {
            Direction.Axis $$11 = $$12.level().getBlockState($$22).getOptionalValue(AXIS).orElse(Direction.Axis.X);
            Optional<BlockUtil.FoundRectangle> $$122 = $$0.getPortalForcer().createPortal($$3, $$11);
            if ($$122.isEmpty()) {
                LOGGER.error("Unable to create a portal, likely target out of worldborder");
                return null;
            }
            $$13 = $$122.get();
            $$14 = TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET);
        }
        return NetherPortalBlock.getDimensionTransitionFromExit($$12, $$22, $$13, $$0, $$14);
    }

    private static TeleportTransition getDimensionTransitionFromExit(Entity $$0, BlockPos $$1, BlockUtil.FoundRectangle $$22, ServerLevel $$3, TeleportTransition.PostTeleportTransition $$4) {
        Vec3 $$10;
        Direction.Axis $$9;
        BlockState $$5 = $$0.level().getBlockState($$1);
        if ($$5.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            Direction.Axis $$6 = $$5.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            BlockUtil.FoundRectangle $$7 = BlockUtil.getLargestRectangleAround($$1, $$6, 21, Direction.Axis.Y, 21, $$2 -> $$0.level().getBlockState((BlockPos)$$2) == $$5);
            Vec3 $$8 = $$0.getRelativePortalPosition($$6, $$7);
        } else {
            $$9 = Direction.Axis.X;
            $$10 = new Vec3(0.5, 0.0, 0.0);
        }
        return NetherPortalBlock.createDimensionTransition($$3, $$22, $$9, $$10, $$0, $$4);
    }

    private static TeleportTransition createDimensionTransition(ServerLevel $$0, BlockUtil.FoundRectangle $$1, Direction.Axis $$2, Vec3 $$3, Entity $$4, TeleportTransition.PostTeleportTransition $$5) {
        BlockPos $$6 = $$1.minCorner;
        BlockState $$7 = $$0.getBlockState($$6);
        Direction.Axis $$8 = $$7.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
        double $$9 = $$1.axis1Size;
        double $$10 = $$1.axis2Size;
        EntityDimensions $$11 = $$4.getDimensions($$4.getPose());
        int $$12 = $$2 == $$8 ? 0 : 90;
        double $$13 = (double)$$11.width() / 2.0 + ($$9 - (double)$$11.width()) * $$3.x();
        double $$14 = ($$10 - (double)$$11.height()) * $$3.y();
        double $$15 = 0.5 + $$3.z();
        boolean $$16 = $$8 == Direction.Axis.X;
        Vec3 $$17 = new Vec3((double)$$6.getX() + ($$16 ? $$13 : $$15), (double)$$6.getY() + $$14, (double)$$6.getZ() + ($$16 ? $$15 : $$13));
        Vec3 $$18 = PortalShape.findCollisionFreePosition($$17, $$0, $$4, $$11);
        return new TeleportTransition($$0, $$18, Vec3.ZERO, $$12, 0.0f, Relative.a(Relative.DELTA, Relative.ROTATION), $$5);
    }

    @Override
    public Portal.Transition getLocalTransition() {
        return Portal.Transition.CONFUSION;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$3.nextInt(100) == 0) {
            $$1.playLocalSound((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, $$3.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int $$4 = 0; $$4 < 4; ++$$4) {
            double $$5 = (double)$$2.getX() + $$3.nextDouble();
            double $$6 = (double)$$2.getY() + $$3.nextDouble();
            double $$7 = (double)$$2.getZ() + $$3.nextDouble();
            double $$8 = ((double)$$3.nextFloat() - 0.5) * 0.5;
            double $$9 = ((double)$$3.nextFloat() - 0.5) * 0.5;
            double $$10 = ((double)$$3.nextFloat() - 0.5) * 0.5;
            int $$11 = $$3.nextInt(2) * 2 - 1;
            if ($$1.getBlockState($$2.west()).is(this) || $$1.getBlockState($$2.east()).is(this)) {
                $$7 = (double)$$2.getZ() + 0.5 + 0.25 * (double)$$11;
                $$10 = $$3.nextFloat() * 2.0f * (float)$$11;
            } else {
                $$5 = (double)$$2.getX() + 0.5 + 0.25 * (double)$$11;
                $$8 = $$3.nextFloat() * 2.0f * (float)$$11;
            }
            $$1.addParticle(ParticleTypes.PORTAL, $$5, $$6, $$7, $$8, $$9, $$10);
        }
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return ItemStack.EMPTY;
    }

    @Override
    protected BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch ($$0.getValue(AXIS)) {
                    case X: {
                        return (BlockState)$$0.setValue(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)$$0.setValue(AXIS, Direction.Axis.X);
                    }
                }
                return $$0;
            }
        }
        return $$0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.a(AXIS);
    }
}

