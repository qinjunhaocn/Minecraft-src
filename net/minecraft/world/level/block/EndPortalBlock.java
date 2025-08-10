/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalBlock
extends BaseEntityBlock
implements Portal {
    public static final MapCodec<EndPortalBlock> CODEC = EndPortalBlock.simpleCodec(EndPortalBlock::new);
    private static final VoxelShape SHAPE = Block.column(16.0, 6.0, 12.0);

    public MapCodec<EndPortalBlock> codec() {
        return CODEC;
    }

    protected EndPortalBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new TheEndPortalBlockEntity($$0, $$1);
    }

    @Override
    protected VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, Entity $$3) {
        return $$0.getShape($$1, $$2);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if (!$$3.canUsePortal(false)) return;
        if (!$$1.isClientSide && $$1.dimension() == Level.END && $$3 instanceof ServerPlayer) {
            ServerPlayer $$5 = (ServerPlayer)$$3;
            if (!$$5.seenCredits) {
                $$5.showEndCredits();
                return;
            }
        }
        $$3.setAsInsidePortal(this, $$2);
    }

    @Override
    @Nullable
    public TeleportTransition getPortalDestination(ServerLevel $$0, Entity $$1, BlockPos $$2) {
        Set<Relative> $$11;
        float $$10;
        ResourceKey<Level> $$3 = $$0.dimension() == Level.END ? Level.OVERWORLD : Level.END;
        ServerLevel $$4 = $$0.getServer().getLevel($$3);
        if ($$4 == null) {
            return null;
        }
        boolean $$5 = $$3 == Level.END;
        BlockPos $$6 = $$5 ? ServerLevel.END_SPAWN_POINT : $$4.getSharedSpawnPos();
        Vec3 $$7 = $$6.getBottomCenter();
        if ($$5) {
            EndPlatformFeature.createEndPlatform($$4, BlockPos.containing($$7).below(), true);
            float $$8 = Direction.WEST.toYRot();
            Set<Relative> $$9 = Relative.a(Relative.DELTA, Set.of((Object)((Object)Relative.X_ROT)));
            if ($$1 instanceof ServerPlayer) {
                $$7 = $$7.subtract(0.0, 1.0, 0.0);
            }
        } else {
            $$10 = $$4.getSharedSpawnAngle();
            $$11 = Relative.a(Relative.DELTA, Relative.ROTATION);
            if ($$1 instanceof ServerPlayer) {
                ServerPlayer $$12 = (ServerPlayer)$$1;
                return $$12.findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING);
            }
            $$7 = $$1.adjustSpawnLocation($$4, $$6).getBottomCenter();
        }
        return new TeleportTransition($$4, $$7, Vec3.ZERO, $$10, 0.0f, $$11, TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET));
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        double $$4 = (double)$$2.getX() + $$3.nextDouble();
        double $$5 = (double)$$2.getY() + 0.8;
        double $$6 = (double)$$2.getZ() + $$3.nextDouble();
        $$1.addParticle(ParticleTypes.SMOKE, $$4, $$5, $$6, 0.0, 0.0, 0.0);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean canBeReplaced(BlockState $$0, Fluid $$1) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.INVISIBLE;
    }
}

