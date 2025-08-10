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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public class EndGatewayBlock
extends BaseEntityBlock
implements Portal {
    public static final MapCodec<EndGatewayBlock> CODEC = EndGatewayBlock.simpleCodec(EndGatewayBlock::new);

    public MapCodec<EndGatewayBlock> codec() {
        return CODEC;
    }

    protected EndGatewayBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new TheEndGatewayBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return EndGatewayBlock.createTickerHelper($$2, BlockEntityType.END_GATEWAY, $$0.isClientSide ? TheEndGatewayBlockEntity::beamAnimationTick : TheEndGatewayBlockEntity::portalTick);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        BlockEntity $$4 = $$1.getBlockEntity($$2);
        if (!($$4 instanceof TheEndGatewayBlockEntity)) {
            return;
        }
        int $$5 = ((TheEndGatewayBlockEntity)$$4).getParticleAmount();
        for (int $$6 = 0; $$6 < $$5; ++$$6) {
            double $$7 = (double)$$2.getX() + $$3.nextDouble();
            double $$8 = (double)$$2.getY() + $$3.nextDouble();
            double $$9 = (double)$$2.getZ() + $$3.nextDouble();
            double $$10 = ($$3.nextDouble() - 0.5) * 0.5;
            double $$11 = ($$3.nextDouble() - 0.5) * 0.5;
            double $$12 = ($$3.nextDouble() - 0.5) * 0.5;
            int $$13 = $$3.nextInt(2) * 2 - 1;
            if ($$3.nextBoolean()) {
                $$9 = (double)$$2.getZ() + 0.5 + 0.25 * (double)$$13;
                $$12 = $$3.nextFloat() * 2.0f * (float)$$13;
            } else {
                $$7 = (double)$$2.getX() + 0.5 + 0.25 * (double)$$13;
                $$10 = $$3.nextFloat() * 2.0f * (float)$$13;
            }
            $$1.addParticle(ParticleTypes.PORTAL, $$7, $$8, $$9, $$10, $$11, $$12);
        }
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
    protected void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3, InsideBlockEffectApplier $$4) {
        if ($$3.canUsePortal(false)) {
            TheEndGatewayBlockEntity $$6;
            BlockEntity $$5 = $$1.getBlockEntity($$2);
            if (!$$1.isClientSide && $$5 instanceof TheEndGatewayBlockEntity && !($$6 = (TheEndGatewayBlockEntity)$$5).isCoolingDown()) {
                $$3.setAsInsidePortal(this, $$2);
                TheEndGatewayBlockEntity.triggerCooldown($$1, $$2, $$0, $$6);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @Nullable
    public TeleportTransition getPortalDestination(ServerLevel $$0, Entity $$1, BlockPos $$2) {
        void $$5;
        BlockEntity $$3 = $$0.getBlockEntity($$2);
        if (!($$3 instanceof TheEndGatewayBlockEntity)) {
            return null;
        }
        TheEndGatewayBlockEntity $$4 = (TheEndGatewayBlockEntity)$$3;
        Vec3 $$6 = $$5.getPortalPosition($$0, $$2);
        if ($$6 == null) {
            return null;
        }
        if ($$1 instanceof ThrownEnderpearl) {
            return new TeleportTransition($$0, $$6, Vec3.ZERO, 0.0f, 0.0f, Set.of(), TeleportTransition.PLACE_PORTAL_TICKET);
        }
        return new TeleportTransition($$0, $$6, Vec3.ZERO, 0.0f, 0.0f, Relative.a(Relative.DELTA, Relative.ROTATION), TeleportTransition.PLACE_PORTAL_TICKET);
    }

    @Override
    protected RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.INVISIBLE;
    }
}

