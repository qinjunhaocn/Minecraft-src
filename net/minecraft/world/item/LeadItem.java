/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class LeadItem
extends Item {
    public LeadItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        BlockState $$3 = $$1.getBlockState($$2 = $$0.getClickedPos());
        if ($$3.is(BlockTags.FENCES)) {
            Player $$4 = $$0.getPlayer();
            if (!$$1.isClientSide && $$4 != null) {
                return LeadItem.bindPlayerMobs($$4, $$1, $$2);
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult bindPlayerMobs(Player $$0, Level $$12, BlockPos $$2) {
        LeashFenceKnotEntity $$3 = null;
        List<Leashable> $$4 = Leashable.leashableInArea($$12, Vec3.atCenterOf($$2), $$1 -> $$1.getLeashHolder() == $$0);
        boolean $$5 = false;
        for (Leashable $$6 : $$4) {
            if ($$3 == null) {
                $$3 = LeashFenceKnotEntity.getOrCreateKnot($$12, $$2);
                $$3.playPlacementSound();
            }
            if (!$$6.canHaveALeashAttachedTo($$3)) continue;
            $$6.setLeashedTo($$3, true);
            $$5 = true;
        }
        if ($$5) {
            $$12.gameEvent(GameEvent.BLOCK_ATTACH, $$2, GameEvent.Context.of($$0));
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }
}

