/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class MinecartItem
extends Item {
    private final EntityType<? extends AbstractMinecart> type;

    public MinecartItem(EntityType<? extends AbstractMinecart> $$0, Item.Properties $$1) {
        super($$1);
        this.type = $$0;
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        if (!$$3.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }
        ItemStack $$4 = $$0.getItemInHand();
        RailShape $$5 = $$3.getBlock() instanceof BaseRailBlock ? $$3.getValue(((BaseRailBlock)$$3.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
        double $$6 = 0.0;
        if ($$5.isSlope()) {
            $$6 = 0.5;
        }
        Vec3 $$7 = new Vec3((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.0625 + $$6, (double)$$2.getZ() + 0.5);
        AbstractMinecart $$8 = AbstractMinecart.createMinecart($$1, $$7.x, $$7.y, $$7.z, this.type, EntitySpawnReason.DISPENSER, $$4, $$0.getPlayer());
        if ($$8 == null) {
            return InteractionResult.FAIL;
        }
        if (AbstractMinecart.useExperimentalMovement($$1)) {
            List<Entity> $$9 = $$1.getEntities(null, $$8.getBoundingBox());
            for (Entity $$10 : $$9) {
                if (!($$10 instanceof AbstractMinecart)) continue;
                return InteractionResult.FAIL;
            }
        }
        if ($$1 instanceof ServerLevel) {
            ServerLevel $$11 = (ServerLevel)$$1;
            $$11.addFreshEntity($$8);
            $$11.gameEvent(GameEvent.ENTITY_PLACE, $$2, GameEvent.Context.of($$0.getPlayer(), $$11.getBlockState($$2.below())));
        }
        $$4.shrink(1);
        return InteractionResult.SUCCESS;
    }
}

